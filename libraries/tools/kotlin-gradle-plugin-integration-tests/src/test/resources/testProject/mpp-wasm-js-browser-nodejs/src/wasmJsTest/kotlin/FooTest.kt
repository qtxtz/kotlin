import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.Ignore

class TestClient {
    @Test
    fun testGreet() {
        assertFalse("No require found", ::checkRequire)
    }
}

@JsFun("() => require === undefined")
external fun checkRequire(): Boolean
