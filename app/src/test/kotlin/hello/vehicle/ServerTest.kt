package hello.vehicle

import io.restassured.RestAssured
import org.junit.After
import org.junit.Test

class ServerTest {

    var server: Server? = null

    @Test
    fun shouldStartupTheServer() {
        // given
        val statusUrl = "http://localhost:4567/status"
        val authMessage = "you need to be authenticated"
        // when
        server = Server()
        // then
        Thread.sleep(50)
        val body = RestAssured.`when`().get(statusUrl).then()
                .statusCode(401)
                .extract()
                .body()
        assert(body.asString() == authMessage)
    }

    @After
    fun tearDown() {
        server?.stop()
    }
}