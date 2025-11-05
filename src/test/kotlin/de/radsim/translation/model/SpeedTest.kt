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
import de.radsim.translation.model.Speed.MAX_SPEED_10_OR_LESS
import de.radsim.translation.model.Speed.MAX_SPEED_10_TO_30
import de.radsim.translation.model.Speed.MAX_SPEED_30_TO_50
import de.radsim.translation.model.Speed.MAX_SPEED_50_TO_70
import de.radsim.translation.model.Speed.MAX_SPEED_70_OR_MORE
import de.radsim.translation.model.Speed.NO_INFORMATION
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SpeedTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: SpeedParameters) {
        // Arrange
        val result = Speed.toRadSim(parameter.osmTags)

        // Assert
        assert(result == parameter.expectedRadSimTag)
    }

    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingSpeedParameters) {
        // Arrange
        // Act
        val result = parameter.radSimTag.backMappingTag

        // Assert
        assert(result == parameter.osmTag)
    }

    @Suppress("SpellCheckingInspection")
    companion object {
        /**
         * @return All test setup to run
         */
        @JvmStatic
        fun testParameters(): Stream<SpeedParameters> {
            return Stream.of(
                // Numeric speed values
                SpeedParameters(mapOf("maxspeed" to "10"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("maxspeed" to "30"), MAX_SPEED_10_TO_30),
                SpeedParameters(mapOf("maxspeed" to "50"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("maxspeed" to "70"), MAX_SPEED_50_TO_70),
                SpeedParameters(mapOf("maxspeed" to "100"), MAX_SPEED_70_OR_MORE),

                // Special string values
                SpeedParameters(mapOf("maxspeed" to "walk"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("maxspeed" to "Schrittgeschwindigkeit"), MAX_SPEED_10_OR_LESS), // 5 km/h
                SpeedParameters(mapOf("maxspeed" to "DE:rural"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("maxspeed" to "de:rural"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("maxspeed" to "DE:urban"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("maxspeed" to "signals"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("maxspeed" to "10;30"), MAX_SPEED_10_TO_30),

                // zone:maxspeed handling
                SpeedParameters(mapOf("zone:maxspeed" to "20"), MAX_SPEED_10_TO_30),
                SpeedParameters(mapOf("zone:maxspeed" to "30"), MAX_SPEED_10_TO_30),
                SpeedParameters(mapOf("zone:maxspeed" to "DE:20"), MAX_SPEED_10_TO_30),
                SpeedParameters(mapOf("zone:maxspeed" to "DE:urban"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("zone:maxspeed" to "DE:motorway"), MAX_SPEED_70_OR_MORE),

                // maxspeed:forward/backward (takes maximum)
                SpeedParameters(mapOf("maxspeed:forward" to "50", "maxspeed:backward" to "30"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("maxspeed:forward" to "30", "maxspeed:backward" to "50"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("maxspeed:forward" to "60"), MAX_SPEED_50_TO_70),
                SpeedParameters(mapOf("maxspeed:backward" to "70"), MAX_SPEED_50_TO_70),

                // Highway-based fallback
                SpeedParameters(mapOf("highway" to "motorway"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "motorway_link"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "primary"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "primary_link"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "secondary"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "secondary_link"), MAX_SPEED_70_OR_MORE),
                SpeedParameters(mapOf("highway" to "living_street"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("highway" to "footway"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("highway" to "pedestrian"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("highway" to "steps"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("highway" to "residential"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("highway" to "tertiary"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("highway" to "tertiary_link"), MAX_SPEED_30_TO_50),
                SpeedParameters(mapOf("highway" to "path"), MAX_SPEED_10_TO_30),
                SpeedParameters(mapOf("highway" to "track"), MAX_SPEED_10_TO_30),

                // No information cases
                SpeedParameters(mapOf("maxspeed" to "none"), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf(), MAX_SPEED_10_OR_LESS),
                SpeedParameters(mapOf("highway" to "unclassified"), MAX_SPEED_10_OR_LESS),

                // Hierarchical priority tests: maxspeed takes priority over everything
                SpeedParameters(
                    mapOf("maxspeed" to "30", "highway" to "motorway", "zone:maxspeed" to "DE:urban"),
                    MAX_SPEED_10_TO_30
                ),
                SpeedParameters(
                    mapOf("maxspeed" to "70", "highway" to "living_street"),
                    MAX_SPEED_50_TO_70
                ),
                SpeedParameters(
                    mapOf("maxspeed" to "100", "zone:maxspeed" to "DE:20"),
                    MAX_SPEED_70_OR_MORE
                ),

                // Hierarchical priority: maxspeed:forward/backward takes priority over zone:maxspeed
                SpeedParameters(
                    mapOf("maxspeed:forward" to "40", "zone:maxspeed" to "DE:20", "highway" to "motorway"),
                    MAX_SPEED_30_TO_50
                ),

                // Hierarchical priority: maxspeed:forward/backward takes priority over highway
                SpeedParameters(
                    mapOf("maxspeed:forward" to "40", "highway" to "motorway"),
                    MAX_SPEED_30_TO_50
                ),

                // Hierarchical priority: zone:maxspeed takes priority over highway
                SpeedParameters(
                    mapOf("zone:maxspeed" to "DE:20", "highway" to "motorway"),
                    MAX_SPEED_10_TO_30
                ),
                SpeedParameters(
                    mapOf("zone:maxspeed" to "DE:urban", "highway" to "living_street"),
                    MAX_SPEED_30_TO_50
                ),

                // Edge case: maxspeed=none should fall back to maxspeed:forward
                SpeedParameters(
                    mapOf("maxspeed" to "none", "maxspeed:forward" to "60", "highway" to "living_street"),
                    MAX_SPEED_50_TO_70
                ),

                // Edge case: maxspeed=none and no forward/backward should fall back to zone:maxspeed
                SpeedParameters(
                    mapOf("maxspeed" to "none", "zone:maxspeed" to "DE:20", "highway" to "motorway"),
                    MAX_SPEED_10_TO_30
                ),

                // Edge case: maxspeed=none with only highway should use highway
                SpeedParameters(
                    mapOf("maxspeed" to "none", "highway" to "motorway"),
                    MAX_SPEED_70_OR_MORE
                ),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingSpeedParameters> {
            return Stream.of(
                BackMappingSpeedParameters(MAX_SPEED_10_OR_LESS, OsmTag("maxspeed", "10")),
                BackMappingSpeedParameters(MAX_SPEED_10_TO_30, OsmTag("maxspeed", "30")),
                BackMappingSpeedParameters(MAX_SPEED_30_TO_50, OsmTag("maxspeed", "50")),
                BackMappingSpeedParameters(MAX_SPEED_50_TO_70, OsmTag("maxspeed", "70")),
                BackMappingSpeedParameters(MAX_SPEED_70_OR_MORE, OsmTag("maxspeed", "100")),
                BackMappingSpeedParameters(NO_INFORMATION, null),
            )
        }
    }
}

class SpeedParameters(
    val osmTags: Map<String, Any>,
    val expectedRadSimTag: Speed,
)

class BackMappingSpeedParameters(
    val radSimTag: Speed,
    val osmTag: OsmTag?,
)
