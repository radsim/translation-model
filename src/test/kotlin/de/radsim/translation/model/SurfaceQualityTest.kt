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

class SurfaceQualityTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: SurfaceQualityParameters) {
        // Arrange
        val result = SurfaceQuality.toRadSim(parameter.osmTags)

        // Assert
        assert(result == parameter.expectedRadSimTag)
    }

    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingSurfaceQualityParameters) {
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
        fun testParameters(): Stream<SurfaceQualityParameters> {
            return Stream.of(
                SurfaceQualityParameters(mapOf("smoothness" to "excellent"), SurfaceQuality.GOOD),
                SurfaceQualityParameters(mapOf("smoothness" to "good"), SurfaceQuality.GOOD),

                SurfaceQualityParameters(mapOf("smoothness" to "intermediate"), SurfaceQuality.MEDIUM),

                SurfaceQualityParameters(mapOf("smoothness" to "bad"), SurfaceQuality.BAD),
                SurfaceQualityParameters(mapOf("smoothness" to "very_bad"), SurfaceQuality.BAD),
                SurfaceQualityParameters(mapOf("smoothness" to "horrible"), SurfaceQuality.BAD),
                SurfaceQualityParameters(mapOf("smoothness" to "very_horrible"), SurfaceQuality.BAD),
                SurfaceQualityParameters(mapOf("smoothness" to "impassable"), SurfaceQuality.BAD),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingSurfaceQualityParameters> {
            return Stream.of(
                BackMappingSurfaceQualityParameters(SurfaceQuality.GOOD, OsmTag("smoothness", "good")),
                BackMappingSurfaceQualityParameters(SurfaceQuality.MEDIUM, OsmTag("smoothness", "intermediate")),
                BackMappingSurfaceQualityParameters(SurfaceQuality.BAD, OsmTag("smoothness", "very_bad")),
                BackMappingSurfaceQualityParameters(SurfaceQuality.NO_INFORMATION, null),
            )
        }
    }
}

class SurfaceQualityParameters(
    val osmTags: Map<String, Any>,
    val expectedRadSimTag: SurfaceQuality,
)

class BackMappingSurfaceQualityParameters(
    val radSimTag: SurfaceQuality,
    val osmTag: OsmTag?,
)
