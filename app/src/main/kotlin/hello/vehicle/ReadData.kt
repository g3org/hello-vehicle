package hello.vehicle

class ReadData(val csvFile: String) {

    fun readFileFromResources(fileName: String): String {
        val resource = this.javaClass.getResource(fileName)
        return resource.readText(Charsets.UTF_8)
    }

    fun dataPointOf(line: String): DataPoint {
        val fields = line.split(",")
        val timestamp: Long = fields.get(0).toLong()
        val vehicleId: String = fields.get(1)
        val sessionId: String = fields.get(2)
        val latitude: Double = fields.get(3).toDouble()
        val longitude: Double = fields.get(4).toDouble()
        val heading: Int = fields.get(5).toInt()
        return DataPoint(
                timestamp,
                latitude,
                longitude,
                heading,
                sessionId,
                vehicleId
        )
    }

    fun readDataFromCSV(): List<DataPoint> {
        val fileContents: String = readFileFromResources(csvFile)
        val lines = linesWithoutHeadingOf(fileContents)
        return lines.map { dataPointOf(it) }
    }

    private fun linesWithoutHeadingOf(fileContents: String): List<String> {
        var lines = fileContents.split("\n")
        return lines.slice(1..lines.size - 1)
    }
}