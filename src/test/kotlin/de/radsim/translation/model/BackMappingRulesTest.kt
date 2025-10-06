package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// AI generated
class BackMappingRulesTest {

    private fun tags(vararg pairs: Pair<String, String>) =
        pairs.map { (k, v) -> OsmTag(k, v) }.toSet()

    // --- R1–R7 -------------------------------------------------------------

    @Test
    fun `R1 - create bicycle road`() {
        val expected = tags("highway" to "residential", "bicycle_road" to "yes")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.NO,
            SimplifiedBikeInfrastructure.BICYCLE_ROAD,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R2 - remove bicycle_road`() {
        val expected = tags("bicycle_road" to "")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_ROAD,
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            emptyMap()
        ).filter { it.key == "bicycle_road" }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `R3 - highway=cycleway segregated=yes`() {
        val expected = tags("highway" to "cycleway", "segregated" to "yes")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_ROAD,
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            emptyMap()
        ).filter { it.key != "bicycle_road" }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `R7 - highway=path bicycle=NULL`() {
        val expected = tags("highway" to "path", "bicycle" to "")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            SimplifiedBikeInfrastructure.NO,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    // --- R8–R10 ------------------------------------------------------------

    @Test
    fun `R8 - bicycle_way to bicycle_lane`() {
        val expected = tags("highway" to "secondary", "cycleway" to "lane")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            SimplifiedBikeInfrastructure.BICYCLE_LANE,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R10 - bicycle_way to mixed way, highway=cycleway`() {
        val expected = tags("foot" to "yes", "segregated" to "no")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            SimplifiedBikeInfrastructure.MIXED_WAY,
            mapOf("highway" to "cycleway")
        )
        assertEquals(expected, actual)
    }

    // --- R11–R13 -----------------------------------------------------------

    @Test
    fun `R11 - bicycle_lane to bicycle_way`() {
        val expected = tags("highway" to "cycleway", "segregated" to "yes")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BICYCLE_LANE,
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R13 - bus_lane to mixed way`() {
        val expected = tags("highway" to "path", "bicycle" to "yes", "segregated" to "no")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BUS_LANE,
            SimplifiedBikeInfrastructure.MIXED_WAY,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    // --- R14–R18 -----------------------------------------------------------

    @Test
    fun `R15 - bus_lane to bicycle_lane`() {
        val expected = tags("cycleway" to "lane")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.BUS_LANE,
            SimplifiedBikeInfrastructure.BICYCLE_LANE,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    // --- R19–R22 (NO to others) --------------------------------------------

    @Test
    fun `R19 - no to bicycle_way`() {
        val expected = tags(
            "bicycle" to "designated",
            "foot" to "designated",
            "segregated" to "yes"
        )
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.NO,
            SimplifiedBikeInfrastructure.BICYCLE_WAY,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R20 - no to bicycle_lane`() {
        val expected = tags("cycleway" to "lane")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.NO,
            SimplifiedBikeInfrastructure.BICYCLE_LANE,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R21 - no to bus_lane`() {
        val expected = tags("cycleway" to "share_busway")
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.NO,
            SimplifiedBikeInfrastructure.BUS_LANE,
            emptyMap()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `R22 - no to mixed way`() {
        val expected = tags(
            "bicycle" to "designated",
            "foot" to "designated",
            "segregated" to "no",
            "highway" to "path"
        )
        val actual = BackMappingRules.applyRule(
            SimplifiedBikeInfrastructure.NO,
            SimplifiedBikeInfrastructure.MIXED_WAY,
            emptyMap()
        )
        assertEquals(expected, actual)
    }
}
