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

/**
 * An enumeration with all the different crossing types considered by RadSim.
 *
 * @property osmTags The OSM tags to check for to determine the crossing type.
 */
@Suppress("unused") // Part of the API
enum class Crossing(
    @Suppress("MemberVisibilityCanBePrivate") // Part of the API
    val osmTags: List<OsmTag>
) {
    TRAFFIC_SIGNAL(listOf(OsmTag("crossing", "traffic_signals"))),
    MARKED_PEDESTRIAN_CROSSING(listOf(OsmTag("crossing", "marked"), OsmTag("crossing", "uncontrolled"))),
    UNMARKED_PEDESTRIAN_CROSSING(listOf(OsmTag("crossing", "unmarked"))),
    ;

    companion object {
        /**
         * The specific OSM tags that are used to determine the RadSim crossing type.
         */
        val specificOsmTagsRegex = listOf(
            "crossing",
        )
    }
}
