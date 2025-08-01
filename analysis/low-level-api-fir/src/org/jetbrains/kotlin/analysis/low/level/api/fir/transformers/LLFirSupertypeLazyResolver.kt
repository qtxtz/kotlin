/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.transformers

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.targets.LLFirResolveTarget
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.targets.LLFirSingleResolveTarget
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.targets.asResolveTarget
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.tryCollectDesignation
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.llFirSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.checkTypeRefIsResolved
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.errorWithFirSpecificEntries
import org.jetbrains.kotlin.fir.FirElementWithResolveState
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.utils.isLocal
import org.jetbrains.kotlin.fir.java.declarations.FirJavaClass
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.firProvider
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.resolve.transformers.FirSupertypeResolverVisitor
import org.jetbrains.kotlin.fir.resolve.transformers.SupertypeComputationSession
import org.jetbrains.kotlin.fir.resolve.transformers.SupertypeComputationStatus
import org.jetbrains.kotlin.fir.resolve.transformers.platformSupertypeUpdater
import org.jetbrains.kotlin.fir.symbols.impl.FirClassifierSymbol
import org.jetbrains.kotlin.fir.symbols.lazyResolveToPhase
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.utils.exceptions.withFirEntry

internal object LLFirSupertypeLazyResolver : LLFirLazyResolver(FirResolvePhase.SUPER_TYPES) {
    override fun createTargetResolver(target: LLFirResolveTarget): LLFirTargetResolver = LLFirSuperTypeTargetResolver(target)

    override fun phaseSpecificCheckIsResolved(target: FirElementWithResolveState) {
        when (target) {
            is FirClass -> {
                for (superTypeRef in target.superTypeRefs) {
                    checkTypeRefIsResolved(superTypeRef, "class super type", target)
                }
            }

            is FirTypeAlias -> {
                checkTypeRefIsResolved(target.expandedTypeRef, typeRefName = "type alias expanded type", target)
            }
        }
    }
}

/**
 * This resolver is responsible for [SUPER_TYPES][FirResolvePhase.SUPER_TYPES] phase.
 *
 * This resolver:
 * - Transforms all supertypes of classes.
 * - Performs type aliases expansion.
 * - Breaks loops in the type hierarchy if needed.
 *
 * Special rules:
 * - First resolves outer classes to this phase.
 * - Resolves all super types recursively.
 * - [Searches][org.jetbrains.kotlin.analysis.low.level.api.fir.transformers.LLFirSuperTypeTargetResolver.crawlSupertype]
 *   super types not only in the declaration site session, but also in the call site session to resolve `expect` declaration first.
 *
 * @see FirSupertypeResolverVisitor
 * @see FirResolvePhase.SUPER_TYPES
 */
