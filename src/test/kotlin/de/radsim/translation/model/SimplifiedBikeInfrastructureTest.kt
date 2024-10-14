/*
 * Copyright (C) 2024 Cyface GmbH
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

class SimplifiedBikeInfrastructureTest {
    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingInfrastructureParameters) {
        // Arrange
        // Act
        val result = parameter.radSimTag.backMappingTag

        // Assert
        assert(result == parameter.osmTags)
    }

    companion object {
        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingInfrastructureParameters> {
            return Stream.of(
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.CYCLE_HIGHWAY,
                    setOf(OsmTag("cycle_highway", "yes"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.BICYCLE_ROAD,
                    setOf(OsmTag("bicycle_road", "yes"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.BICYCLE_WAY,
                    setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.BICYCLE_LANE,
                    setOf(OsmTag("cycleway", "lane"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.BUS_LANE,
                    setOf(OsmTag("cycleway", "share_busway"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.MIXED_WAY,
                    setOf(OsmTag("highway", "footway"), OsmTag("bicycle", "yes"))
                ),
                BackMappingInfrastructureParameters(
                    SimplifiedBikeInfrastructure.NO,
                    setOf(OsmTag("highway", "service"))
                )
            )
        }
    }
}

data class BackMappingInfrastructureParameters(
    val radSimTag: SimplifiedBikeInfrastructure,
    val osmTags: Set<OsmTag>
)
