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

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * Tests the internal functionality of [BikeInfrastructure].
 */
class BikeInfrastructureTest {

    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: BikeInfrastructureParameters) {
        // Arrange
        val result = BikeInfrastructure.toRadSim(parameter.osmTags)

        // Assert
        assertThat(
            "Expected OSM tags ${parameter.osmTags} to map to ${parameter.expectedRadSimTag} but was $result",
            result,
            equalTo(parameter.expectedRadSimTag)
        )
    }

    /**
     * [BIK-1598] Verifies that OSM ways with `cycleway:right=track` or `cycleway:both=track`
     * on secondary roads are mapped to [SimplifiedBikeInfrastructure.BICYCLE_WAY].
     */
    @ParameterizedTest
    @MethodSource("bicycleWayBugParameters")
    fun testOsmTagsMappedToSimplifiedBicycleWay(parameter: BikeInfrastructureParameters) {
        // Act
        val result = BikeInfrastructure.toRadSim(parameter.osmTags)

        // Assert
        assertThat(
            "Expected OSM tags ${parameter.osmTags} to simplify to BICYCLE_WAY but " +
                "got ${result.simplified} (detailed: $result)",
            result.simplified,
            equalTo(SimplifiedBikeInfrastructure.BICYCLE_WAY)
        )
    }

    @ParameterizedTest
    @MethodSource("simplificationMappingParameters")
    fun testSimplificationMapping(parameter: SimplifiedInfrastructureParameters) {
        // Act
        val result = parameter.bikeInfrastructure.simplified

        // Assert
        assertThat(
            "Expected ${parameter.bikeInfrastructure} to map to ${parameter.expectedSimplified} but was $result",
            result,
            equalTo(parameter.expectedSimplified)
        )
    }

    companion object {
        /**
         * Attention:
         * - The test cases in here have been generated, they can safely be removed or replaced.
         * - If you add test cases which reproduce a bug, please specifically mark them as such, linking the Bug id.
         *
         * @return All test setup to run
         */
        @Suppress("SpellCheckingInspection")
        @JvmStatic
        @SuppressWarnings("LongMethod")
        fun testParameters(): Stream<BikeInfrastructureParameters> {
            return Stream.of(
                // Tests written for mapping as defined in `set_value() => if sides == "double"`
                // https://github.com/1prk/osm_categorizer/blob/radsim/netapy/assessor_free.py
                // OSM tags that should map to 'no'
                BikeInfrastructureParameters(mapOf("access" to "no"), BikeInfrastructure.NO),
                BikeInfrastructureParameters(mapOf("tram" to "yes"), BikeInfrastructure.NO),

                // OSM tags that should map to bicycle ways
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "designated",
                    ),
                    BikeInfrastructure.BICYCLE_WAY_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf("cycleway:right" to "track"),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_NO_LEFT
                ),
                BikeInfrastructureParameters(
                    mapOf("cycleway:left" to "track"),
                    BikeInfrastructure.BICYCLE_WAY_LEFT_NO_RIGHT
                ),
                BikeInfrastructureParameters(mapOf("cycleway" to "track"), BikeInfrastructure.BICYCLE_WAY_BOTH),

                // OSM tags that should map to bicycle lanes
                BikeInfrastructureParameters(
                    mapOf("cycleway:right" to "lane"),
                    BikeInfrastructure.BICYCLE_LANE_RIGHT_NO_LEFT
                ),
                BikeInfrastructureParameters(
                    mapOf("cycleway:left" to "lane"),
                    BikeInfrastructure.BICYCLE_LANE_LEFT_NO_RIGHT
                ),

                // OSM tags that should map to mixed ways
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "designated",
                        "foot" to "designated",
                        "segregated" to "no"
                    ),
                    BikeInfrastructure.MIXED_WAY_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "designated",
                        "foot" to "designated",
                        "segregated" to "no",
                        "cycleway:right" to "track"
                    ),
                    BikeInfrastructure.MIXED_WAY_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "designated",
                        "foot" to "yes",
                        "segregated" to "no"
                    ),
                    BikeInfrastructure.MIXED_WAY_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "designated",
                        "foot" to "yes",
                        "segregated" to "yes"
                    ),
                    BikeInfrastructure.BICYCLE_WAY_BOTH // Because segregated=yes
                ),

                // OSM tags that should map to bus lanes
                BikeInfrastructureParameters(
                    mapOf("cycleway:right" to "share_busway"),
                    BikeInfrastructure.BUS_LANE_RIGHT_NO_LEFT
                ),
                BikeInfrastructureParameters(
                    mapOf("cycleway:left" to "share_busway"),
                    BikeInfrastructure.BUS_LANE_LEFT_NO_RIGHT
                ),

                // OSM tags that should map to pedestrian paths
                BikeInfrastructureParameters(
                    mapOf("highway" to "footway"),
                    BikeInfrastructure.PEDESTRIAN_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "footway", "bicycle" to "no"),
                    BikeInfrastructure.PEDESTRIAN_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "footway", "bicycle" to "yes"),
                    BikeInfrastructure.MIXED_WAY_BOTH // Because bicycles are allowed
                ),

                // OSM tags that should map to mit roads
                BikeInfrastructureParameters(
                    mapOf("highway" to "tertiary"),
                    BikeInfrastructure.MIT_ROAD_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "primary"),
                    BikeInfrastructure.MIT_ROAD_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "residential"),
                    BikeInfrastructure.MIT_ROAD_BOTH
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "residential",
                        "sidewalk:right" to "yes"
                    ),
                    BikeInfrastructure.MIT_ROAD_BOTH
                ),

                // OSM tags that should map to cycle highway
                BikeInfrastructureParameters(
                    mapOf("cycle_highway" to "yes"),
                    BikeInfrastructure.CYCLE_HIGHWAY
                ),

                // OSM tags that should map to bicycle road
                BikeInfrastructureParameters(
                    mapOf("bicycle_road" to "yes"),
                    BikeInfrastructure.BICYCLE_ROAD
                ),
                BikeInfrastructureParameters(
                    mapOf("cyclestreet" to "yes"),
                    BikeInfrastructure.BICYCLE_ROAD
                ),

                // OSM tags that should map to path not forbidden
                BikeInfrastructureParameters(
                    mapOf("highway" to "path"),
                    BikeInfrastructure.SERVICE_MISC // Since it's considered service unless designated
                ),

                // OSM tags that should map to service_misc
                BikeInfrastructureParameters(
                    mapOf("highway" to "service"),
                    BikeInfrastructure.SERVICE_MISC
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "track"),
                    BikeInfrastructure.SERVICE_MISC
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "track", "bicycle" to "designated"),
                    BikeInfrastructure.BICYCLE_WAY_BOTH // Because bicycle=designated
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "path", "bicycle" to "yes"),
                    BikeInfrastructure.SERVICE_MISC
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "cycleway:right" to "track",
                        "sidewalk:right" to "yes"
                    ),
                    BikeInfrastructure.SERVICE_MISC
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "path",
                        "bicycle" to "yes",
                        "foot" to "yes",
                        "segregated" to "no",
                        "sidewalk:right" to "yes"
                    ),
                    BikeInfrastructure.SERVICE_MISC
                ),
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:right" to "share_busway",
                        "highway" to "path",
                        "bicycle" to "yes",
                        "foot" to "yes",
                        "segregated" to "no"
                    ),
                    BikeInfrastructure.SERVICE_MISC
                ),

                // OSM tags that should map to 'no' due to not accessible
                BikeInfrastructureParameters(
                    mapOf("highway" to "residential", "access" to "no"),
                    BikeInfrastructure.NO
                ),
                BikeInfrastructureParameters(
                    mapOf("highway" to "path", "access" to "no"),
                    BikeInfrastructure.NO
                ),

                // OSM tags that should map to 'no' due to tram=yes
                BikeInfrastructureParameters(
                    mapOf("highway" to "primary", "tram" to "yes"),
                    BikeInfrastructure.NO
                ),

                // OSM tags with indoor=yes should not be considered pedestrian
                BikeInfrastructureParameters(
                    mapOf("highway" to "footway", "indoor" to "yes"),
                    BikeInfrastructure.NO
                ),

                // Testing with cycleway=lane and sidewalks
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "residential",
                        "cycleway" to "lane",
                        "sidewalk" to "both"
                    ),
                    BikeInfrastructure.BICYCLE_LANE_BOTH
                ),

                // Testing bus lanes with mixed ways
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:right" to "share_busway",
                        "cycleway:left" to "track"
                    ),
                    // Since left is track, we need to see if mapping adjusts
                    BikeInfrastructure.BICYCLE_WAY_LEFT_BUS_RIGHT
                ),

                // Testing bicycle lanes on both sides
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway" to "lane",
                        "cycleway:both" to "lane"
                    ),
                    BikeInfrastructure.BICYCLE_LANE_BOTH
                ),

                // Testing bicycle ways on both sides with segregated
                BikeInfrastructureParameters(
                    mapOf(
                        "highway" to "cycleway",
                        "segregated" to "yes"
                    ),
                    BikeInfrastructure.BICYCLE_WAY_BOTH
                ),

                // Testing bicycle way on one side and bicycle lane on the other
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:right" to "track",
                        "cycleway:left" to "lane"
                    ),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_LANE_LEFT
                ),

                // Testing bicycle way right and pedestrian left
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:right" to "track",
                        "highway" to "footway",
                        "bicycle" to "no"
                    ),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_PEDESTRIAN_LEFT
                )
            )
        }

        /**
         * [BIK-1598] OSM ways with `cycleway:right=track` or `cycleway:both=track` on secondary
         * roads that should map to [SimplifiedBikeInfrastructure.BICYCLE_WAY].
         */
        @Suppress("SpellCheckingInspection")
        @JvmStatic
        fun bicycleWayBugParameters(): Stream<BikeInfrastructureParameters> {
            return Stream.of(
                // Way 1507166 - Lenneplatz: tags from RoadNetwork PBF as the TUD sees them without any filters
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:left" to "no",
                        "cycleway:right" to "track",
                        "cycleway:right:bicycle" to "designated",
                        "cycleway:right:oneway" to "yes",
                        "cycleway:right:segregated" to "yes",
                        "highway" to "secondary",
                        "lanes" to "2",
                        "lit" to "yes",
                        "maxspeed" to "50",
                        "name" to "Lenneplatz",
                        "oneway" to "yes",
                        "sidewalk:right" to "yes",
                        "sidewalk:right:surface" to "paving_stones",
                        "smoothness" to "excellent",
                        "surface" to "asphalt",
                    ),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_NO_LEFT
                ),
                // Way 1507166 - Lenneplatz: tags as filtered by backend's NetworkExtractor (with BIK-1598 fix)
                BikeInfrastructureParameters(
                    mapOf(
                        "@id" to "1507166",
                        "cycleway:left" to "no",
                        "cycleway:right" to "track",
                        "cycleway:right:bicycle" to "designated",
                        "cycleway:right:segregated" to "yes",
                        "highway" to "secondary",
                        "lanes" to "2",
                        "maxspeed" to "50",
                        "sidewalk:right" to "yes",
                        "surface" to "asphalt",
                        "base_id" to "24251523",
                        "type" to "segment",
                        "segment_length" to "25.86",
                    ),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_NO_LEFT
                ),
                // Way 21648974 - Gerhart-Hauptmann-Str.: cycleway:right=track (no cycleway:left)
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:right" to "track",
                        "cycleway:right:bicycle" to "designated",
                        "cycleway:right:oneway" to "yes",
                        "cycleway:right:segregated" to "yes",
                        "cycleway:right:surface" to "paving_stones",
                        "highway" to "secondary",
                        "lanes" to "1",
                        "lit" to "yes",
                        "maxspeed" to "50",
                        "name" to "Gerhart-Hauptmann-Strasse",
                        "oneway" to "yes",
                        "sidewalk:right" to "yes",
                        "sidewalk:right:surface" to "paving_stones",
                        "surface" to "asphalt",
                    ),
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_NO_LEFT
                ),
                // Way 7414471 - Gerhart-Hauptmann-Str.: cycleway:both=track
                BikeInfrastructureParameters(
                    mapOf(
                        "cycleway:both" to "track",
                        "cycleway:both:bicycle" to "designated",
                        "cycleway:both:segregated" to "yes",
                        "cycleway:left:oneway" to "-1",
                        "cycleway:right:oneway" to "yes",
                        "embedded_rails" to "tram",
                        "highway" to "secondary",
                        "lanes" to "4",
                        "lit" to "yes",
                        "name" to "Gerhart-Hauptmann-Strasse",
                        "sidewalk:both" to "yes",
                        "sidewalk:both:surface" to "paving_stones",
                        "surface" to "asphalt",
                    ),
                    BikeInfrastructure.BICYCLE_WAY_BOTH
                ),
            )
        }

        /**
         * Attention:
         * - The test cases in here have been generated, they can safely be removed or replaced.
         * - If you add test cases which reproduce a bug, please specifically mark them as such, linking the Bug id.
         */
        @JvmStatic
        fun simplificationMappingParameters(): Stream<SimplifiedInfrastructureParameters> {
            return Stream.of(
                // Cycle highway simplified to CYCLE_HIGHWAY
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.CYCLE_HIGHWAY,
                    SimplifiedBikeInfrastructure.CYCLE_HIGHWAY
                ),

                // Bicycle road simplified to BICYCLE_ROAD
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_ROAD,
                    SimplifiedBikeInfrastructure.BICYCLE_ROAD
                ),

                // Various bicycle ways simplified to BICYCLE_WAY
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_WAY_BOTH,
                    SimplifiedBikeInfrastructure.BICYCLE_WAY
                ),
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_LANE_LEFT,
                    SimplifiedBikeInfrastructure.BICYCLE_WAY
                ),
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_WAY_RIGHT_NO_LEFT,
                    SimplifiedBikeInfrastructure.BICYCLE_WAY
                ),

                // Various bicycle lanes simplified to BICYCLE_LANE
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_LANE_BOTH,
                    SimplifiedBikeInfrastructure.BICYCLE_LANE
                ),
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BICYCLE_LANE_RIGHT_MIT_LEFT,
                    SimplifiedBikeInfrastructure.BICYCLE_LANE
                ),

                // Bus lanes simplified to BUS_LANE
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BUS_LANE_BOTH,
                    SimplifiedBikeInfrastructure.BUS_LANE
                ),
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.BUS_LANE_RIGHT_NO_LEFT,
                    SimplifiedBikeInfrastructure.BUS_LANE
                ),

                // Mixed ways simplified to MIXED_WAY
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.MIXED_WAY_BOTH,
                    SimplifiedBikeInfrastructure.MIXED_WAY
                ),
                SimplifiedInfrastructureParameters(
                    BikeInfrastructure.MIXED_WAY_RIGHT_NO_LEFT,
                    SimplifiedBikeInfrastructure.MIXED_WAY
                ),

                // Default case simplified to NO
                SimplifiedInfrastructureParameters(BikeInfrastructure.NO, SimplifiedBikeInfrastructure.NO)
            )
        }
    }
}

class BikeInfrastructureParameters(
    val osmTags: Map<String, Any>,
    val expectedRadSimTag: BikeInfrastructure,
)

class SimplifiedInfrastructureParameters(
    val bikeInfrastructure: BikeInfrastructure,
    val expectedSimplified: SimplifiedBikeInfrastructure,
)
