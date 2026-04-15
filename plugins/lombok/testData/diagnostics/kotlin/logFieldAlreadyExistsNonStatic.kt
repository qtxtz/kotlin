// FULL_JDK

// FILE: test.kt

import lombok.extern.java.Log

<!LOG_PROPERTY_ALREADY_EXISTS!>@Log<!>
class LogExampleWithDirectLogField {
    val log = "No log"
}

@Log // No warning
class LogAndExtensionProperty

val LogAndExtensionProperty.log: String
    get() = "No log"

// FILE: lombok.config

lombok.log.fieldIsStatic=false
