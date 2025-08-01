// RUN_PIPELINE_TILL: FRONTEND
interface In<in T>
interface Out<out T>
interface Inv<T>
interface Pair<out X, out Y>

interface Test<in I, out O, P> {
    fun ok1(): O
    fun ok2(): In<I>
    fun ok3(): In<In<O>>
    fun ok4(): Inv<P>
    fun ok5(): P
    fun ok6(): Out<O>
    fun ok7(): Out<P>
    fun ok8(): Out<In<P>>
    fun ok9(): Pair<In<I>, O>

    fun ok10(): Inv<in I>
    fun ok11(): Inv<out O>
    fun ok12(): Inv<in P>
    fun ok13(): Inv<out P>

    fun neOk1(): <!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>
    fun neOk2(): In<<!TYPE_VARIANCE_CONFLICT_ERROR!>O<!>>
    fun neOk3(): In<In<<!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>>>
    fun neOk4(): Inv<<!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>>
    fun neOk5(): Inv<<!TYPE_VARIANCE_CONFLICT_ERROR!>O<!>>
    fun neOk6(): Pair<In<<!TYPE_VARIANCE_CONFLICT_ERROR!>O<!>>, <!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>>
    fun neOk7(): Inv<in <!TYPE_VARIANCE_CONFLICT_ERROR!>O<!>>
    fun neOk8(): Out<<!CONFLICTING_PROJECTION("Out")!>in<!> I>

    fun neOk10(): Inv<in <!TYPE_VARIANCE_CONFLICT_ERROR!>O<!>>
    fun neOk11(): Inv<out <!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>>

    fun neOk30(): Pair<!WRONG_NUMBER_OF_TYPE_ARGUMENTS!><I, ><!>
    fun neOk31(): Pair<<!TYPE_VARIANCE_CONFLICT_ERROR!>I<!>, <!WRONG_NUMBER_OF_TYPE_ARGUMENTS!>Inv<!>>
    fun neOk32(): <!WRONG_NUMBER_OF_TYPE_ARGUMENTS!>Inv<!>
    fun neOk33(): Inv<<!SYNTAX!><!>>
    fun neOk34(): Inv<<!UNRESOLVED_REFERENCE!>C<!>>
    fun neOk35(): Inv<!WRONG_NUMBER_OF_TYPE_ARGUMENTS!><P, P><!>
}

/* GENERATED_FIR_TAGS: functionDeclaration, in, inProjection, interfaceDeclaration, nullableType, out, outProjection,
typeParameter */
