declare namespace JS_TESTS {
    type Nullable<T> = T | null | undefined
    function KtSingleton<T>(): T & (abstract new() => any);
    namespace foo {
        class Simple /* implements kotlin.Annotation */ {
            constructor();
            equals(other: Nullable<any>): boolean;
            hashCode(): number;
            toString(): string;
        }
        namespace Simple {
            /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
            namespace $metadata$ {
                const constructor: abstract new () => Simple;
            }
        }
        class WithStringParam /* implements kotlin.Annotation */ {
            constructor(message: string);
            get message(): string;
            equals(other: Nullable<any>): boolean;
            hashCode(): number;
            toString(): string;
        }
        namespace WithStringParam {
            /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
            namespace $metadata$ {
                const constructor: abstract new () => WithStringParam;
            }
        }
        class WithMultipleParams /* implements kotlin.Annotation */ {
            constructor(name: string, count: number);
            get name(): string;
            get count(): number;
            equals(other: Nullable<any>): boolean;
            hashCode(): number;
            toString(): string;
        }
        namespace WithMultipleParams {
            /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
            namespace $metadata$ {
                const constructor: abstract new () => WithMultipleParams;
            }
        }
        class WithDefaultValue /* implements kotlin.Annotation */ {
            constructor(level?: number);
            get level(): number;
            equals(other: Nullable<any>): boolean;
            hashCode(): number;
            toString(): string;
        }
        namespace WithDefaultValue {
            /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
            namespace $metadata$ {
                const constructor: abstract new () => WithDefaultValue;
            }
        }
        class WithBooleanParam /* implements kotlin.Annotation */ {
            constructor(enabled: boolean);
            get enabled(): boolean;
            equals(other: Nullable<any>): boolean;
            hashCode(): number;
            toString(): string;
        }
        namespace WithBooleanParam {
            /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
            namespace $metadata$ {
                const constructor: abstract new () => WithBooleanParam;
            }
        }
        function box(): string;
    }
}
