package hello.vehicle

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val baseUrl = "http://localhost:4567/"
val headers = mapOf<String, String>("Content-Type" to "application/json", "Authorization" to "Bearer 123bf42")

fun main() {
    val dataPoints = ReadData("data.csv").readDataFromCSV()
    dataPoints.forEach { import(it) }
    println("number of data points from server: ${fetchNumberOfDataPoints()}")
}

fun import(dataPoint: DataPoint): Boolean {
    println("importing data point $dataPoint")
    val response = khttp.put("$baseUrl/vehicle/${dataPoint.vehicle}", headers = headers, data = Json.encodeToString(dataPoint))
    return response.statusCode == 200
}

fun fetchNumberOfDataPoints(): Int {
    val response = khttp.get("$baseUrl/status", headers = headers)
    return response.jsonObject.getInt("dataPoints")
}