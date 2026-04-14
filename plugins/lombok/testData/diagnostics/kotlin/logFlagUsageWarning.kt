// FULL_JDK

// FILE: test.kt

import lombok.extern.java.Log

<!LOG_FLAG_USAGE_WARNING!>@Log<!>
class LogExampleWarning

class NotAnnotated

// FILE: lombok.config
lombok.log.flagUsage=warning
