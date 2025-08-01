fun getClassToEnumFoo(): ClassToEnum.Foo = ClassToEnum.Foo()
fun getClassToEnumFooAsAny(): Any = ClassToEnum.Foo()

fun getClassToEnumBar(): ClassToEnum.Bar = ClassToEnum.Bar
fun getClassToEnumBarAsAny(): Any = ClassToEnum.Bar

fun getClassToEnumBaz(): ClassToEnum.Baz = ClassToEnum().Baz()
fun getClassToEnumBazAsAny(): Any = ClassToEnum().Baz()

fun getObjectToEnumFoo(): ObjectToEnum.Foo = ObjectToEnum.Foo()
fun getObjectToEnumFooAsAny(): Any = ObjectToEnum.Foo()

fun getObjectToEnumBar(): ObjectToEnum.Bar = ObjectToEnum.Bar
fun getObjectToEnumBarAsAny(): Any = ObjectToEnum.Bar

fun getEnumToClassFoo(): EnumToClass = EnumToClass.Foo
fun getEnumToClassFooAsAny(): Any = EnumToClass.Foo

fun getEnumToClassBar(): EnumToClass = EnumToClass.Bar
fun getEnumToClassBarAsAny(): Any = EnumToClass.Bar

fun getEnumToClassBaz(): EnumToClass = EnumToClass.Baz
fun getEnumToClassBazAsAny(): Any = EnumToClass.Baz

fun getEnumToObjectFoo(): EnumToObject = EnumToObject.Foo
fun getEnumToObjectFooAsAny(): Any = EnumToObject.Foo

fun getEnumToObjectBar(): EnumToObject = EnumToObject.Bar
fun getEnumToObjectBarAsAny(): Any = EnumToObject.Bar

fun getClassToObject(): ClassToObject = ClassToObject()
fun getClassToObjectAsAny(): Any = ClassToObject()

fun getObjectToClass(): ObjectToClass = ObjectToClass
fun getObjectToClassAsAny(): Any = ObjectToClass

fun getClassToInterface(): ClassToInterface = ClassToInterface()
fun getClassToInterfaceAsAny(): Any = ClassToInterface()

fun getNestedObjectToCompanion1(): NestedObjectToCompanion1.Companion = NestedObjectToCompanion1.Companion
fun getNestedObjectToCompanion1AsAny(): Any = NestedObjectToCompanion1.Companion

fun getNestedObjectToCompanion2(): NestedObjectToCompanion2.Foo = NestedObjectToCompanion2.Foo
fun getNestedObjectToCompanion2AsAny(): Any = NestedObjectToCompanion2.Foo

fun getCompanionToNestedObject1(): CompanionToNestedObject1.Companion = CompanionToNestedObject1.Companion
fun getCompanionToNestedObject1AsAny(): Any = CompanionToNestedObject1.Companion
fun getCompanionToNestedObject1Name(): String = CompanionToNestedObject1.Companion.name()
fun getCompanionToNestedObject1NameShort(): String = CompanionToNestedObject1.name() // "Companion" is omit

fun getCompanionToNestedObject2(): CompanionToNestedObject2.Foo = CompanionToNestedObject2.Foo
fun getCompanionToNestedObject2AsAny(): Any = CompanionToNestedObject2.Foo
fun getCompanionToNestedObject2Name(): String = CompanionToNestedObject2.Foo.name()
fun getCompanionToNestedObject2NameShort(): String = CompanionToNestedObject2.name() // "Foo" is omit

fun getCompanionAndNestedObjectsSwap(): String = CompanionAndNestedObjectsSwap.name() // companion object name is omit

fun getNestedToInnerName() = NestedClassContainer.NestedToInner().name()
fun getNestedToInnerObjectName() = NestedClassContainer.NestedToInner.Object.name()
fun getNestedToInnerCompanionName() = NestedClassContainer.NestedToInner.name()
fun getNestedToInnerNestedName() = NestedClassContainer.NestedToInner.Nested().name()
fun getNestedToInnerInnerName() = NestedClassContainer.NestedToInner().Inner().name()

