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
 * The categories are:
 * - 1- lanes: single lane roads or oaths ways without motor vehicle lanes
 * - 2+ lanes: multi-lane roads
 *
 * @property value The value for the RadSim number of lanes category tag
 */
enum class NumberOfLanesCategory(val value: String) {
    /**
     * Roads with no motor vehicle lanes (footways, cycleways, pedestrian paths) or single lane roads (residential,
     * service roads, tracks).
     */
    @Suppress("SpellCheckingInspection")
    LANES_1_OR_LESS("1orless"),

    /**
     * Multi-lane roads with 2 or more lanes.
     */
    @Suppress("SpellCheckingInspection")
    LANES_2_OR_MORE("2ormore");

    companion object {
        /**
         * Classifies a numeric lane count into a NumberOfLanesCategory.
         *
         * @param numberOfLanes The number of lanes as returned by [NumberOfLanes.toRadSim]
         * @return The corresponding NumberOfLanesCategory
         */
        @Suppress("unused") // Part of the API
        fun fromLaneCount(numberOfLanes: Int): NumberOfLanesCategory {
            return when {
                numberOfLanes <= 1 -> LANES_1_OR_LESS
                else -> LANES_2_OR_MORE
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
