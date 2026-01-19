/*
 * Copyright (C) 2019-2024 Cyface GmbH
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class NumberOfLanesTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: NumberOfLanesParameters) {
        // Arrange
        val result = NumberOfLanes.toRadSim(parameter.osmTags)

        // Assert
        assert(result == parameter.expectedLanes) {
            "Expected ${parameter.expectedLanes} lanes for tags ${parameter.osmTags}, but got $result"
        }
    }

    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingLanesParameters) {
        // Arrange
        // Act
        val result = NumberOfLanes.backMappingTag(parameter.numberOfLanes)

        // Assert
        assert(result == parameter.osmTag) {
            "Expected ${parameter.osmTag} for ${parameter.numberOfLanes} lanes, but got $result"
        }
    }

    @Suppress("SpellCheckingInspection")
    companion object {
        /**
         * @return All test setup to run
         */
        @JvmStatic
        fun testParameters(): Stream<NumberOfLanesParameters> {
            return Stream.of(
                // Direct lanes tag
                NumberOfLanesParameters(mapOf("lanes" to "1"), 1),
                NumberOfLanesParameters(mapOf("lanes" to "2"), 2),
                NumberOfLanesParameters(mapOf("lanes" to "3"), 3),
                NumberOfLanesParameters(mapOf("lanes" to "4"), 4),
                NumberOfLanesParameters(mapOf("lanes" to "6"), 6),

                // lanes:forward and lanes:backward (sum)
                NumberOfLanesParameters(mapOf("lanes:forward" to "1", "lanes:backward" to "1"), 2),
                NumberOfLanesParameters(mapOf("lanes:forward" to "2", "lanes:backward" to "2"), 4),
                NumberOfLanesParameters(mapOf("lanes:forward" to "3", "lanes:backward" to "1"), 4),
                NumberOfLanesParameters(mapOf("lanes:forward" to "2"), 2),
                NumberOfLanesParameters(mapOf("lanes:backward" to "3"), 3),

                // Highway-based fallback: 0 lanes (no car access)
                NumberOfLanesParameters(mapOf("highway" to "footway"), 0),
                NumberOfLanesParameters(mapOf("highway" to "steps"), 0),
                NumberOfLanesParameters(mapOf("highway" to "cycleway"), 0),
                NumberOfLanesParameters(mapOf("highway" to "pedestrian"), 0),
                NumberOfLanesParameters(mapOf("highway" to "platform"), 0),
                NumberOfLanesParameters(mapOf("highway" to "elevator"), 0),
                NumberOfLanesParameters(mapOf("highway" to "bridleway"), 0),
                NumberOfLanesParameters(mapOf("highway" to "bus_stop"), 0),

                // Highway-based fallback: 1 lane
                NumberOfLanesParameters(mapOf("highway" to "track"), 1),
                NumberOfLanesParameters(mapOf("highway" to "path"), 1),
                NumberOfLanesParameters(mapOf("highway" to "service"), 1),
                NumberOfLanesParameters(mapOf("highway" to "residential"), 1),
                NumberOfLanesParameters(mapOf("highway" to "living_street"), 1),
                NumberOfLanesParameters(mapOf("highway" to "unclassified"), 1),
                NumberOfLanesParameters(mapOf("highway" to "construction"), 1),
                NumberOfLanesParameters(mapOf("highway" to "proposed"), 1),
                NumberOfLanesParameters(mapOf("highway" to "rest_area"), 1),
                NumberOfLanesParameters(mapOf("highway" to "raceway"), 1),

                // Highway-based fallback: 1 lane (link roads)
                NumberOfLanesParameters(mapOf("highway" to "tertiary_link"), 1),
                NumberOfLanesParameters(mapOf("highway" to "secondary_link"), 1),
                NumberOfLanesParameters(mapOf("highway" to "primary_link"), 1),
                NumberOfLanesParameters(mapOf("highway" to "trunk_link"), 1),

                // Highway-based fallback: 2 lanes
                NumberOfLanesParameters(mapOf("highway" to "tertiary"), 2),
                NumberOfLanesParameters(mapOf("highway" to "secondary"), 2),
                NumberOfLanesParameters(mapOf("highway" to "primary"), 2),

                // Default fallback: 1 lane for unknown highway types
                NumberOfLanesParameters(mapOf("highway" to "motorway"), 1),
                NumberOfLanesParameters(mapOf("highway" to "trunk"), 1),
                NumberOfLanesParameters(mapOf("highway" to "motorway_link"), 1),

                // Ultimate fallback: 1 lane when no relevant tags
                NumberOfLanesParameters(mapOf(), 1),
                NumberOfLanesParameters(mapOf("surface" to "asphalt"), 1),

                // Hierarchical priority tests: lanes tag takes priority over everything
                NumberOfLanesParameters(
                    mapOf("lanes" to "4", "highway" to "footway"),
                    4
                ),
                NumberOfLanesParameters(
                    mapOf("lanes" to "3", "lanes:forward" to "1", "lanes:backward" to "1"),
                    3
                ),
                NumberOfLanesParameters(
                    mapOf("lanes" to "2", "highway" to "primary"),
                    2
                ),

                // Hierarchical priority: lanes:forward/backward takes priority over highway
                NumberOfLanesParameters(
                    mapOf("lanes:forward" to "3", "lanes:backward" to "2", "highway" to "footway"),
                    5
                ),
                NumberOfLanesParameters(
                    mapOf("lanes:forward" to "1", "highway" to "primary"),
                    1
                ),

                // Edge case: invalid lanes value should fall back
                NumberOfLanesParameters(
                    mapOf("lanes" to "invalid", "highway" to "tertiary"),
                    2
                ),
                NumberOfLanesParameters(
                    mapOf("lanes" to "invalid", "lanes:forward" to "2"),
                    2
                ),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingLanesParameters> {
            return Stream.of(
                BackMappingLanesParameters(0, OsmTag("lanes", "0")),
                BackMappingLanesParameters(1, OsmTag("lanes", "1")),
                BackMappingLanesParameters(2, OsmTag("lanes", "2")),
                BackMappingLanesParameters(3, OsmTag("lanes", "3")),
                BackMappingLanesParameters(4, OsmTag("lanes", "4")),
                BackMappingLanesParameters(-1, null),
            )
        }
    }
}

class NumberOfLanesParameters(
    val osmTags: Map<String, Any>,
    val expectedLanes: Int,
)

class BackMappingLanesParameters(
    val numberOfLanes: Int,
    val osmTag: OsmTag?,
)