fun getInnerToNestedName() = InnerClassContainer().InnerToNested().name()
fun getInnerToNestedObjectName() = InnerClassContainer().InnerToNested().Object().name()
fun getInnerToNestedCompanionName() = InnerClassContainer().InnerToNested().Companion().name()
fun getInnerToNestedNestedName() = InnerClassContainer().InnerToNested().Nested().name()
fun getInnerToNestedInnerName() = InnerClassContainer().InnerToNested().Inner().name()

annotation class AnnotationClassWithParameterThatBecomesRegularClass(val x: AnnotationClassThatBecomesRegularClass)
annotation class AnnotationClassWithParameterOfParameterThatBecomesRegularClass(val x: AnnotationClassWithParameterThatBecomesRegularClass)
annotation class AnnotationClassWithParameterThatDisappears(val x: AnnotationClassThatDisappears)
annotation class AnnotationClassWithParameterOfParameterThatDisappears(val x: AnnotationClassWithParameterThatDisappears)
annotation class AnnotationClassWithClassReferenceParameterThatDisappears1(val x: kotlin.reflect.KClass<RemovedClass> = RemovedClass::class)
annotation class AnnotationClassWithClassReferenceParameterThatDisappears2(val x: kotlin.reflect.KClass<*> = RemovedClass::class)
annotation class AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1(val x: AnnotationClassWithClassReferenceParameterThatDisappears1 = AnnotationClassWithClassReferenceParameterThatDisappears1())
annotation class AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2(val x: AnnotationClassWithClassReferenceParameterThatDisappears2 = AnnotationClassWithClassReferenceParameterThatDisappears2())
annotation class AnnotationClassWithRemovedEnumEntryParameter(val x: EnumClassWithDisappearingEntry = EnumClassWithDisappearingEntry.REMOVED)
annotation class AnnotationClassWithRemovedEnumEntryParameterOfParameter(val x: AnnotationClassWithRemovedEnumEntryParameter = AnnotationClassWithRemovedEnumEntryParameter())
annotation class AnnotationClassWithParameterThatBecomesPrivate1(val x: PublicTopLevelLib1.AnnotationClassThatBecomesPrivate = PublicTopLevelLib1.AnnotationClassThatBecomesPrivate())
annotation class AnnotationClassWithParameterThatBecomesPrivate2(val x: kotlin.reflect.KClass<PublicTopLevelLib1.ClassThatBecomesPrivate> = PublicTopLevelLib1.ClassThatBecomesPrivate::class)
annotation class AnnotationClassWithParameterOfParameterThatBecomesPrivate2(val x: AnnotationClassWithParameterThatBecomesPrivate2 = AnnotationClassWithParameterThatBecomesPrivate2())
annotation class AnnotationClassWithParameterThatBecomesPrivate3(val x: kotlin.reflect.KClass<*> = PublicTopLevelLib1.ClassThatBecomesPrivate::class)
annotation class AnnotationClassWithParameterOfParameterThatBecomesPrivate3(val x: AnnotationClassWithParameterThatBecomesPrivate3 = AnnotationClassWithParameterThatBecomesPrivate3())
annotation class AnnotationClassWithParameterThatBecomesPrivate4(val x: PublicTopLevelLib1.EnumClassThatBecomesPrivate = PublicTopLevelLib1.EnumClassThatBecomesPrivate.ENTRY)
annotation class AnnotationClassWithParameterOfParameterThatBecomesPrivate4(val x: AnnotationClassWithParameterThatBecomesPrivate4 = AnnotationClassWithParameterThatBecomesPrivate4())

object PublicTopLevelLib2 {
    private class PrivateClass
    annotation class AnnotationClassWithParameterWithPrivateDefaultValue(val x: kotlin.reflect.KClass<*> = PrivateClass::class)
    annotation class AnnotationClassWithParameterOfParameterWithPrivateDefaultValue(val x: AnnotationClassWithParameterWithPrivateDefaultValue = AnnotationClassWithParameterWithPrivateDefaultValue())
}