private class LLFirSuperTypeTargetResolver(
    target: LLFirResolveTarget,
    private val supertypeComputationSession: LLFirSupertypeComputationSession = LLFirSupertypeComputationSession(),
    private val visitedElements: MutableSet<FirElementWithResolveState> = hashSetOf(),
) : LLFirTargetResolver(target, FirResolvePhase.SUPER_TYPES) {
    private val supertypeResolver = object : FirSupertypeResolverVisitor(
        session = resolveTargetSession,
        supertypeComputationSession = supertypeComputationSession,
        scopeSession = resolveTargetScopeSession,
    ) {
        /**
         * We can do nothing here because at a call moment we've already resolved [outerClass]
         * because we resolve classes from top to down
         */
        override fun resolveAllSupertypesForOuterClass(outerClass: FirClass) {
            // We can get into this function during a loop calculation, so it is possible that the result for [outerClass]
            // is not yet published, so we expect that this class was already visited or resolved
            if (outerClass !in visitedElements) {
                outerClass.asResolveTarget()?.let { resolveTarget ->
                    // It is possible in case of declaration collision,
                    // so we need this logic only to be sure that [outerClass] is resolved
                    resolveToSupertypePhase(resolveTarget)
                }

                LLFirSupertypeLazyResolver.checkIsResolved(outerClass)
            }
        }
    }

    @Deprecated("Should never be called directly, only for override purposes, please use withRegularClass", level = DeprecationLevel.ERROR)
    override fun withContainingRegularClass(firClass: FirRegularClass, action: () -> Unit) {
        doResolveWithoutLock(firClass)
        supertypeResolver.withClass(firClass) {
            action()
        }
    }

    override fun doResolveWithoutLock(target: FirElementWithResolveState): Boolean {
        val isVisited = !visitedElements.add(target)
        if (isVisited) return true

        when (target) {
            is FirRegularClass -> performResolve(
                declaration = target,
                superTypeRefsForTransformation = {
                    // We should create a copy of the original collection
                    // to avoid [ConcurrentModificationException] during another thread publication
                    ArrayList(target.superTypeRefs)
                },
                resolver = {
                    supertypeResolver.withClass(target) {
                        supertypeResolver.resolveSpecificClassLikeSupertypes(target, it, resolveRecursively = false)
                    }
                },
                superTypeUpdater = { superTypeRefs ->
                    val expandedTypeRefs = superTypeRefs.map { supertypeComputationSession.expandTypealiasInPlace(it, target.llFirSession) }
                    target.replaceSuperTypeRefs(expandedTypeRefs)
                    resolveTargetSession.platformSupertypeUpdater?.updateSupertypesIfNeeded(target, resolveTargetScopeSession)
                },
            )
            is FirTypeAlias -> performResolve(
                declaration = target,
                superTypeRefsForTransformation = { target.expandedTypeRef },
                resolver = { supertypeResolver.resolveTypeAliasSupertype(target, it, resolveRecursively = false) },
                superTypeUpdater = { superTypeRefs ->
                    val expandedTypeRef = supertypeComputationSession.expandTypealiasInPlace(superTypeRefs.single(), target.llFirSession)
                    target.replaceExpandedTypeRef(expandedTypeRef)
                },
            )
            else -> {
                performCustomResolveUnderLock(target) {
                    // just update the phase
                }
            }
        }

        return true
    }

    /**
     * [superTypeRefsForTransformation] will be executed under [declaration] lock
     */
    private inline fun <T : FirClassLikeDeclaration, S> performResolve(
        declaration: T,
        superTypeRefsForTransformation: () -> S,
        resolver: (S) -> List<FirResolvedTypeRef>,
        crossinline superTypeUpdater: (List<FirTypeRef>) -> Unit,
    ) {
        // To avoid redundant work, because a publication won't be executed
        if (declaration.resolvePhase >= resolverPhase) return

        declaration.lazyResolveToPhase(resolverPhase.previous)

        var superTypeRefs: S? = null
        withReadLock(declaration) {
            superTypeRefs = superTypeRefsForTransformation()
        }

        // "null" means that some other thread is already resolved [declaration] to [resolverPhase]
        if (superTypeRefs == null) return

        // 1. Resolve declaration super type refs
        @Suppress("UNCHECKED_CAST")
        val resolvedSuperTypeRefs = resolver(superTypeRefs as S)

        // 2. Resolve super declarations
        val status = supertypeComputationSession.getSupertypesComputationStatus(declaration)
        if (status is SupertypeComputationStatus.Computed) {
            crawlAllSupertypes(declaration, status.supertypeRefs)
        }

        // 3. Find loops
        val loopedSuperTypeRefs = supertypeComputationSession.findLoopFor(declaration)

        // 4. Get error type refs or already resolved
        val resultedTypeRefs = loopedSuperTypeRefs ?: resolvedSuperTypeRefs

        // 5. Publish the result
        performCustomResolveUnderLock(declaration) {
            superTypeUpdater(resultedTypeRefs)
        }
    }

    fun crawlAllSupertypes(declaration: FirClassLikeDeclaration, superTypeRefs: List<FirResolvedTypeRef>) {
        supertypeComputationSession.withDeclarationSession(declaration) {
            for (computedType in superTypeRefs) {
                crawlSupertype(computedType.coneType)
            }
        }
    }

    private fun resolveToSupertypePhase(target: LLFirSingleResolveTarget) {
        LLFirSuperTypeTargetResolver(
            target = target,
            supertypeComputationSession = supertypeComputationSession,
            visitedElements = visitedElements,
        ).resolveDesignation()

        LLFirSupertypeLazyResolver.checkIsResolved(target.target)
    }

    /**
     * We want to apply resolved supertypes to as many designations as possible.
     * So we crawl the resolved supertypes of visited designations to find more designations to collect.
     */
    private fun crawlSupertype(type: ConeKotlinType) {
        // Resolution order: from declaration site to use site
        for (session in supertypeComputationSession.useSiteSessions.asReversed()) {
            /**
             * We can avoid deduplication here as the symbol will be checked with [visitedElements]
             */
            type.toSymbol(session)?.let(::crawlSupertype)
        }

        if (type is ConeClassLikeType) {
            // The `classLikeDeclaration` is not associated with a file, and thus there is no need to resolve it, but it may still point
            // to declarations via its type arguments which need to be collected and have a containing file.
            // For example, a `Function1` could point to a type alias.
            type.typeArguments.forEach { it.type?.let(::crawlSupertype) }
        }
    }

    private fun crawlSupertype(symbol: FirClassifierSymbol<*>) {
        val classLikeDeclaration = symbol.fir
        if (classLikeDeclaration !is FirClassLikeDeclaration) return
        if (classLikeDeclaration in visitedElements) return

        if (classLikeDeclaration.resolvePhase >= resolverPhase) {
            visitedElements += classLikeDeclaration
            if (classLikeDeclaration is FirJavaClass) {
                // We do not have phases guarantees for Java classes, so we should process them with an assumption
                // that there are some unresolved supertypes from the declaration site point of view
                supertypeComputationSession.withDeclarationSession(classLikeDeclaration) {
                    crawlSupertypeFromResolvedDeclaration(classLikeDeclaration)
                }
            } else {
                crawlSupertypeFromResolvedDeclaration(classLikeDeclaration)
            }

            return
        }

        val resolveTarget = classLikeDeclaration.asResolveTarget()
        if (resolveTarget != null) {
            resolveToSupertypePhase(resolveTarget)
        }
    }

    private fun crawlSupertypeFromResolvedDeclaration(classLikeDeclaration: FirClassLikeDeclaration) {
        val parentClass = classLikeDeclaration.outerClass()
        if (parentClass != null) {
            crawlSupertype(parentClass.defaultType())
        }

        val superTypeRefs = when (classLikeDeclaration) {
            is FirTypeAlias -> listOf(classLikeDeclaration.expandedTypeRef)
            is FirClass -> classLikeDeclaration.superTypeRefs
        }

        for (typeRef in superTypeRefs) {
            val coneType = typeRef.coneTypeOrNull ?: errorWithFirSpecificEntries(
                "The declaration super type must be resolved, but the actual type reference is not resolved",
                fir = classLikeDeclaration,
            ) {
                withFirEntry("typeRef", typeRef)
            }

            crawlSupertype(coneType)
        }
    }

    override fun doLazyResolveUnderLock(target: FirElementWithResolveState) {
        error("Should be resolved without lock in ${::doResolveWithoutLock.name}")
    }
}

