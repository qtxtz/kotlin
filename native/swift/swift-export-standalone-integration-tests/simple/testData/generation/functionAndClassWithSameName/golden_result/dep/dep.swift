@_exported import ExportedKotlinPackages
@_implementationOnly import KotlinBridges_dep
import KotlinRuntime
import KotlinRuntimeSupport

public extension ExportedKotlinPackages.test.factory.modules {
    public final class ClassFromDependency: KotlinRuntime.KotlinBase, KotlinRuntimeSupport._KotlinBridged {
        public override init() {
            let __kt = test_factory_modules_ClassFromDependency_init_allocate()
            super.init(__externalRCRef: __kt)
            test_factory_modules_ClassFromDependency_init_initialize__TypesOfArguments__Swift_UnsafeMutableRawPointer__(__kt)
        }
        package override init(
            __externalRCRef: Swift.UnsafeMutableRawPointer?
        ) {
            super.init(__externalRCRef: __externalRCRef)
        }
    }
}