fun getAnnotationClassWithChangedParameterType(): AnnotationClassWithChangedParameterType = AnnotationClassWithChangedParameterType(101)
fun getAnnotationClassWithChangedParameterTypeAsAny(): Any = AnnotationClassWithChangedParameterType(103)
fun getAnnotationClassThatBecomesRegularClass(): AnnotationClassThatBecomesRegularClass = AnnotationClassThatBecomesRegularClass(105)
fun getAnnotationClassThatBecomesRegularClassAsAny(): Any = AnnotationClassThatBecomesRegularClass(107)
fun getAnnotationClassWithParameterThatBecomesRegularClass(): AnnotationClassWithParameterThatBecomesRegularClass = AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(109))
fun getAnnotationClassWithParameterThatBecomesRegularClassAsAny(): Any = AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(111))
fun getAnnotationClassWithParameterOfParameterThatBecomesRegularClass(): AnnotationClassWithParameterOfParameterThatBecomesRegularClass = AnnotationClassWithParameterOfParameterThatBecomesRegularClass(AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(113)))
fun getAnnotationClassWithParameterOfParameterThatBecomesRegularClassAsAny(): Any = AnnotationClassWithParameterOfParameterThatBecomesRegularClass(AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(115)))
fun getAnnotationClassThatDisappears(): AnnotationClassThatDisappears = AnnotationClassThatDisappears(117)
fun getAnnotationClassThatDisappearsAsAny(): Any = AnnotationClassThatDisappears(119)
fun getAnnotationClassWithParameterThatDisappears(): AnnotationClassWithParameterThatDisappears = AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(121))
fun getAnnotationClassWithParameterThatDisappearsAsAny(): Any = AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(123))
fun getAnnotationClassWithParameterOfParameterThatDisappears(): AnnotationClassWithParameterOfParameterThatDisappears = AnnotationClassWithParameterOfParameterThatDisappears(AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(125)))
fun getAnnotationClassWithParameterOfParameterThatDisappearsAsAny(): Any = AnnotationClassWithParameterOfParameterThatDisappears(AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(127)))
fun getAnnotationClassWithRenamedParameters(): AnnotationClassWithRenamedParameters = AnnotationClassWithRenamedParameters(129, "Banana")
fun getAnnotationClassWithRenamedParametersAsAny(): Any = AnnotationClassWithRenamedParameters(131, "Orange")
fun getAnnotationClassWithReorderedParameters(): AnnotationClassWithReorderedParameters = AnnotationClassWithReorderedParameters(133, "Kiwi")
fun getAnnotationClassWithReorderedParametersAsAny(): Any = AnnotationClassWithReorderedParameters(135, "Grapefruit")
fun getAnnotationClassWithNewParameter(): AnnotationClassWithNewParameter = AnnotationClassWithNewParameter(137)
fun getAnnotationClassWithNewParameterAsAny(): Any = AnnotationClassWithNewParameter(139)

fun getAnnotationClassWithClassReferenceParameterThatDisappears1(): AnnotationClassWithClassReferenceParameterThatDisappears1 = AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class)
fun getAnnotationClassWithClassReferenceParameterThatDisappears1AsAny(): Any = AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class)
fun getAnnotationClassWithDefaultClassReferenceParameterThatDisappears1(): AnnotationClassWithClassReferenceParameterThatDisappears1 = AnnotationClassWithClassReferenceParameterThatDisappears1()
fun getAnnotationClassWithDefaultClassReferenceParameterThatDisappears1AsAny(): Any = AnnotationClassWithClassReferenceParameterThatDisappears1()
fun getAnnotationClassWithClassReferenceParameterThatDisappears2(): AnnotationClassWithClassReferenceParameterThatDisappears2 = AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class)
fun getAnnotationClassWithClassReferenceParameterThatDisappears2AsAny(): Any = AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class)
fun getAnnotationClassWithDefaultClassReferenceParameterThatDisappears2(): AnnotationClassWithClassReferenceParameterThatDisappears2 = AnnotationClassWithClassReferenceParameterThatDisappears2()
fun getAnnotationClassWithDefaultClassReferenceParameterThatDisappears2AsAny(): Any = AnnotationClassWithClassReferenceParameterThatDisappears2()

