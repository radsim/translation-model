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
 *   The UI and mapping then handles both directions separately, e.g. `bicycle_way_right_lane_left` should be:
 *     a) shown in the UI and Map as `bicycle_way` right, `lane` left
 *     b) the user should be able to change both sides individually
 *     c) the back-mapping merges both directions into one or multiple tags (or maybe even splits the geometry)
 *        But this then needs to take the forward/backend, one-way and formal direction into account.
 *   But we then need to consider for-/backward, the formal/geometry direction and `one-way=yes`.
 *   I.e. "right" can mean "forward" if defined in the opposite direction.
 *   Please check out: https://wiki.openstreetmap.org/wiki/Forward_%26_backward,_left_%26_right
 *
 * The back-mapping will also be relevant for the feature where the user can update the road network state.
 *
 * For mapping from OSM to Radsim, use [BikeInfrastructure].
 *
 * @property value The value for the RadSim infrastructure type tag.
 */
enum class SimplifiedBikeInfrastructure(val value: String) {

    /**
     * Infrastructure type where bikes have their own "highway".
     */
    CYCLE_HIGHWAY("CycleHighway"),

    /**
     * Infrastructure where bikes have a road which is specifically designated for bicycles.
     */
    BICYCLE_ROAD("BicycleRoad"),

    /**
     * Infrastructure type where bicycles have an exclusive track, segregated from other traffic.
     */
    BICYCLE_WAY("BicycleWay"),

    /**
     * Infrastructure type where bicycles have a designated lane on the road.
     */
    BICYCLE_LANE("BicycleLane"),

    /**
     * Infrastructure type where bicycles share a lane with buses.
     */
    BUS_LANE("BusLane"),

    /**
     * Infrastructure type where bicycles share a lane with pedestrians or other traffic.
     */
    MIXED_WAY("MixedWay"),

    /**
     * Infrastructure type where no bike infrastructure is available.
     *
     * Includes the sub-categories `MISC_SERVICE`, `MIT_ROAD`, `PEDESTRIAN` and `PATH_NOT_FORBIDDEN`
     */
    NO("No"),
    ;

    companion object {
        /**
         * The RadSim tag used to store the simplified infrastructure type (6 categories + "no").
         */
        const val RADSIM_TAG = "roadStyleSimplified"
    }
}
