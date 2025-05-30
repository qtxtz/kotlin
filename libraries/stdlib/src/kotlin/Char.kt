/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// Auto-generated file. DO NOT EDIT!
// Generated by org.jetbrains.kotlin.generators.builtins.numbers.primitives.CommonCharGenerator

package kotlin

/** Represents a 16-bit Unicode character. */
public expect class Char : Comparable<Char> {
    /**
     * Compares this value with the specified value for order.
     *
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    @kotlin.internal.IntrinsicConstEvaluation
    public override fun compareTo(other: Char): Int

    /** Adds the other Int value to this value resulting a Char. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun plus(other: Int): Char

    /** Subtracts the other Char value from this value resulting an Int. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun minus(other: Char): Int

    /** Subtracts the other Int value from this value resulting a Char. */
    @kotlin.internal.IntrinsicConstEvaluation
    public operator fun minus(other: Int): Char

    /**
     * Returns this value incremented by one.
     *
     * @sample samples.misc.Builtins.inc
     */
    public operator fun inc(): Char

    /**
     * Returns this value decremented by one.
     *
     * @sample samples.misc.Builtins.dec
     */
    public operator fun dec(): Char

    /** Creates a range from this value to the specified [other] value. */
    public operator fun rangeTo(other: Char): CharRange

    /**
     * Creates a range from this value up to but excluding the specified [other] value.
     *
     * If the [other] value is less than or equal to `this` value, then the returned range is empty.
     */
    @SinceKotlin("1.9")
    @WasExperimental(ExperimentalStdlibApi::class)
    public operator fun rangeUntil(other: Char): CharRange

    /** Returns the value of this character as a `Byte`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code.toByte()"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toByte(): Byte

    /** Returns the value of this character as a `Char`. */
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toChar(): Char

    /** Returns the value of this character as a `Short`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code.toShort()"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toShort(): Short

    /** Returns the value of this character as a `Int`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toInt(): Int

    /** Returns the value of this character as a `Long`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code.toLong()"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toLong(): Long

    /** Returns the value of this character as a `Float`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code.toFloat()"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toFloat(): Float

    /** Returns the value of this character as a `Double`. */
    @Deprecated("Conversion of Char to Number is deprecated. Use Char.code property instead.", ReplaceWith("this.code.toDouble()"))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    @kotlin.internal.IntrinsicConstEvaluation
    public fun toDouble(): Double

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun toString(): String

    @kotlin.internal.IntrinsicConstEvaluation
    public override fun equals(other: Any?): Boolean

    public override fun hashCode(): Int

    @Suppress("EXPECTED_PROPERTY_INITIALIZER")
    public companion object {
        /**
         * The minimum value of a character code unit.
         */
        @SinceKotlin("1.3")
        public const val MIN_VALUE: Char = '\u0000'

        /**
         * The maximum value of a character code unit.
         */
        @SinceKotlin("1.3")
        public const val MAX_VALUE: Char = '\uFFFF'

        /**
         * The minimum value of a Unicode high-surrogate code unit.
         */
        public const val MIN_HIGH_SURROGATE: Char = '\uD800'

        /**
         * The maximum value of a Unicode high-surrogate code unit.
         */
        public const val MAX_HIGH_SURROGATE: Char = '\uDBFF'

        /**
         * The minimum value of a Unicode low-surrogate code unit.
         */
        public const val MIN_LOW_SURROGATE: Char = '\uDC00'

        /**
         * The maximum value of a Unicode low-surrogate code unit.
         */
        public const val MAX_LOW_SURROGATE: Char = '\uDFFF'

        /**
         * The minimum value of a Unicode surrogate code unit.
         */
        public const val MIN_SURROGATE: Char = MIN_HIGH_SURROGATE

        /**
         * The maximum value of a Unicode surrogate code unit.
         */
        public const val MAX_SURROGATE: Char = MAX_LOW_SURROGATE

        /**
         * The number of bytes used to represent a Char in a binary form.
         */
        @SinceKotlin("1.3")
        public const val SIZE_BYTES: Int = 2

        /**
         * The number of bits used to represent a Char in a binary form.
         */
        @SinceKotlin("1.3")
        public const val SIZE_BITS: Int = 16
    }
}
