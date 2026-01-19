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
 * An enumeration providing the different number of lanes categories considered by RadSim.
 *
 * TODO: These categories are preliminary guesses. Update when TUD provides the final
 *       coefficients and category definitions for numberOfLanes.
 *
 * The categories are:
 * - 0 lanes: footways, cycleways, paths without motor vehicle lanes
 * - 1 lane: single lane roads (residential, service, etc.)
 * - 2 lanes: standard two-lane roads (TODO: reference category for selection model until we hear something from TUD)
 * - 3+ lanes: multi-lane roads
 *
 * @property value The value for the RadSim number of lanes category tag
 */
enum class NumberOfLanesCategory(val value: String) {
    /**
     * Roads with no motor vehicle lanes (footways, cycleways, pedestrian paths).
     */
    LANES_0("0"),

    /**
     * Single lane roads (residential, service roads, tracks).
     */
    LANES_1("1"),

    /**
     * Standard two-lane roads (tertiary, secondary, primary).
     * TODO: This is the reference category for the selection model until we hear something from TUD.
     */
    LANES_2("2"),

    /**
     * Multi-lane roads with 3 or more lanes.
     */
    LANES_3_OR_MORE("3ormore");

    companion object {
        /**
         * Classifies a numeric lane count into a NumberOfLanesCategory.
         *
         * @param numberOfLanes The number of lanes as returned by [NumberOfLanes.toRadSim]
         * @return The corresponding NumberOfLanesCategory
         */
        fun fromLaneCount(numberOfLanes: Int): NumberOfLanesCategory {
            return when {
                numberOfLanes <= 0 -> LANES_0
                numberOfLanes == 1 -> LANES_1
                numberOfLanes == 2 -> LANES_2
                else -> LANES_3_OR_MORE
            }
        }

        /**
         * Parses a RadSim tag value and returns the corresponding NumberOfLanesCategory.
         *
         * @param value The RadSim tag value
         * @return The corresponding NumberOfLanesCategory
         */
        fun fromValue(value: String): NumberOfLanesCategory =
            entries.firstOrNull { it.value == value }
                ?: error("Unexpected RadSim number of lanes category tag: $value")
    }
}
