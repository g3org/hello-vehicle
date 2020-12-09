package hello.vehicle

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class DataStoreTest {

    var dataStore = DataStore()
    val dataPoints = listOf<DataPoint>(
            DataPoint(1003, 1.0, 2.0, 12, "s1", "v1"),
            DataPoint(1000, 1.0, 2.0, 12, "s1", "v1"),
            DataPoint(1004, 1.0, 2.0, 12, "s1", "v1"),
            DataPoint(1002, 1.0, 2.0, 12, "s1", "v1"),
            DataPoint(1001, 1.0, 2.0, 12, "s1", "v1"),
            DataPoint(2000, 1.0, 2.0, 12, "s2", "v1"),
            DataPoint(2001, 1.0, 2.0, 12, "s2", "v1"),
            DataPoint(1002, 1.0, 2.0, 12, "s3", "v2"),
            DataPoint(1003, 1.0, 2.0, 12, "s3", "v2")
    )

    @Before
    fun setUp() {
        dataStore.dataPoints.clear()
        dataPoints.forEach { dataStore.addDataPoint(it) }
    }

    @Test
    fun shouldAddDataPointsOnlyOnce() {
        // given
        // when
        dataPoints.forEach { dataStore.addDataPoint(it) }
        // then
        dataPoints.forEach {
            assertThat(dataStore.dataPoints).contains(it)
        }
        assertThat(dataStore.dataPoints).hasSameSizeAs(dataPoints)
    }

    @Test
    fun shouldReturnSessionsForVehicle() {
        // given
        // when
        val sessions = dataStore.sessionsOf("v1")
        // then
        assertThat(sessions).containsExactly("s1", "s2")
    }

    @Test
    fun shouldReturnDataPointsForSessionSortedByTimestamp() {
        // given
        val expectedTimestampsInOrder = listOf(1000L, 1001L, 1002L, 1003L, 1004L)
        // when
        val sessionPoints = dataStore.dataPointsForSession("s1") as List
        // then
        expectedTimestampsInOrder.forEachIndexed { index, timestamp ->
            assertThat(sessionPoints.get(index).timestamp).isEqualTo(timestamp)
        }
        assertThat(sessionPoints).hasSameSizeAs(expectedTimestampsInOrder)
    }

    @Test
    fun shouldReturnLastPosOfVehicle() {
        // given
        val expectedPosition = DataPoint(2001, 1.0, 2.0, 12, "s2", "v1")
        // when
        val lastPosition = dataStore.lastPositionOf("v1")
        // then
        assertThat(lastPosition).isEqualTo(expectedPosition)
    }
}