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
 * An enum with the infrastructure types in the "simplified" format.
 *
 * "Simplified" means that we treat all OSM ways as bicycle infrastructure and annotate other vehicle usage.
 * This is not how OSM actually works, see [BikeInfrastructure] for the correct representation.
 *
 * The simplified format is used for back-mapping (Radsim to OSM) until we generate new RouteAlternatives.
 * - The simplified version sets all OSM tags interpreted by the [BikeInfrastructure] to a value
 *   which does not interact with the mapping code (see `backend.RadSimTagMerger.merge`). We then only set the OSM tags
 *   relevant for the [SimplifiedBikeInfrastructure] to a value which results into the newly selected category.
 *   This ensures our hierarchical mapping does not return early with another category.
 * - We'll then switch to a Matrix-lookup "what OSM tags are needed to switch from category 4 to 6".
 * - We might then handle left/right separately (in OSM and UI) but need to consider for-/backward and the direction
 *   in which the geometry is defined. "Right" can mean "forward" if defined in the opposite direction.
 *   Please check out: https://wiki.openstreetmap.org/wiki/Forward_%26_backward,_left_%26_right
 *
 * The back-mapping will also be relevant for the feature where the user can update the road network state.
 *
 * For mapping from OSM to Radsim, use [BikeInfrastructure].
 *
 * @property value The value for the RadSim infrastructure type tag.
 * @property backMappingTag The OSM tag used to back-map the RadSim infrastructure type to OSM.
 */
enum class SimplifiedBikeInfrastructure(val value: String, val backMappingTag: Set<OsmTag>) {

    /**
     * Infrastructure type where bikes have their own "highway".
     */
    CYCLE_HIGHWAY("CycleHighway", setOf(OsmTag("cycle_highway", "yes"))),

    /**
     * Infrastructure where bikes have a road which is specifically designated for bicycles.
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

    // These 4 categories are sub-categories of [NO], maybe including [NO] itself.
    // They only exist for mapping but are aggregated as [NO] in the client and only [NO] can be selected in the client.
    // FIXME See if these "no" can be in here - or remove if we remove DetailedBI.backMappingType
    SERVICE_MISC("ServiceMisc", NO.backMappingTag),
    MIT_ROAD("MitRoad", NO.backMappingTag),
    PEDESTRIAN("Pedestrian", NO.backMappingTag),
    PATH_NOT_FORBIDDEN("PathNotForbidden", NO.backMappingTag),
    ;

    companion object {
        /**
         * The RadSim tag used to store the simplified infrastructure type (6 categories + "no").
         */
        const val RADSIM_TAG = "roadStyleSimplified"

        /**
         * Find the infrastructure type based on the provided OSM tags.
         *
         * This method checks specific OSM tags hierarchically and returns the first match.
         *
         * @param tags The tags to search for the infrastructure type.
         * @return The infrastructure type based on the provided tags.
         * /
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

            // Check for motorized transport (MIT) road (left or right)
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
        }*/
    }
}
