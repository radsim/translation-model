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
import de.radsim.translation.model.BikeInfrastructure.LANE
import de.radsim.translation.model.BikeInfrastructure.MIXED
import de.radsim.translation.model.BikeInfrastructure.TRACK
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BikeInfrastructureTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: BikeInfrastructureParameters) {
        // Arrange
        val result = BikeInfrastructure.toRadSim(parameter.osmTags, MIXED)

        // Assert
        assert(result == parameter.expectedRadSimTag)
    }

    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingInfrastructureParameters) {
        // Arrange
        // Act
        val result = parameter.radSimTag.backMappingTag

        // Assert
        assert(result == parameter.osmTag)
    }

    companion object {
        /**
         * @return All test setup to run
         */
        @JvmStatic
        fun testParameters(): Stream<BikeInfrastructureParameters> {
            return Stream.of(
                BikeInfrastructureParameters(mapOf("highway" to "cycleway"), TRACK),
                BikeInfrastructureParameters(mapOf("cycleway" to "track"), TRACK),
                BikeInfrastructureParameters(mapOf("cycleway:right" to "track"), TRACK),
                BikeInfrastructureParameters(mapOf("cycleway:left" to "track"), TRACK),
                BikeInfrastructureParameters(mapOf("cycleway" to "opposite_track"), TRACK),
                BikeInfrastructureParameters(mapOf("bicycle" to "path"), TRACK),
                BikeInfrastructureParameters(mapOf("bicycle" to "track"), TRACK),

                BikeInfrastructureParameters(mapOf("cycleway" to "lane"), LANE),
                BikeInfrastructureParameters(mapOf("bicycle" to "lane"), LANE),
                BikeInfrastructureParameters(mapOf("cycleway:right" to "lane"), LANE),
                BikeInfrastructureParameters(mapOf("cycleway:left" to "lane"), LANE),
                BikeInfrastructureParameters(mapOf("cycleway:both" to "lane"), LANE),
                BikeInfrastructureParameters(mapOf("cycleway" to "opposite_lane"), LANE),

                // Other or no information
                BikeInfrastructureParameters(mapOf("highway" to "somethingElse"), MIXED),
                BikeInfrastructureParameters(mapOf(), MIXED),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingInfrastructureParameters> {
            return Stream.of(
                BackMappingInfrastructureParameters(TRACK, OsmTag("cycleway", "track")),
                BackMappingInfrastructureParameters(LANE, OsmTag("cycleway", "lane")),
                BackMappingInfrastructureParameters(MIXED, OsmTag("bicycle", "yes")),
                BackMappingInfrastructureParameters(BikeInfrastructure.NO_INFORMATION, null),
            )
        }
    }
}

class BikeInfrastructureParameters(
    val osmTags: Map<String, Any>,
    val expectedRadSimTag: BikeInfrastructure,
)

class BackMappingInfrastructureParameters(
    val radSimTag: BikeInfrastructure,
    val osmTag: OsmTag?,
)
