/*
 * Copyright (C) 2026 Cyface GmbH
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

/**
 * An enumeration providing the different land use categories considered by RadSim.
 *
 * Land use polygons and lines are extracted from OSM data and buffered based on their category and area.
 * Ways are then annotated with binary attributes indicating whether they are "near" (>= 50%
 * of length within buffer) each land use type.
 *
 * These filters match the Python reference implementation (compute_landuse notebook).
 *
 * @property value The value identifier for this land use category
 * @property radsimTag The RadSim tag name used to store the binary attribute on ways
 * @property osmFilters The OSM tag combinations used to identify features of this category.
 *           Note: Actual osmium filter expressions (with a/, w/ prefixes) are in NetworkExtractor.
 */
enum class LandUseCategory(
    val value: String,
    val radsimTag: String,
    val osmFilters: List<String>
) {
    /**
     * Water features: water bodies, wetlands, basins, rivers, streams.
     *
     * Areas: natural=water/wetland/bay/strait, landuse=reservoir/basin/aquaculture,
     *        waterway=riverbank/canal/ditch/drain/stream
     * Lines: waterway=river/stream/canal/riverbank
     *
     * Buffer: 5-100m based on area and type (rivers get 100m).
     */
    WATER(
        "water",
        LandUse.RADSIM_TAG_WATER,
        listOf(
            // Areas (natural)
            "natural=water",
            "natural=wetland",
            "natural=bay",
            "natural=strait",
            // Areas (landuse)
            "landuse=reservoir",
            "landuse=basin",
            "landuse=aquaculture",
            // Areas (waterway)
            "waterway=riverbank",
            "waterway=canal",
            "waterway=ditch",
            "waterway=drain",
            "waterway=stream",
            // Lines (waterway)
            "waterway=river"
        )
    ),

    /**
     * Green spaces: forests, meadows, parks, gardens, natural vegetation.
     *
     * Areas: landuse=forest/meadow/grass/greenfield/village_green,
     *        natural=wood/grassland/heath/scrub,
     *        leisure=park/garden/golf_course/pitch/playground,
     *        amenity=grave_yard, boundary=national_park/protected_area
     * Lines: natural=hedge/tree_row
     *
     * Buffer: 5-20m based on area.
     */
    GREEN(
        "green",
        LandUse.RADSIM_TAG_GREEN,
        listOf(
            // Areas (landuse)
            "landuse=forest",
            "landuse=meadow",
            "landuse=grass",
            "landuse=greenfield",
            "landuse=village_green",
            // Areas (natural)
            "natural=wood",
            "natural=grassland",
            "natural=heath",
            "natural=scrub",
            // Areas (leisure)
            "leisure=park",
            "leisure=garden",
            "leisure=golf_course",
            "leisure=pitch",
            "leisure=playground",
            // Areas (other)
            "amenity=grave_yard",
            "boundary=national_park",
            "boundary=protected_area",
            // Lines (natural)
            "natural=hedge",
            "natural=tree_row"
        )
    ),

    /**
     * Natural/agricultural areas: farmland, orchards, vineyards, greenhouses.
     *
     * Areas: landuse=farmland/farmyard/orchard/vineyard/plant_nursery/greenhouse_horticulture,
     *        crop=* (any value), meadow=agricultural/pasture/hay,
     *        natural=grassland, building=farm/greenhouse
     * Lines: landuse=farmland/farmyard/orchard/vineyard/plant_nursery/greenhouse_horticulture
     *
     * Buffer: 5-30m based on area.
     */
    NATURAL(
        "natural",
        LandUse.RADSIM_TAG_NATURAL,
        listOf(
            // Areas (landuse)
            "landuse=farmland",
            "landuse=farmyard",
            "landuse=orchard",
            "landuse=vineyard",
            "landuse=plant_nursery",
            "landuse=greenhouse_horticulture",
            // Areas (crop - any value)
            "crop=*",
            // Areas (meadow key with specific values)
            "meadow=agricultural",
            "meadow=pasture",
            "meadow=hay",
            // Areas (natural)
            "natural=grassland",
            // Areas (building)
            "building=farm",
            "building=greenhouse"
        )
    );

    companion object {
        /**
         * Returns the land use category for a given value string.
         *
         * @param value The category value string
         * @return The corresponding LandUseCategory
         * @throws IllegalStateException if the value is not recognized
         */
        fun fromValue(value: String): LandUseCategory =
            entries.firstOrNull { it.value == value }
                ?: error("Unexpected RadSim land use category: $value")

        /**
         * Returns the land use category for a given RadSim tag name.
         *
         * @param tag The RadSim tag name
         * @return The corresponding LandUseCategory
         * @throws IllegalStateException if the tag is not recognized
         */
        fun fromRadsimTag(tag: String): LandUseCategory =
            entries.firstOrNull { it.radsimTag == tag }
                ?: error("Unexpected RadSim land use tag: $tag")

        // Buffer calculations match the Python reference implementation (compute_landuse notebook)

        /**
         * Calculates buffer size for water features based on area and type.
         *
         * Rivers get a larger buffer (100m) as they are linear features.
         * Lakes and other water bodies are buffered based on their area.
         *
         * @param areaSquareMeters The polygon area in square meters
         * @param isRiver Whether this is a river (linear waterway)
         * @return Buffer distance in meters
         */
        @Suppress("MagicNumber")
        fun waterBufferMeters(areaSquareMeters: Double, isRiver: Boolean): Double {
            if (isRiver) return 100.0
            return when {
                areaSquareMeters < 100 -> 5.0 // Very small ponds
                areaSquareMeters < 1000 -> 20.0 // Small lakes
                areaSquareMeters < 10000 -> 50.0 // Medium lakes
                else -> 100.0 // Large lakes
            }
        }

        /**
         * Calculates buffer size for green features based on area.
         *
         * @param areaSquareMeters The polygon area in square meters
         * @return Buffer distance in meters
         */
        @Suppress("MagicNumber")
        fun greenBufferMeters(areaSquareMeters: Double): Double {
            return when {
                areaSquareMeters < 50 -> 5.0
                areaSquareMeters < 500 -> 10.0
                else -> 20.0
            }
        }

        /**
         * Calculates buffer size for natural/agriculture features based on area.
         *
         * @param areaSquareMeters The polygon area in square meters
         * @return Buffer distance in meters
         */
        @Suppress("MagicNumber")
        fun naturalBufferMeters(areaSquareMeters: Double): Double {
            return when {
                areaSquareMeters < 200 -> 5.0
                areaSquareMeters < 2000 -> 15.0
                else -> 30.0
            }
        }

        /**
         * Calculates the appropriate buffer for a polygon based on its category and area.
         *
         * @param category The land use category
         * @param areaSquareMeters The polygon area in square meters
         * @param isRiver Whether this is a river (only applicable for WATER category)
         * @return Buffer distance in meters
         */
        @Suppress("unused") // Part of the API
        fun bufferMeters(category: LandUseCategory, areaSquareMeters: Double, isRiver: Boolean = false): Double {
            return when (category) {
                WATER -> waterBufferMeters(areaSquareMeters, isRiver)
                GREEN -> greenBufferMeters(areaSquareMeters)
                NATURAL -> naturalBufferMeters(areaSquareMeters)
            }
        }
    }
}
