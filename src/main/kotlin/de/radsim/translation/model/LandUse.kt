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
 * Constants and utilities for land use feature mapping.
 *
 * Unlike other RadSim features (NumberOfLanes, BikeInfrastructure), land use is not derived
 * from way tags. It requires spatial intersection with polygon geometries. Ways are annotated
 * with binary land use attributes during RoadNetworkProcessing based on proximity to land use
 * polygons.
 *
 * The three land use categories represent different types of environment:
 * - Water: Rivers, lakes, basins
 * - Green: Forests, parks, grass areas
 * - Natural: Agricultural land, meadows, orchards
 *
 * Each way receives a binary value (0 or 1) for each category based on whether >= 50% of its
 * length falls within buffered polygons of that category.
 *
 * NOTE: This binary value is just an intermediate implementation. This treats a segment in the land-use case
 * similar to other attributes, e.g. "either it is a cycle way or not". TUD will eventually change this to calculate
 * the actual share of the edge which is "inside the land-use buffer area". But the coefficients will stay the same
 * in the SelectionModel. Until we get a note from TUD, this binary decision stays as it is.
 */
object LandUse {
    /**
     * RadSim tag for water proximity (binary: 0 or 1).
     *
     * Set to 1 if >= 50% of the way length is within buffered water polygons.
     */
    const val RADSIM_TAG_WATER = "radsim_lu_is_near_water"

    /**
     * RadSim tag for green space proximity (binary: 0 or 1).
     *
     * Set to 1 if >= 50% of the way length is within buffered green space polygons.
     */
    const val RADSIM_TAG_GREEN = "radsim_lu_is_near_green"

    /**
     * RadSim tag for natural/agricultural proximity (binary: 0 or 1).
     *
     * Set to 1 if >= 50% of the way length is within buffered natural/agricultural polygons.
     */
    const val RADSIM_TAG_NATURAL = "radsim_lu_is_near_natural"

    /**
     * All land use RadSim tags.
     */
    // val ALL_TAGS = listOf(RADSIM_TAG_WATER, RADSIM_TAG_GREEN, RADSIM_TAG_NATURAL)

    /**
     * Threshold percentage for binary classification.
     *
     * A way is classified as "near" a land use type if >= 50% of its length
     * falls within buffered polygons of that type.
     */
    const val THRESHOLD_PERCENTAGE = 50.0

    /**
     * Converts a percentage overlap to binary classification.
     *
     * @param percentageOverlap The percentage of way length within buffered polygon (0-100)
     * @return 1 if >= THRESHOLD_PERCENTAGE (50%), 0 otherwise
     */
    @Suppress("unused") // Part of the API
    fun toBinary(percentageOverlap: Double): Int {
        return if (percentageOverlap >= THRESHOLD_PERCENTAGE) 1 else 0
    }
}
