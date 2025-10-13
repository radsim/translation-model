/*
 * Copyright (C) 2024-2025 Cyface GmbH
 *
 * This file is part of the RadSim Translation Model.
 *
 *  The RadSim Translation Model is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The RadSim Translation Model is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with the RadSim Translation Model.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SimplifiedBikeInfrastructureTest {

    @Test
    fun `NO to BicycleRoad should produce base tags`() {
        val result = applyBackMap(SimplifiedBikeInfrastructure.BICYCLE_ROAD)
        assertEquals(
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes")),
            result
        )
    }

    @Test
    fun `NO to CycleHighway should set cycle_highway tag`() {
        val result = applyBackMap(SimplifiedBikeInfrastructure.CYCLE_HIGHWAY)
        assertEquals(setOf(OsmTag("cycle_highway", "yes")), result)
    }

    @Test
    fun `BicycleLane to MixedWay should remove lane and set shared path tags`() {
        val current = mapOf("cycleway" to "lane")
        val result = delta(
            SimplifiedBikeInfrastructure.MIXED_WAY,
            current
        )
        assertEquals(
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "yes"), // from lane removal step (R7)
                OsmTag("segregated", "no"),
                OsmTag("bicycle", "designated"), // from R22 (NO→MIXED_WAY)
                OsmTag("foot", "designated"),
                OsmTag("cycleway", ""), // communiates tag deletion
            ),
            result
        )
    }

    @Test
    fun `MixedWay to NO should return R7 tags`() {
        val current = mapOf(
            "highway" to "footway", // Or "path"
            "bicycle" to "yes",
            "segregated" to "no"
        )

        val result = delta(
            SimplifiedBikeInfrastructure.NO,
            current
        )

        assertEquals(
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "")
            ),
            result
        )
    }

    @Test
    fun `BicycleWay to BicycleLane should apply highway+cycleway lane`() {
        val current = mapOf("highway" to "cycleway")
        val result = delta(
            SimplifiedBikeInfrastructure.BICYCLE_LANE,
            current
        )
        assertEquals(
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane")),
            result
        )
    }

    @Test
    fun `BusLane to CycleHighway should only add cycle_highway tag`() {
        val result = applyBackMap(SimplifiedBikeInfrastructure.CYCLE_HIGHWAY)
        assertEquals(setOf(OsmTag("cycle_highway", "yes")), result)
    }

    @Test
    fun `NO to NO should be idempotent`() {
        val result = delta(
            SimplifiedBikeInfrastructure.NO,
            emptyMap()
        )
        assertEquals(emptySet<OsmTag>(), result)
    }

    /**
     * Convenience wrapper around recursiveBackMap using empty starting tag set.
     */
    private fun applyBackMap(
        to: SimplifiedBikeInfrastructure
    ): Set<OsmTag> = delta(to)

    private fun delta(
        to: SimplifiedBikeInfrastructure,
        currentTags: Map<String, Any> = emptyMap()
    ): Set<OsmTag> {
        return RadSimDeltaEngine.computeDelta(
            currentTags = currentTags,
            key = SimplifiedBikeInfrastructure.RADSIM_TAG,
            value = to.value
        )
    }
}
