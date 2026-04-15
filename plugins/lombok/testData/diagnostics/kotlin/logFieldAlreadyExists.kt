// FULL_JDK

import lombok.extern.java.Log

<!LOG_PROPERTY_ALREADY_EXISTS!>@Log<!>
class LogOnOuterClassWhenItsCompanionHasLogField {
    companion object MyCompanion {
        val log = "No log"
    }
}

class LogOnCompanionWhenCompanionHasLogField {
    <!LOG_PROPERTY_ALREADY_EXISTS!>@Log<!>
    companion object MyCompanion {
        val log = "No log"
    }
}

