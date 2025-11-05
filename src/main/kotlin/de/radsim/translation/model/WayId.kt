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

@Suppress("unused") // Part of the API
object WayId {
    /**
     * The RadSim tag used to store the way id.
     */
    const val RADSIM_TAG = "@id"

    /**
     * The OSM tag used to back-map the RadSim way id to OSM.
     */
    @Suppress("MemberVisibilityCanBePrivate") // Part of the API
    const val BACK_MAPPING_OSM_TAG = "@id"

    /**
     * The specific OSM tags that are used to determine the RadSim way id.
     */
    val specificOsmTagsRegex = listOf(
        BACK_MAPPING_OSM_TAG,
    )
}
