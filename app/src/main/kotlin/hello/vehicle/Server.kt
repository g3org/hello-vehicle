package hello.vehicle

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spark.Request
import spark.kotlin.Http
import spark.kotlin.halt
import spark.kotlin.ignite

class Server {

    val SECRET = "123bf42"
    val VEHICLE_ID_PARAM = ":vehicleId"
    val SESSION_ID_PARAM = ":sessionId"
    val APPLICATION_JSON = "application/json"

    val dataStore = DataStore()
    var http: Http? = null

    init {

        http = ignite()
        http?.before {
            if (!isAuthenticated(request)) {
                halt(401, "you need to be authenticated")
            }
        }

        http?.get("/status") {
            response.type(APPLICATION_JSON)
            val status = mapOf("health" to "green", "dataPoints" to "${dataStore.dataPoints.size}")
            Json.encodeToString(status)
        }

        http?.put("/vehicle/$VEHICLE_ID_PARAM", APPLICATION_JSON) {
            val dataPoint = Json.decodeFromString<DataPoint>(request.body())
            dataStore.addDataPoint(dataPoint)
            ""
        }

        http?.get("/vehicle/$VEHICLE_ID_PARAM/sessions") {
            response.type(APPLICATION_JSON)
            val vehicleId = request.params(VEHICLE_ID_PARAM)
            val sessions = dataStore.sessionsOf(vehicleId) as List<String>
            Json.encodeToString(sessions)
        }

        http?.get("/session/$SESSION_ID_PARAM") {
            response.type(APPLICATION_JSON)
            val sessionId = request.params(SESSION_ID_PARAM)
            val dataPoints = dataStore.dataPointsForSession(sessionId) as List<DataPoint>
            Json.encodeToString(dataPoints)
        }

        http?.get("/vehicle/$VEHICLE_ID_PARAM/last") {
            response.type(APPLICATION_JSON)
            val vehicleId = request.params(VEHICLE_ID_PARAM)
            val dataPoint = dataStore.lastPositionOf(vehicleId)
            Json.encodeToString(dataPoint)
        }
    }

    fun isAuthenticated(request: Request): Boolean {
        val authHeader = request.headers("Authorization")
        return authHeader == "Bearer $SECRET"
    }

    fun stop() {
        http?.stop()
    }
}