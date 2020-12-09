package hello.vehicle.features

import hello.vehicle.DataPoint
import hello.vehicle.Server
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers

class HelloVehicleStepDefs : En {

    val apiToken = "123bf42"
    val baseUrl = "http://localhost:4567"
    val dataPoints = mutableListOf<DataPoint>()

    var server: Server? = null
    var dataCache: MutableMap<String, Any> = mutableMapOf()

    init {

        Before { _ ->
            dataCache.clear()
            server = Server()
            Thread.sleep(50)
        }

        After { _ ->
            server?.stop()
            Thread.sleep(30)
            server = null
        }

        Given("the scenario considers following data points:") { data: DataTable ->
            for (row in data.asMaps()) {
                val dataPoint = extractDataPoint(row)
                dataPoints.add(dataPoint)
            }
        }

        Given("the server is running") {
            assertServerHealth(healthStatus = "green")
        }

        When("retrieving server status with auth header {string}") { authHeaderSpec: String ->
            if ("none" == authHeaderSpec) {
                dataCache.put("req-spec", RestAssured.`when`())
            } else if ("diverging" == authHeaderSpec) {
                dataCache.put("req-spec", RestAssured.given().authenticateAPI("667-neighbour-of-the-beast"))
            } else if ("correct" == authHeaderSpec) {
                dataCache.put("req-spec", RestAssured.given().authenticateAPI())
            }
        }

        Then("the API responds with statusCode {int}") { expectedStatusCode: Int ->
            val requestSpecification = dataCache.get("req-spec") as RequestSpecification
            requestSpecification.`when`().get("$baseUrl/status")
                    .then().statusCode(expectedStatusCode)
        }

        When("data is imported via API") {
            importDataPoints()
        }

        Then("the server holds a list of all data points") {
            getServerStatus()
                    .then()
                    .statusCode(200)
                    .body("dataPoints", Matchers.equalTo(dataPoints.size.toString()))
        }

        Given("the data is imported") {
            importDataPoints()
        }

        When("all sessions for vehicle {string} are retrieved") { vehicleId: String ->
            dataCache.putIfAbsent("vehicleId", vehicleId)
        }

        Then("the API returns sessions {string}") { sessionIds: String ->
            val sessions = sessionIds.split(",")
            val vehicleId = dataCache.get("vehicleId")!!
            val body = getBodyAsString("$baseUrl/vehicle/$vehicleId/sessions")
            val responseSessions = Json.decodeFromString<List<String>>(body);
            assertThat(sessions.all { responseSessions.contains(it) }).isTrue()
        }

        When("all data points for session {string} are retrieved") { sessionId: String ->
            val body = getBodyAsString("$baseUrl/session/$sessionId")
            dataCache.put("body_session", body)
        }

        Then("the API returns the following _ordered_ list of data points:") { dataTable: DataTable ->
            val dataPoints = Json.decodeFromString<List<DataPoint>>(dataCache.get("body_session")!! as String)
            dataTable.asMaps().forEachIndexed { index, mutableMap ->
                assertThat(dataPoints.get(index)).isEqualTo(extractDataPoint(row = mutableMap))
            }
            assertThat(dataTable.asMaps().size).isEqualTo(dataPoints.size)
        }

        When("the last position of vehicle {string} is retrieved") { vehicleId: String ->
            val body = getBodyAsString("$baseUrl/vehicle/$vehicleId/last")
            dataCache.put("body_lastpos", body)
        }

        Then("the API returns the data point with timestamp {string}") { timestamp: String ->
            val dataPoint = Json.decodeFromString<DataPoint>(dataCache.get("body_lastpos")!! as String)
            val expected = dataPoints.find { it.timestamp == timestamp.toLong() }
            assertThat(dataPoint).isEqualTo(expected)
            println(Json.encodeToString(dataPoint))
        }

    }

    fun RequestSpecification.authenticateAPI(): RequestSpecification {
        this.authenticateAPI(apiToken)
        return this
    }

    fun RequestSpecification.authenticateAPI(token: String): RequestSpecification {
        this.header("Authorization", "Bearer $token")
        return this
    }

    private fun getBodyAsString(url: String): String {
        val body = RestAssured.given()
                .authenticateAPI()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body()
        return body.asString()
    }

    private fun importDataPoints() {
        for (dataPoint in dataPoints) {
            RestAssured.given()
                    .authenticateAPI()
                    .contentType(ContentType.JSON)
                    .body(Json.encodeToString(dataPoint))
                    .`when`()
                    .put("$baseUrl/vehicle/${dataPoint.vehicle}")
                    .then()
                    .statusCode(200)
        }
    }

    private fun extractDataPoint(row: Map<String, String>): DataPoint {
        return DataPoint(
                row.get("timestamp")!!.toLong(),
                row.get("latitude")!!.toDouble(),
                row.get("longitude")!!.toDouble(),
                row.get("heading")!!.toInt(),
                row.get("sessionId")!!,
                row.get("vehicleId")!!
        )
    }

    private fun assertServerHealth(healthStatus: String) {
        getServerStatus()
                .then()
                .statusCode(200)
                .body("health", Matchers.equalTo(healthStatus))

    }

    private fun getServerStatus() = RestAssured.given().authenticateAPI().`when`().get("$baseUrl/status")
}