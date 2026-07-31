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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class BackMappingMatrixTest {

    @Test
    fun `NO to CYCLE_HIGHWAY should not stall when current way is a path`() {
        // Reproducer: highway=path with bicycle=yes, foot=yes, segregated=yes is classified as
        // SERVICE_MISC (simplified: NO). Adding cycle_highway=yes should transition to CYCLE_HIGHWAY,
        // but isService() was checked before isCycleHighway() in toRadSim(), so the updated tags
        // still matched isService() first, causing a stall.
        val pathTags = mapOf(
            "highway" to "path",
            "bicycle" to "yes",
            "foot" to "yes",
            "segregated" to "yes",
            "@id" to "11917677",
            "base_id" to "147300297",
            "type" to "segment",
            "segment_length" to "13.67",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = pathTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.CYCLE_HIGHWAY.value
            )
        }
    }

    @Test
    fun `NO to CYCLE_HIGHWAY should not stall when way has access=no`() {
        // All 89 stalls from bulk back-mapping test had access=no. The access=no check in
        // toRadSim() returned NO before reaching isCycleHighway(), so adding cycle_highway=yes
        // didn't change the classification.
        val accessNoTags = mapOf(
            "highway" to "service",
            "access" to "no",
            "@id" to "1420897",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = accessNoTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.CYCLE_HIGHWAY.value
            )
        }
    }

    @Test
    fun `NO to BICYCLE_LANE should not stall when way has access=no`() {
        // [BIK-2058] Reproducer: way 18030960 in Zwickau, classified as NO due to access=no.
        // R20 adds cycleway=lane but the access=no check in toRadSim() returned NO before
        // reaching isBikeLaneRight(), causing a stall.
        val accessNoTags = mapOf(
            "highway" to "secondary",
            "access" to "no",
            "@id" to "18030960",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = accessNoTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.BICYCLE_LANE.value
            )
        }
    }

    @Test
    fun `NO to BICYCLE_LANE should not stall when current way is a service road`() {
        // isService() matches highway=service before isBikeLaneRight() is reached.
        val serviceTags = mapOf(
            "highway" to "service",
            "@id" to "99999",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = serviceTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.BICYCLE_LANE.value
            )
        }
    }

    @Test
    fun `NO to BUS_LANE should not stall when way has access=no`() {
        // Same class of bug as NO->BICYCLE_LANE: R21 adds cycleway=share_busway but
        // access=no check fires first.
        val accessNoTags = mapOf(
            "highway" to "secondary",
            "access" to "no",
            "@id" to "99998",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = accessNoTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.BUS_LANE.value
            )
        }
    }

    @Test
    fun `NO to BICYCLE_WAY should not stall when way has access=no`() {
        // R19 sets highway=cycleway + bicycle=designated but no cycleway* tag.
        // The guard must also recognize highway=cycleway as explicit bike infra.
        val accessNoTags = mapOf(
            "highway" to "secondary",
            "access" to "no",
            "@id" to "99997",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = accessNoTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.BICYCLE_WAY.value
            )
        }
    }

    @Test
    fun `NO to MIXED_WAY should not stall when way has access=no`() {
        // R22 sets highway=path + bicycle=designated but no cycleway* tag.
        // The guard must also recognize bicycle=designated as explicit bike infra.
        val accessNoTags = mapOf(
            "highway" to "secondary",
            "access" to "no",
            "@id" to "99996",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )

        assertDoesNotThrow {
            RadSimDeltaEngine.computeDelta(
                currentTags = accessNoTags,
                key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                value = SimplifiedBikeInfrastructure.MIXED_WAY.value
            )
        }
    }

    @TestFactory
    fun `to NO should not stall when way has cycleway=track`(): List<DynamicTest> {
        // Every ->NO rule must remove the cycleway tag, otherwise the
        // hasExplicitBikeInfrastructure guard skips isService() and the way
        // re-classifies as bike infra instead of NO.
        val categories = SimplifiedBikeInfrastructure.entries.filter {
            it != SimplifiedBikeInfrastructure.NO
        }

        return categories.map { from ->
            DynamicTest.dynamicTest("$from → NO with cycleway=track") {
                val context = minimalContextFor(from) + mapOf("cycleway" to "track")
                assertDoesNotThrow {
                    RadSimDeltaEngine.computeDelta(
                        currentTags = context,
                        key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                        value = SimplifiedBikeInfrastructure.NO.value
                    )
                }
            }
        }
    }

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
                mapOf("highway" to "cycleway", "cycle_highway" to "yes")

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
