// IGNORE_TREE_ACCESS: KT-64899
// IGNORE_BODY_CALCULATOR: KT-85026

interface Base {
    interface Derived : Base
    class Foo : Base
    object Bar : Base
    abstract class Baz : Base
}
