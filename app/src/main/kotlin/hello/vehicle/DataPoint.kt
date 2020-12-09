package hello.vehicle

import kotlinx.serialization.Serializable

@Serializable
data class DataPoint(
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val heading: Int,
        val session: String,
        val vehicle: String
)
