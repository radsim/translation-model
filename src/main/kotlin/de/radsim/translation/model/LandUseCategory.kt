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
 * These filters match the Python reference implementation (compute_land-use notebook).
 *
 * @property value The value identifier for this land use category
 * @property radsimTag The RadSim tag name used to store the binary attribute on ways
 */
enum class LandUseCategory(
    val value: String,
    val radsimTag: String,
) {
    /**
     * Water features like water bodies, wetlands, basins, rivers, streams.
     *
     * Buffer: 5-100m based on area and type (rivers get 100m).
     */
    WATER(
        "water",
        LandUse.RADSIM_TAG_WATER,
    ),

    /**
     * Green spaces like forests, meadows, parks, gardens, natural vegetation.
     *
     * Buffer: 5-20m based on area.
     */
    GREEN(
        "green",
        LandUse.RADSIM_TAG_GREEN,
    ),

    /**
     * Natural/agricultural areas like farmland, orchards, vineyards, greenhouses.
     *
     * Buffer: 5-30m based on area.
     */
    NATURAL(
        "natural",
        LandUse.RADSIM_TAG_NATURAL,
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