fun getAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1(): AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1 = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1(AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class))
fun getAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1AsAny(): Any = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1(AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class))
fun getAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1(): AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1 = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1()
fun getAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1AsAny(): Any = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1()
fun getAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2(): AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2 = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2(AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class))
fun getAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2AsAny(): Any = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2(AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class))
fun getAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2(): AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2 = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2()
fun getAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2AsAny(): Any = AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2()
fun getAnnotationClassWithRemovedEnumEntryParameter(): AnnotationClassWithRemovedEnumEntryParameter = AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED)
fun getAnnotationClassWithRemovedEnumEntryParameterAsAny(): Any = AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED)
fun getAnnotationClassWithDefaultRemovedEnumEntryParameter(): AnnotationClassWithRemovedEnumEntryParameter = AnnotationClassWithRemovedEnumEntryParameter()
fun getAnnotationClassWithDefaultRemovedEnumEntryParameterAsAny(): Any = AnnotationClassWithRemovedEnumEntryParameter()
fun getAnnotationClassWithRemovedEnumEntryParameterOfParameter(): AnnotationClassWithRemovedEnumEntryParameterOfParameter = AnnotationClassWithRemovedEnumEntryParameterOfParameter(AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED))
fun getAnnotationClassWithRemovedEnumEntryParameterOfParameterAsAny(): Any = AnnotationClassWithRemovedEnumEntryParameterOfParameter(AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED))
fun getAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameter(): AnnotationClassWithRemovedEnumEntryParameterOfParameter = AnnotationClassWithRemovedEnumEntryParameterOfParameter()
fun getAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameterAsAny(): Any = AnnotationClassWithRemovedEnumEntryParameterOfParameter()

fun getAnnotationClassWithParameterThatBecomesPrivate1(): AnnotationClassWithParameterThatBecomesPrivate1 = AnnotationClassWithParameterThatBecomesPrivate1()
fun getAnnotationClassWithParameterThatBecomesPrivate1AsAny(): Any = AnnotationClassWithParameterThatBecomesPrivate1()
fun getAnnotationClassWithParameterThatBecomesPrivate2(): AnnotationClassWithParameterThatBecomesPrivate2 = AnnotationClassWithParameterThatBecomesPrivate2()
fun getAnnotationClassWithParameterThatBecomesPrivate2AsAny(): Any = AnnotationClassWithParameterThatBecomesPrivate2()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate2(): AnnotationClassWithParameterOfParameterThatBecomesPrivate2 = AnnotationClassWithParameterOfParameterThatBecomesPrivate2()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate2AsAny(): Any = AnnotationClassWithParameterOfParameterThatBecomesPrivate2()
fun getAnnotationClassWithParameterThatBecomesPrivate3(): AnnotationClassWithParameterThatBecomesPrivate3 = AnnotationClassWithParameterThatBecomesPrivate3()
fun getAnnotationClassWithParameterThatBecomesPrivate3AsAny(): Any = AnnotationClassWithParameterThatBecomesPrivate3()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate3(): AnnotationClassWithParameterOfParameterThatBecomesPrivate3 = AnnotationClassWithParameterOfParameterThatBecomesPrivate3()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate3AsAny(): Any = AnnotationClassWithParameterOfParameterThatBecomesPrivate3()
fun getAnnotationClassWithParameterThatBecomesPrivate4(): AnnotationClassWithParameterThatBecomesPrivate4 = AnnotationClassWithParameterThatBecomesPrivate4()
fun getAnnotationClassWithParameterThatBecomesPrivate4AsAny(): Any = AnnotationClassWithParameterThatBecomesPrivate4()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate4(): AnnotationClassWithParameterOfParameterThatBecomesPrivate4 = AnnotationClassWithParameterOfParameterThatBecomesPrivate4()
fun getAnnotationClassWithParameterOfParameterThatBecomesPrivate4AsAny(): Any = AnnotationClassWithParameterOfParameterThatBecomesPrivate4()
fun getAnnotationClassWithParameterWithPrivateDefaultValue(): PublicTopLevelLib2.AnnotationClassWithParameterWithPrivateDefaultValue = PublicTopLevelLib2.AnnotationClassWithParameterWithPrivateDefaultValue()
fun getAnnotationClassWithParameterWithPrivateDefaultValueAsAny(): Any = PublicTopLevelLib2.AnnotationClassWithParameterWithPrivateDefaultValue()
fun getAnnotationClassWithParameterOfParameterWithPrivateDefaultValue(): PublicTopLevelLib2.AnnotationClassWithParameterOfParameterWithPrivateDefaultValue = PublicTopLevelLib2.AnnotationClassWithParameterOfParameterWithPrivateDefaultValue()
fun getAnnotationClassWithParameterOfParameterWithPrivateDefaultValueAsAny(): Any = PublicTopLevelLib2.AnnotationClassWithParameterOfParameterWithPrivateDefaultValue()

