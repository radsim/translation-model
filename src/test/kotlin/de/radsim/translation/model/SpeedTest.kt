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
import de.radsim.translation.model.Speed.MAX_SPEED_MIV_GT_30_LTE_50
import de.radsim.translation.model.Speed.MAX_SPEED_MIV_GT_50
import de.radsim.translation.model.Speed.MAX_SPEED_MIV_LTE_30
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
                SpeedParameters(mapOf("maxspeed" to "10 mph"), MAX_SPEED_MIV_LTE_30),
                SpeedParameters(mapOf("maxspeed" to "walk"), MAX_SPEED_MIV_LTE_30),

                SpeedParameters(mapOf("maxspeed" to "DE:rural"), MAX_SPEED_MIV_GT_30_LTE_50),
                SpeedParameters(mapOf("maxspeed" to "DE:urban"), MAX_SPEED_MIV_GT_30_LTE_50),

                SpeedParameters(mapOf("maxspeed" to "unknown"), NO_INFORMATION),
                SpeedParameters(mapOf("maxspeed" to "none"), NO_INFORMATION),
                SpeedParameters(mapOf("maxspeed" to "signals"), NO_INFORMATION),

                SpeedParameters(mapOf("maxspeed" to "50(foobar)"), MAX_SPEED_MIV_GT_30_LTE_50),

                SpeedParameters(mapOf("maxspeed" to "30"), MAX_SPEED_MIV_LTE_30),
                SpeedParameters(mapOf("maxspeed" to "50"), MAX_SPEED_MIV_GT_30_LTE_50),
                SpeedParameters(mapOf("maxspeed" to "60"), MAX_SPEED_MIV_GT_50),

                SpeedParameters(mapOf("highway" to "living_street"), MAX_SPEED_MIV_LTE_30),

                SpeedParameters(mapOf("maxspeed" to "unknown"), NO_INFORMATION),
                SpeedParameters(mapOf("maxspeed" to "none"), NO_INFORMATION),
                SpeedParameters(mapOf("maxspeed" to "signals"), NO_INFORMATION),
                SpeedParameters(mapOf(), NO_INFORMATION),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingSpeedParameters> {
            return Stream.of(
                BackMappingSpeedParameters(MAX_SPEED_MIV_LTE_30, OsmTag("maxspeed", "30")),
                BackMappingSpeedParameters(MAX_SPEED_MIV_GT_30_LTE_50, OsmTag("maxspeed", "50")),
                BackMappingSpeedParameters(MAX_SPEED_MIV_GT_50, OsmTag("maxspeed", "100")),
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
