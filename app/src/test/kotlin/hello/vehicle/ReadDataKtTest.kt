package hello.vehicle

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ReadDataKtTest {

    private val csvFile = "data.csv"

    @Test
    fun shouldReadFileFromResources() {
        // given
        val readData = ReadData(csvFile)
        // when
        val fileContents = readData.readFileFromResources(readData.csvFile)
        // then
        assertThat(fileContents).isNotBlank()
    }

    @Test
    fun shouldRealdDataFromFile() {
        // given
        val readData = ReadData(csvFile)
        // when
        val dataFromCSV = readData.readDataFromCSV()
        // then
        assertThat(dataFromCSV).isNotEmpty()
        val first = dataFromCSV.first()
        assertThat(first.heading).isEqualTo(252)
        assertThat(first.longitude).isEqualTo(13.54)
        assertThat(first.latitude).isEqualTo(57.1167)
        assertThat(first.session).isEqualTo("SESSION3")
        assertThat(first.vehicle).isEqualTo("VEHICLE11")
        assertThat(first.timestamp).isEqualTo(1519990621975)

        val last = dataFromCSV.last()
        assertThat(last.heading).isEqualTo(115)
        assertThat(last.longitude).isEqualTo(13.5527)
        assertThat(last.latitude).isEqualTo(57.1450)
        assertThat(last.session).isEqualTo("SESSION4")
        assertThat(last.vehicle).isEqualTo("VEHICLE22")
        assertThat(last.timestamp).isEqualTo(1520106503194)
    }
}