@AnnotationClassWithChangedParameterType(1) class HolderOfAnnotationClassWithChangedParameterType { override fun toString() = "HolderOfAnnotationClassWithChangedParameterType" }
@AnnotationClassThatBecomesRegularClass(2) class HolderOfAnnotationClassThatBecomesRegularClass { override fun toString() = "HolderOfAnnotationClassThatBecomesRegularClass" }
@AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(3)) class HolderOfAnnotationClassWithParameterThatBecomesRegularClass { override fun toString() = "HolderOfAnnotationClassWithParameterThatBecomesRegularClass" }
@AnnotationClassWithParameterOfParameterThatBecomesRegularClass(AnnotationClassWithParameterThatBecomesRegularClass(AnnotationClassThatBecomesRegularClass(4))) class HolderOfAnnotationClassWithParameterOfParameterThatBecomesRegularClass { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterThatBecomesRegularClass" }
@AnnotationClassThatDisappears(5) class HolderOfAnnotationClassThatDisappears { override fun toString() = "HolderOfAnnotationClassThatDisappears" }
@AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(6)) class HolderOfAnnotationClassWithParameterThatDisappears { override fun toString() = "HolderOfAnnotationClassWithParameterThatDisappears" }
@AnnotationClassWithParameterOfParameterThatDisappears(AnnotationClassWithParameterThatDisappears(AnnotationClassThatDisappears(7))) class HolderOfAnnotationClassWithParameterOfParameterThatDisappears { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterThatDisappears" }
@AnnotationClassWithRenamedParameters(8, "Grape") class HolderOfAnnotationClassWithRenamedParameters { override fun toString() = "HolderOfAnnotationClassWithRenamedParameters" }
@AnnotationClassWithReorderedParameters(9, "Figs") class HolderOfAnnotationClassWithReorderedParameters { override fun toString() = "HolderOfAnnotationClassWithReorderedParameters" }
@AnnotationClassWithNewParameter(10) class HolderOfAnnotationClassWithNewParameter { override fun toString() = "HolderOfAnnotationClassWithNewParameter" }
@AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class) class HolderOfAnnotationClassWithClassReferenceParameterThatDisappears1 { override fun toString() = "HolderOfAnnotationClassWithClassReferenceParameterThatDisappears1" }
@AnnotationClassWithClassReferenceParameterThatDisappears1() class HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears1 { override fun toString() = "HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears1" }
@AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class) class HolderOfAnnotationClassWithClassReferenceParameterThatDisappears2 { override fun toString() = "HolderOfAnnotationClassWithClassReferenceParameterThatDisappears2" }
@AnnotationClassWithClassReferenceParameterThatDisappears2() class HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears2 { override fun toString() = "HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears2" }
@AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1(AnnotationClassWithClassReferenceParameterThatDisappears1(RemovedClass::class)) class HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1 { override fun toString() = "HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1" }
@AnnotationClassWithClassReferenceParameterOfParameterThatDisappears1() class HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1 { override fun toString() = "HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1" }
@AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2(AnnotationClassWithClassReferenceParameterThatDisappears2(RemovedClass::class)) class HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2 { override fun toString() = "HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2" }
@AnnotationClassWithClassReferenceParameterOfParameterThatDisappears2() class HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2 { override fun toString() = "HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2" }
@AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED) class HolderOfAnnotationClassWithRemovedEnumEntryParameter { override fun toString() = "HolderOfAnnotationClassWithRemovedEnumEntryParameter" }
@AnnotationClassWithRemovedEnumEntryParameter() class HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameter { override fun toString() = "HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameter" }
@AnnotationClassWithRemovedEnumEntryParameterOfParameter(AnnotationClassWithRemovedEnumEntryParameter(EnumClassWithDisappearingEntry.REMOVED)) class HolderOfAnnotationClassWithRemovedEnumEntryParameterOfParameter { override fun toString() = "HolderOfAnnotationClassWithRemovedEnumEntryParameterOfParameter" }
@AnnotationClassWithRemovedEnumEntryParameterOfParameter() class HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameter { override fun toString() = "HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameter" }
@AnnotationClassWithParameterThatBecomesPrivate1 class HolderOfAnnotationClassWithParameterThatBecomesPrivate1 { override fun toString() = "HolderOfAnnotationClassWithParameterThatBecomesPrivate1" }
@AnnotationClassWithParameterThatBecomesPrivate2 class HolderOfAnnotationClassWithParameterThatBecomesPrivate2 { override fun toString() = "HolderOfAnnotationClassWithParameterThatBecomesPrivate2" }
@AnnotationClassWithParameterOfParameterThatBecomesPrivate2 class HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate2 { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate2" }
@AnnotationClassWithParameterThatBecomesPrivate3 class HolderOfAnnotationClassWithParameterThatBecomesPrivate3 { override fun toString() = "HolderOfAnnotationClassWithParameterThatBecomesPrivate3" }
@AnnotationClassWithParameterOfParameterThatBecomesPrivate3 class HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate3 { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate3" }
@AnnotationClassWithParameterThatBecomesPrivate4 class HolderOfAnnotationClassWithParameterThatBecomesPrivate4 { override fun toString() = "HolderOfAnnotationClassWithParameterThatBecomesPrivate4" }
@AnnotationClassWithParameterOfParameterThatBecomesPrivate4 class HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate4 { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate4" }
@PublicTopLevelLib2.AnnotationClassWithParameterWithPrivateDefaultValue class HolderOfAnnotationClassWithParameterWithPrivateDefaultValue { override fun toString() = "HolderOfAnnotationClassWithParameterWithPrivateDefaultValue" }
@PublicTopLevelLib2.AnnotationClassWithParameterOfParameterWithPrivateDefaultValue class HolderOfAnnotationClassWithParameterOfParameterWithPrivateDefaultValue { override fun toString() = "HolderOfAnnotationClassWithParameterOfParameterWithPrivateDefaultValue" }

fun getHolderOfAnnotationClassWithChangedParameterType() = HolderOfAnnotationClassWithChangedParameterType()
fun getHolderOfAnnotationClassThatBecomesRegularClass() = HolderOfAnnotationClassThatBecomesRegularClass()
fun getHolderOfAnnotationClassWithParameterThatBecomesRegularClass() = HolderOfAnnotationClassWithParameterThatBecomesRegularClass()
fun getHolderOfAnnotationClassWithParameterOfParameterThatBecomesRegularClass() = HolderOfAnnotationClassWithParameterOfParameterThatBecomesRegularClass()
fun getHolderOfAnnotationClassThatDisappears() = HolderOfAnnotationClassThatDisappears()
fun getHolderOfAnnotationClassWithParameterThatDisappears() = HolderOfAnnotationClassWithParameterThatDisappears()
fun getHolderOfAnnotationClassWithParameterOfParameterThatDisappears() = HolderOfAnnotationClassWithParameterOfParameterThatDisappears()
fun getHolderOfAnnotationClassWithRenamedParameters() = HolderOfAnnotationClassWithRenamedParameters()
fun getHolderOfAnnotationClassWithReorderedParameters() = HolderOfAnnotationClassWithReorderedParameters()
fun getHolderOfAnnotationClassWithNewParameter() = HolderOfAnnotationClassWithNewParameter()
fun getHolderOfAnnotationClassWithClassReferenceParameterThatDisappears1() = HolderOfAnnotationClassWithClassReferenceParameterThatDisappears1()
fun getHolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears1() = HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears1()
fun getHolderOfAnnotationClassWithClassReferenceParameterThatDisappears2() = HolderOfAnnotationClassWithClassReferenceParameterThatDisappears2()
fun getHolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears2() = HolderOfAnnotationClassWithDefaultClassReferenceParameterThatDisappears2()
fun getHolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1() = HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears1()
fun getHolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1() = HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears1()
fun getHolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2() = HolderOfAnnotationClassWithClassReferenceParameterOfParameterThatDisappears2()
fun getHolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2() = HolderOfAnnotationClassWithDefaultClassReferenceParameterOfParameterThatDisappears2()
fun getHolderOfAnnotationClassWithRemovedEnumEntryParameter() = HolderOfAnnotationClassWithRemovedEnumEntryParameter()
fun getHolderOfAnnotationClassWithDefaultRemovedEnumEntryParameter() = HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameter()
fun getHolderOfAnnotationClassWithRemovedEnumEntryParameterOfParameter() = HolderOfAnnotationClassWithRemovedEnumEntryParameterOfParameter()
fun getHolderOfAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameter() = HolderOfAnnotationClassWithDefaultRemovedEnumEntryParameterOfParameter()
fun getHolderOfAnnotationClassWithParameterThatBecomesPrivate1(): HolderOfAnnotationClassWithParameterThatBecomesPrivate1 = HolderOfAnnotationClassWithParameterThatBecomesPrivate1()
fun getHolderOfAnnotationClassWithParameterThatBecomesPrivate2(): HolderOfAnnotationClassWithParameterThatBecomesPrivate2 = HolderOfAnnotationClassWithParameterThatBecomesPrivate2()
fun getHolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate2(): HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate2 = HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate2()
fun getHolderOfAnnotationClassWithParameterThatBecomesPrivate3(): HolderOfAnnotationClassWithParameterThatBecomesPrivate3 = HolderOfAnnotationClassWithParameterThatBecomesPrivate3()
fun getHolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate3(): HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate3 = HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate3()
fun getHolderOfAnnotationClassWithParameterThatBecomesPrivate4(): HolderOfAnnotationClassWithParameterThatBecomesPrivate4 = HolderOfAnnotationClassWithParameterThatBecomesPrivate4()
fun getHolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate4(): HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate4 = HolderOfAnnotationClassWithParameterOfParameterThatBecomesPrivate4()
fun getHolderOfAnnotationClassWithParameterWithPrivateDefaultValue(): HolderOfAnnotationClassWithParameterWithPrivateDefaultValue = HolderOfAnnotationClassWithParameterWithPrivateDefaultValue()
fun getHolderOfAnnotationClassWithParameterOfParameterWithPrivateDefaultValue(): HolderOfAnnotationClassWithParameterOfParameterWithPrivateDefaultValue = HolderOfAnnotationClassWithParameterOfParameterWithPrivateDefaultValue()

fun getValueToClass(): ValueToClass = ValueToClass(1)
fun getValueToClassAsAny(): Any = ValueToClass(3)

fun getClassToValue(): ClassToValue = ClassToValue(1)
fun getClassToValueAsAny(): Any = ClassToValue(3)

fun getSumFromDataClass(): Int {
    val (x, y) = DataToClass(1, 2)
    return x + y
}

fun instantiationOfAbstractClass() {
    // Accessing uninitialized members of abstract class is an UB. We shall not allow instantiating
    // abstract classes except for from their direct inheritors.
    ClassToAbstractClass().getGreeting()
}

// This is required to check that enum entry classes are correctly handled in partial linkage.
enum class StableEnum {
    FOO {
        val x = "OK"

        inner class Inner {
            val y = x
        }

        val z = Inner()

        override val test: String
            get() = z.y
    },
    BAR {
        override val test = "OK"
    };

    abstract val test: String
}

// This is required to check that the guard condition initially added in commit
// https://github.com/JetBrains/kotlin/blob/2a4d8800374578c1aa9ec9c996b393a98f5a6e3b/kotlin-native/backend.native/compiler/ir/backend.native/src/org/jetbrains/kotlin/backend/konan/serialization/KonanIrlinker.kt#L701
// does not break Native codegen anymore.
class StableInheritorOfClassThatUsesPrivateTopLevelClass : AbstractIterator<String>() {
    private var i = 0
    public override fun computeNext() {
        if (i < 5) setNext((i++).toString()) else done()
    }
}

fun testStableInheritorOfClassThatUsesPrivateTopLevelClass(): String = buildString {
    for (s in StableInheritorOfClassThatUsesPrivateTopLevelClass()) append(s)
}
