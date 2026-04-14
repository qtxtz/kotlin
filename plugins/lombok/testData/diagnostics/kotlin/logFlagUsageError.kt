// FULL_JDK

// FILE: test.kt

import lombok.extern.java.Log

<!LOG_FLAG_USAGE_ERROR!>@Log<!>
class LogExampleError

class NotAnnotated

// FILE: lombok.config
lombok.log.flagUsage=error