private fun FirClassLikeDeclaration.asResolveTarget(): LLFirSingleResolveTarget? = tryCollectDesignation()?.asResolveTarget()

private fun FirClassLikeDeclaration.outerClass(): FirRegularClass? =
    llFirSession.firProvider.getContainingClass(symbol)?.fir as? FirRegularClass

private open class LLFirSupertypeComputationSession(
    useSiteSessions: PersistentList<LLFirSession> = persistentListOf(),
) : SupertypeComputationSession() {
    var useSiteSessions: PersistentList<LLFirSession> = useSiteSessions
        private set

    inline fun withDeclarationSession(declaration: FirClassLikeDeclaration, action: () -> Unit) {
        val newSession = declaration.llFirSession.takeUnless { it == useSiteSessions.lastOrNull() }
        try {
            newSession?.let { useSiteSessions = useSiteSessions.add(it) }
            action()
        } finally {
            newSession?.let { useSiteSessions = useSiteSessions.removeAt(useSiteSessions.lastIndex) }
        }
    }

    /**
     * These collections exist to reuse a collection for each search to avoid repeated memory allocation.
     * Can be replaced with a new collection on each invocation of [findLoopFor]
     */
    private val visited: MutableSet<FirClassLikeDeclaration> = hashSetOf()
    private val looped: MutableSet<FirClassLikeDeclaration> = hashSetOf()
    private val pathOrderedSet: LinkedHashSet<FirClassLikeDeclaration> = LinkedHashSet()
    // ---------------

    /**
     * Map from [FirClassLikeDeclaration] to [List<FirResolvedTypeRef>>],
     * where the list contains at least one [FirErrorTypeRef] for looped references
     */
    private val updatedTypesForDeclarationsWithLoop: MutableMap<FirClassLikeDeclaration, List<FirResolvedTypeRef>> = hashMapOf()

    /**
     * @param declaration declaration to be checked for loops
     * @return list of resolved super type refs if at least one of them is [FirErrorTypeRef] due to cycle hierarchy
     */
    fun findLoopFor(declaration: FirClassLikeDeclaration): List<FirResolvedTypeRef>? {
        breakLoopFor(
            declaration = declaration,
            // Only loops from the declaration site point of view should be processed
            session = declaration.llFirSession,
            visited = visited,
            looped = looped,
            pathOrderedSet = pathOrderedSet,
            // LL resolver only works for non-local declarations
            localClassesNavigationInfo = null,
        )

        visited.clear()
        looped.clear()
        return updatedTypesForDeclarationsWithLoop[declaration]
    }

    /**
     * We shouldn't try to iterate over unresolved class. Otherwise, it can lead to [ConcurrentModificationException]
     */
    override fun getResolvedSuperTypeRefsForOutOfSessionDeclaration(
        classLikeDeclaration: FirClassLikeDeclaration,
        useSiteSession: FirSession,
    ): List<FirResolvedTypeRef> {
        if (classLikeDeclaration.resolvePhase < FirResolvePhase.SUPER_TYPES) return emptyList()

        return super.getResolvedSuperTypeRefsForOutOfSessionDeclaration(classLikeDeclaration, useSiteSession)
    }

    /**
     * It is possible that one of super type refs were already reported as an error, but the second – not.
     * So in this case, we want to save already reported errors and add a new one.
     * Example:
     * ```
     * interface B : A, ResolveMe {}
     * interface C : B {}
     * interface D : B {}
     * interface ResolveMe<caret> : F {}
     * // D will be marked as error during ResolveMe->F->D->B->ResolveMe round.
     * // And we will back to super type refs of class F during the resolution of class C
     * interface F : D, C {}
     * interface NonLoopedInterface : C
     * ```
     */
    override fun reportLoopErrorRefs(classLikeDeclaration: FirClassLikeDeclaration, supertypeRefs: List<FirResolvedTypeRef>) {
        updatedTypesForDeclarationsWithLoop.merge(classLikeDeclaration, supertypeRefs) { oldRefs, newRefs ->
            buildList(oldRefs.size) {
                for ((old, new) in oldRefs.zip(newRefs)) {
                    if (old is FirErrorTypeRef) {
                        add(old)
                    } else {
                        add(new)
                    }
                }
            }
        }
    }
}

