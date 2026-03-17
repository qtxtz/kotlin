// IGNORE_TREE_ACCESS: KT-64899
// IGNORE_BODY_CALCULATOR: KT-85026

interface MyInterface {
    fun foo()
}

val localProperty: Any = object : MyInterface {
    override fun foo() {}
}

localProperty as MyInterface

class MyClass : MyInterface by localProperty
