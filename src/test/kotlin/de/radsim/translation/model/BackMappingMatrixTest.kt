/*
 * Copyright (C) 2025 Cyface GmbH
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

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class BackMappingMatrixTest {

    @TestFactory
    fun `all infrastructure combinations should back-map without recursion or stall`(): List<DynamicTest> {
        val values = SimplifiedBikeInfrastructure.entries

        return values.flatMap { from ->
            values.filter { it != from }.map { to ->
                DynamicTest.dynamicTest("$from → $to") {
                    assertDoesNotThrow {
                        // Important: realistic context, emptyMap can disrupt detection of `from`
                        val context = minimalContextFor(from)

                        RadSimDeltaEngine.computeDelta(
                            currentTags = context,
                            key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                            value = to.value
                        )
                    }
                }
            }
        }
    }

    /**
     * Provide minimal tags so `from` is actually recognized as current infra.
     * Without this, emptyMap() always resolves to NO, causing invalid paths.
     */
    private fun minimalContextFor(from: SimplifiedBikeInfrastructure): Map<String, Any> =
        when (from) {
            SimplifiedBikeInfrastructure.BICYCLE_ROAD ->
                mapOf("bicycle_road" to "yes")

            SimplifiedBikeInfrastructure.CYCLE_HIGHWAY ->
                mapOf("cycle_highway" to "yes")

            SimplifiedBikeInfrastructure.BICYCLE_WAY ->
                mapOf("highway" to "cycleway") // generic bike_way signature

            SimplifiedBikeInfrastructure.BICYCLE_LANE ->
                mapOf("cycleway" to "lane")

            SimplifiedBikeInfrastructure.BUS_LANE ->
                mapOf("cycleway" to "share_busway")

            SimplifiedBikeInfrastructure.MIXED_WAY ->
                mapOf("highway" to "footway", "bicycle" to "yes", "segregated" to "no")

            SimplifiedBikeInfrastructure.NO ->
                emptyMap() // NO has no signature
        }
}