/**
 * This session is designed to be used during local classes resolution to
 * properly handle non-local classes in the hierarchy to not modify them by the CLI transformer.
 *
 * It is not enough to just resolve non-local classes to [FirResolvePhase.SUPER_TYPES] due to classpath substitution.
 * Such non-local classes have to be processed via [LLFirSuperTypeTargetResolver.crawlAllSupertypes] in the context of local use site.
 *
 * ### Example:
 * ```kotlin
 * // MODULE: dependency
 * // FILE: dependency.kt
 * package org.example
 *
 * interface Base
 *
 * abstract class Foo : Base
 *
 * // MODULE: usage(dependency)
 * // FILE: usage.kt
 * package org.example
 *
 * interface Base {
 *     fun bar()
 *     fun baz()
 * }
 *
 * fun test() {
 *     abstract class FooImpl : Foo() {
 *     }
 * }
 * ```
 *
 * In this case, the entire hierarchy of `Foo` has to be resolved with the local use site session.
 *
 * @see LLFirSupertypeComputationSession
 * @see org.jetbrains.kotlin.fir.resolve.transformers.runSupertypeResolvePhaseForLocalClass
 */
internal class LLSupertypeComputationSessionLocalClassesAware : SupertypeComputationSession() {
    override fun getResolvedSuperTypeRefsForOutOfSessionDeclaration(
        classLikeDeclaration: FirClassLikeDeclaration,
        useSiteSession: FirSession,
    ): List<FirResolvedTypeRef> {
        if (!classLikeDeclaration.isLocal) {
            resolveToSupertypePhase(classLikeDeclaration, useSiteSession)
        }

        return super.getResolvedSuperTypeRefsForOutOfSessionDeclaration(classLikeDeclaration, useSiteSession)
    }

    override fun supertypeRefs(
        declaration: FirClassLikeDeclaration,
        useSiteSession: FirSession,
    ): List<FirTypeRef> = if (declaration.isLocal) {
        super.supertypeRefs(declaration, useSiteSession)
    } else {
        resolveToSupertypePhase(declaration, useSiteSession)

        // The hierarchy is fully resolved, so the empty list can be returned
        emptyList()
    }

    private fun resolveToSupertypePhase(declaration: FirClassLikeDeclaration, useSiteSession: FirSession) {
        // 1. Resolve the entire hierarchy for the non-local class (in the declaration-site context)
        declaration.lazyResolveToPhase(FirResolvePhase.SUPER_TYPES)

        val target = declaration.asResolveTarget() ?: return
        useSiteSession as LLFirSession

        val resolver = LLFirSuperTypeTargetResolver(
            target = target,
            supertypeComputationSession = LLFirSupertypeComputationSession(
                useSiteSessions = persistentListOf(useSiteSession),
            ),
            visitedElements = hashSetOf(),
        )

        // 2. Resolve the entire hierarchy for the non-local class (in the use-site context)
        resolver.crawlAllSupertypes(
            declaration = declaration,
            superTypeRefs = super.getResolvedSuperTypeRefsForOutOfSessionDeclaration(declaration, useSiteSession),
        )
    }
}

