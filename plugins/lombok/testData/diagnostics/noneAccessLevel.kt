// ISSUE: KT-85693

// FILE: NoneAccessLevel.java

import lombok.*;

@AllArgsConstructor(access = AccessLevel.NONE)
public class NoneAccessLevel {
    int intField;
}

// FILE: test.kt

fun usage() {
    <!INVISIBLE_REFERENCE!>NoneAccessLevel<!>(1);
}
