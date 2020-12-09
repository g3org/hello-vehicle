package hello.vehicle

class DataStore {

    var dataPoints: MutableCollection<DataPoint> = mutableListOf()

    fun addDataPoint(dataPoint: DataPoint) {
        if (!dataPoints.contains(dataPoint)) {
            dataPoints.add(dataPoint)
        }
    }

    fun sessionsOf(vehicleId: String): Collection<String> {
        return dataPoints.filter { it.vehicle == vehicleId }.map { it.session }.distinct()
    }

    fun dataPointsForSession(sessionId: String): Collection<DataPoint> {
        return dataPoints.filter { it.session == sessionId }.sortedBy { it.timestamp }
    }

    fun lastPositionOf(vehicleId: String): DataPoint {
        return dataPoints.filter { it.vehicle == vehicleId }.sortedBy { it.timestamp }.last()
    }
}