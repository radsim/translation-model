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
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.bicycleWayLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.bicycleWayRight
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isBikeLaneLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isBikeLaneRight
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isBikeRoad
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isBusLaneLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isBusLaneRight
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isCycleHighway
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isNotAccessible
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isPathNotForbidden
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isPedestrianLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isPedestrianRight
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.isService
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.mitRoadLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.mitRoadRight
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.mixedWayLeft
import de.radsim.translation.model.DetailedBikeInfrastructure.Companion.mixedWayRight

/**
 * An enum with the infrastructure types in the "simplified" format.
 *
 * "Simplified" means that we treat all OSM ways as bicycle infrastructure and annotate other vehicle usage.
 * This is not how OSM actually works, see [DetailedBikeInfrastructure] for the correct representation.
 *
 * The simplified format is used for simple back-mapping (Radsim to OSM).
 * For mapping from OSM to Radsim, use [DetailedBikeInfrastructure].
 *
 * @property value The value for the RadSim infrastructure type tag.
 * @property backMappingTag The OSM tag used to back-map the RadSim infrastructure type to OSM.
 */
enum class SimplifiedBikeInfrastructure(val value: String, val backMappingTag: Set<OsmTag>) {

    // FIXME: Also set all OSM tags interpreted by the mapping to a value which does not
    // lead to an early "out" in the hierarchical mapping function

    /**
     * FIXME
     */
    CYCLE_HIGHWAY("CycleHighway", setOf(OsmTag("cycle_highway", "yes"))),

    /**
     * FIXME
     */
    BICYCLE_ROAD("BicycleRoad", setOf(OsmTag("bicycle_road", "yes"))),

    /**
     * Infrastructure type where bikes have their own track without any other traffic nearby. FIXME
     */
    BICYCLE_WAY("BicycleWay", setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))),

    /**
     * Infrastructure type where bikes have their own lane. FIXME
     */
    BICYCLE_LANE("BicycleLane", setOf(OsmTag("cycleway", "lane"))),

    /**
     * Infrastructure type where bikes use a bus lane. FIXME
     */
    @Suppress("SpellCheckingInspection")
    BUS_LANE("BusLane", setOf(OsmTag("cycleway", "share_busway"))),

    /**
     * Infrastructure type where bikes share their lane with other traffic forms. FIXME
     */
    MIXED_WAY("MixedWay", setOf(OsmTag("highway", "footway"), OsmTag("bicycle", "yes"))),

    /**
     * Infrastructure type where no bike infrastructure is available.
     */
    NO("No", setOf(OsmTag("highway", "service"))),

    // FIXME See if these "no" can be in here
    SERVICE_MISC("ServiceMisc", NO.backMappingTag),
    MIT_ROAD("MitRoad", NO.backMappingTag),
    PEDESTRIAN("Pedestrian", NO.backMappingTag),
    PATH_NOT_FORBIDDEN("PathNotForbidden", NO.backMappingTag),
    ;

    companion object {
        /**
         * The RadSim tag used to store the infrastructure type.
         *
         * FIXME: See if we need this in SimplifiedBikeInfrastructure or DetailedBikeInfrastructure
         */
        const val RADSIM_TAG = "roadStyle"

        /**
         * The specific OSM tags that are used to determine the RadSim infrastructure type.
         *
         * "cycleway" is the default key used by BRouter (probably, check again if unsure)
         * "cycleway[:l/r/b]": used e.g. by the profile "fastbike-verylowtraffic"
         *
         * FIXME: See if we need this in SimplifiedBikeInfrastructure or DetailedBikeInfrastructure
         * FIXME: Update list: Probably all tags, so we can use them also to set them to a null-value see above
         */
        @Suppress("SpellCheckingInspection", "unused") // Part of the API
        val specificOsmTags = listOf(
            "highway",
            "bicycle",
            "cycleway",
            "cycleway:both",
            "cycleway:right",
            "cycleway:left",
        )

        /**
         * Find the infrastructure type based on the provided OSM tags.
         *
         * This method checks specific OSM tags hierarchically and returns the first match.
         *
         * @param tags The tags to search for the infrastructure type.
         * @return The infrastructure type based on the provided tags.
         */
        // We keep the method structure to be easier comparable with the mapping from TUD:
        // https://github.com/1prk/osm_categorizer/blob/radsim/netapy/assessor_free.py
        @Suppress("CyclomaticComplexMethod", "LongMethod", "ComplexMethod", "ReturnCount")
        fun toRadSim(tags: Map<String, String>): SimplifiedBikeInfrastructure {
            // Check if access is restricted or if there is a tram
            if ((tags.containsValue("access") && isNotAccessible(tags)) || (tags["tram"] == "yes")) {
                return NO // Unpacked from "service"
            }

            // Check if it's a service road
            if (isService(tags)) {
                return SERVICE_MISC
            }

            // Check if it's a cycle highway
            if (isCycleHighway(tags)) {
                return CYCLE_HIGHWAY
            }

            // Check if it's a bicycle road
            if (isBikeRoad(tags)) {
                return BICYCLE_ROAD
            }

            // Check for bicycle way (left or right)
            if (bicycleWayLeft(tags) || bicycleWayRight(tags)) {
                return BICYCLE_WAY
            }

            // Check for bicycle lane (left or right)
            if (isBikeLaneLeft(tags) || isBikeLaneRight(tags)) {
                return BICYCLE_LANE
            }

            // Check for bus lane (left or right)
            if (isBusLaneLeft(tags) || isBusLaneRight(tags)) {
                return BUS_LANE
            }

            // Check for mixed way (left or right)
            if (mixedWayLeft(tags) || mixedWayRight(tags)) {
                return MIXED_WAY
            }

            // Check for mit road (left or right)
            if (mitRoadLeft(tags) || mitRoadRight(tags)) {
                return MIT_ROAD
            }

            // Check for pedestrian path (left or right) and not indoor
            if ((isPedestrianLeft(tags) || isPedestrianRight(tags)) && (tags["indoor"] != "yes")) {
                return if (tags["access"] == "customers") {
                    NO
                } else {
                    PEDESTRIAN
                }
            }

            // Check if path is not forbidden
            if (isPathNotForbidden(tags)) {
                return PATH_NOT_FORBIDDEN
            }

            // Fallback to "no"
            return NO
        }
    }
}
