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

class SurfaceTypeTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    fun testToRadSim(parameter: SurfaceTypeParameters) {
        // Arrange
        val result = SurfaceType.toRadSim(parameter.osmTags)

        // Assert
        assert(result == parameter.expectedRadSimTag)
    }

    @ParameterizedTest
    @MethodSource("backMappingParameters")
    fun testBackMapping(parameter: BackMappingSurfaceTypeParameters) {
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
        @SuppressWarnings("LongMethod") // TODO
        fun testParameters(): Stream<SurfaceTypeParameters> {
            @Suppress("SpellCheckingInspection")
            return Stream.of(
                // All values, just for top prio tag name "surface"
                SurfaceTypeParameters(mapOf("surface" to "asphalt"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "asphalt:paving_stones"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "bricks"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "concrete"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "concrete:lanes"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "concrete:plates"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "granite:plates"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "paved"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "paving_stones"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "paving_stones:50"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "paving_stones:lanes"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "plates"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "tartan"), SurfaceType.COMFORT_1_ASPHALT),

                SurfaceTypeParameters(mapOf("surface" to "compacted"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("surface" to "unpaved"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("surface" to "fine_gravel"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("surface" to "grass_paver"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("surface" to "dirt"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("surface" to "dirt:sand"), SurfaceType.COMFORT_2_COMPACTED),

                SurfaceTypeParameters(mapOf("surface" to "asphalt:cobblestone"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "cobblestone"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "cobblestone:flattened"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "metal"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "metal_grid"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "sett"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "tiles"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "unhewn_cobblestone"), SurfaceType.COMFORT_3_COBBLESTONE),

                SurfaceTypeParameters(mapOf("surface" to "bare_rock"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "bushes"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "earth"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "grass"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "grass:ground"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "gravel:grass"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "gravel"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "ground"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "ground:grass"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "ground:mud"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "ground:wood"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "mud"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "pebblestone"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "rock"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "roots"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "sand"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "sandstone"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "stepping_stones"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "stone"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "wood"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("surface" to "woodchips"), SurfaceType.COMFORT_4_GRAVEL),

                // Check that the lower priority tags are also considered, don't use the fall-back default comfort_1!
                SurfaceTypeParameters(mapOf("cycleway:surface" to "earth"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("cycleway:both:surface" to "earth"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("cycleway:left:surface" to "earth"), SurfaceType.COMFORT_4_GRAVEL),
                SurfaceTypeParameters(mapOf("cycleway:right:surface" to "earth"), SurfaceType.COMFORT_4_GRAVEL),

                // Ensure tags are interpreted hierarchically, ordered in the opposite way
                SurfaceTypeParameters(
                    mapOf(
                        "cycleway:surface" to "earth", // comfort_4
                        "surface" to "asphalt", // comfort_1 - should win
                    ),
                    SurfaceType.COMFORT_1_ASPHALT
                ),
                SurfaceTypeParameters(
                    mapOf(
                        "cycleway:both.surface" to "unpaved", // comfort_2
                        "cycleway:surface" to "unpaved", // comfort_2
                        "surface" to "sandstone", // comfort_4 - should win
                    ),
                    SurfaceType.COMFORT_4_GRAVEL
                ),

                // Ensure we don't accidentally check *all* tags for the *first* value, then the next tag etc.
                SurfaceTypeParameters(
                    mapOf(
                        "cycleway:surface" to "asphalt", // comfort_1 - first value but lower priority tag
                        "surface" to "earth", // comfort_4 - later value but higher priority tag
                    ),
                    SurfaceType.COMFORT_4_GRAVEL
                ),

                // Ensure typos in the tag values are considered, don't use the fall-back default comfort_1!
                SurfaceTypeParameters(mapOf("surface" to "cobblestone;flattened"), SurfaceType.COMFORT_3_COBBLESTONE),
                SurfaceTypeParameters(mapOf("surface" to "cobblestone/flattened"), SurfaceType.COMFORT_3_COBBLESTONE),

                // Ensure alternative tags are considered, don't use the fall-back default comfort_1!
                // option 1: all surface tags are not set
                SurfaceTypeParameters(mapOf("highway" to "path"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("highway" to "track"), SurfaceType.COMFORT_2_COMPACTED),
                SurfaceTypeParameters(mapOf("highway" to "somethingElse"), SurfaceType.COMFORT_1_ASPHALT),
                // option 2: surface tags are set to something unknown
                SurfaceTypeParameters(mapOf("surface" to "somethingElse"), SurfaceType.COMFORT_1_ASPHALT),
                SurfaceTypeParameters(mapOf("surface" to "sthE", "highway" to "path"), SurfaceType.COMFORT_2_COMPACTED),
            )
        }

        @JvmStatic
        fun backMappingParameters(): Stream<BackMappingSurfaceTypeParameters> {
            return Stream.of(
                BackMappingSurfaceTypeParameters(SurfaceType.COMFORT_1_ASPHALT, OsmTag("surface", "asphalt")),
                BackMappingSurfaceTypeParameters(SurfaceType.COMFORT_2_COMPACTED, OsmTag("surface", "compacted")),
                BackMappingSurfaceTypeParameters(SurfaceType.COMFORT_3_COBBLESTONE, OsmTag("surface", "cobblestone")),
                BackMappingSurfaceTypeParameters(SurfaceType.COMFORT_4_GRAVEL, OsmTag("surface", "gravel")),
            )
        }
    }
}

class SurfaceTypeParameters(
    val osmTags: Map<String, Any>,
    val expectedRadSimTag: SurfaceType,
)

class BackMappingSurfaceTypeParameters(
    val radSimTag: SurfaceType,
    val osmTag: OsmTag,
)
