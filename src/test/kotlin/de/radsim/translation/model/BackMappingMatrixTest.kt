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

import de.cyface.model.osm.OsmTag
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `segregated designated path should back-map to all categories without stall`(): List<DynamicTest> {
        // [BIK-2092] Reproducer: way 26852577 in Muenster aborted the whole base net job.
        // The way is a segregated designated path, so `bicycleWayRight` classifies it as
        // BICYCLE_WAY through `bicycle=designated` + `foot=designated` + `segregated=yes`.
        // The BICYCLE_WAY -> BICYCLE_LANE rule only set `highway=secondary` + `cycleway=lane`,
        // which leaves that triple intact, so the category never changed and the engine stalled.
        val targets = SimplifiedBikeInfrastructure.entries.filter {
            it != SimplifiedBikeInfrastructure.BICYCLE_WAY
        }

        return targets.map { to ->
            DynamicTest.dynamicTest("BICYCLE_WAY (segregated designated path) → $to") {
                assertDoesNotThrow {
                    RadSimDeltaEngine.computeDelta(
                        currentTags = SEGREGATED_DESIGNATED_PATH,
                        key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                        value = to.value
                    )
                }
            }
        }
    }

    @Test
    fun `segregated designated path to BICYCLE_LANE should clear the path signature`() {
        // [BIK-2092] The delta must remove `segregated`, otherwise `bicycleWayRight` still
        // matches and the way stays BICYCLE_WAY.
        val delta = RadSimDeltaEngine.computeDelta(
            currentTags = SEGREGATED_DESIGNATED_PATH,
            key = SimplifiedBikeInfrastructure.RADSIM_TAG,
            value = SimplifiedBikeInfrastructure.BICYCLE_LANE.value
        )

        val result = BikeInfrastructure.toRadSim(apply(SEGREGATED_DESIGNATED_PATH, delta)).simplified
        assertEquals(SimplifiedBikeInfrastructure.BICYCLE_LANE, result)
    }

    @Test
    fun `segregated designated path to BUS_LANE should clear the path signature`() {
        // [BIK-2092] Same rule family as BICYCLE_LANE: the bus lane marking also sits on the
        // carriageway, so the designated path signature must go.
        val delta = RadSimDeltaEngine.computeDelta(
            currentTags = SEGREGATED_DESIGNATED_PATH,
            key = SimplifiedBikeInfrastructure.RADSIM_TAG,
            value = SimplifiedBikeInfrastructure.BUS_LANE.value
        )

        val result = BikeInfrastructure.toRadSim(apply(SEGREGATED_DESIGNATED_PATH, delta)).simplified
        assertEquals(SimplifiedBikeInfrastructure.BUS_LANE, result)
    }

    @TestFactory
    fun `side specific bike path should back-map to all categories without stall`(): List<DynamicTest> {
        // [BIK-2092] A side-specific `cycleway:right=track` also makes `isBikePathRight` true.
        // The BICYCLE_WAY -> BICYCLE_LANE rule sets the bare `cycleway` key only, so the
        // side-specific tag survives and keeps the way in BICYCLE_WAY.
        val sideSpecificBikePath = mapOf(
            "highway" to "secondary",
            "cycleway:right" to "track",
            "segregated" to "yes",
            "@id" to "26852578",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )
        val targets = SimplifiedBikeInfrastructure.entries.filter {
            it != SimplifiedBikeInfrastructure.BICYCLE_WAY
        }

        return targets.map { to ->
            DynamicTest.dynamicTest("BICYCLE_WAY (cycleway:right=track) → $to") {
                assertDoesNotThrow {
                    RadSimDeltaEngine.computeDelta(
                        currentTags = sideSpecificBikePath,
                        key = SimplifiedBikeInfrastructure.RADSIM_TAG,
                        value = to.value
                    )
                }
            }
        }
    }

    @TestFactory
    fun `to NO should not stall when way has cycleway infra tags`(): List<DynamicTest> {
        // Every ->NO rule must remove all cycleway tags, otherwise the
        // hasExplicitBikeInfrastructure guard skips isService() and the way
        // re-classifies as bike infra instead of NO.
        val categories = SimplifiedBikeInfrastructure.entries.filter {
            it != SimplifiedBikeInfrastructure.NO
        }
        val cyclewayKeys = listOf("cycleway", "cycleway:right", "cycleway:both")

        return categories.flatMap { from ->
            cyclewayKeys.map { key ->
                DynamicTest.dynamicTest("$from → NO with $key=track") {
                    val context = minimalContextFor(from) + mapOf(key to "track")
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

    /**
     * Apply a back-mapping delta to a tag set, the same way the caller of the engine does.
     * An empty value means the caller must remove the tag.
     */
    private fun apply(tags: Map<String, Any>, delta: Set<OsmTag>): Map<String, Any> {
        val updated = tags.toMutableMap()
        delta.forEach { tag ->
            val value = tag.value
            if (value is String && value.isEmpty()) updated.remove(tag.key) else updated[tag.key] = value
        }
        return updated
    }

    companion object {
        /**
         * Tags of way 26852577 in Muenster, which stalled the back-mapping. [BIK-2092]
         */
        private val SEGREGATED_DESIGNATED_PATH = mapOf(
            "highway" to "path",
            "bicycle" to "designated",
            "foot" to "designated",
            "segregated" to "yes",
            "cycleway:surface" to "paving_stones",
            "cycleway:width" to "1.2",
            "footway:surface" to "paving_stones",
            "surface" to "paving_stones",
            "lcn" to "yes",
            "oneway" to "yes",
            "@id" to "26852577",
            "base_id" to "1",
            "type" to "segment",
            "segment_length" to "10",
        )
    }
}
