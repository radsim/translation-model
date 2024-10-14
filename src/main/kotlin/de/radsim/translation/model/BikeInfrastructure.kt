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
 * An enum with the infrastructure types in the "detailed" format.
 *
 * "Detailed" represents the way OpenStreetMap (OSM) annotated bicycle infrastructure:
 * - Bicycle infrastructure and usage is tagged to the road's OSM ways and geometry.
 * - e.g. a road with a bicycle lane on the right
 *
 * The [BikeInfrastructure] includes can be used to map from OSM to Radsim tags.
 * For back-mapping (Radsim to OSM) we currently use [SimplifiedBikeInfrastructure].
 *
 * Notes:
 * - The mapping assumes cyclists only use the infrastructure as allowed (German "STVO").
 * - The OSM mapping community uses "left/right" also in cases where "forward/backward" would be correct.
 *   Because of this, it's intentional, that both `bicycle_way_right_no_left` and `bicycle_way_left_no_right`
 *   are simplified to `bicycle_way` as the geometry might be fined opposite to the travel direction.
 *   See [SimplifiedBikeInfrastructure] description for more details.
 *   This codes does not yet check for this `forward/backward`, `one_way=true` and geometry direction issue.
 *
 * @property value The value for the RadSim infrastructure type tag.
 * @property simplified The simplified infrastructure type which should be shown to the client for this detailed type.
 *                      - `*_mit|_no`: Return first part, e.g. `bicycle_way_right_no_left` => `bicycle_way`
 *                      - `*_left_*_right|*_right_*_left`: Return the hierarchically higher type for now (workaround):
 *                        ... > bicycle_way > bicycle_lane > bus_lane > mixed_way > ...
 */
enum class BikeInfrastructure(
    val value: String,
    @Suppress("MemberVisibilityCanBePrivate") val simplified: SimplifiedBikeInfrastructure // Part of the API
) {

    // Cycle highway
    CYCLE_HIGHWAY("CycleHighway", SimplifiedBikeInfrastructure.CYCLE_HIGHWAY),

    // Bicycle road
    BICYCLE_ROAD("BicycleRoad", SimplifiedBikeInfrastructure.BICYCLE_ROAD),

    // Bicycle way
    BICYCLE_WAY_BOTH("BicycleWayBoth", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_LANE_LEFT("BicycleWayRightLaneLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_BUS_LEFT("BicycleWayRightBusLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_MIXED_LEFT("BicycleWayRightMixedLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_MIT_LEFT("BicycleWayRightMitLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_PEDESTRIAN_LEFT("BicycleWayRightPedestrianLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_NO_LEFT("BicycleWayRightNoLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_LANE_RIGHT("BicycleWayLeftLaneRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_BUS_RIGHT("BicycleWayLeftBusRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_MIXED_RIGHT("BicycleWayLeftMixedRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_MIT_RIGHT("BicycleWayLeftMitRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_PEDESTRIAN_RIGHT("BicycleWayLeftPedestrianRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_LEFT_NO_RIGHT("BicycleWayLeftNoRight", SimplifiedBikeInfrastructure.BICYCLE_WAY),

    // Bicycle lane
    BICYCLE_LANE_BOTH("BicycleLaneBoth", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_BUS_LEFT("BicycleLaneRightBusLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_MIXED_LEFT("BicycleLaneRightMixedLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_MIT_LEFT("BicycleLaneRightMitLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_PEDESTRIAN_LEFT("BicycleLaneRightPedestrianLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_NO_LEFT("BicycleLaneRightNoLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_LEFT_BUS_RIGHT("BicycleLaneLeftBusRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_LEFT_MIXED_RIGHT("BicycleLaneLeftMixedRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_LEFT_MIT_RIGHT("BicycleLaneLeftMitRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_LEFT_PEDESTRIAN_RIGHT("BicycleLaneLeftPedestrianRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_LEFT_NO_RIGHT("BicycleLaneLeftNoRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),

    // Bus lanes
    BUS_LANE_BOTH("BusLaneBoth", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_MIXED_LEFT("BusLaneRightMixedLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_MIT_LEFT("BusLaneRightMitLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_PEDESTRIAN_LEFT("BusLaneRightPedestrianLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_NO_LEFT("BusLaneRightNoLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_LEFT_MIXED_RIGHT("BusLaneLeftMixedRight", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_LEFT_MIT_RIGHT("BusLaneLeftMitRight", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_LEFT_PEDESTRIAN_RIGHT("BusLaneLeftPedestrianRight", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_LEFT_NO_RIGHT("BusLaneLeftNoRight", SimplifiedBikeInfrastructure.BUS_LANE),

    // Mixed ways
    MIXED_WAY_BOTH("MixedWayBoth", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_MIT_LEFT("MixedWayRightMitLeft", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_PEDESTRIAN_LEFT("MixedWayRightPedestrianLeft", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_NO_LEFT("MixedWayRightNoLeft", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_LEFT_MIT_RIGHT("MixedWayLeftMitRight", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_LEFT_PEDESTRIAN_RIGHT("MixedWayLeftPedestrianRight", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_LEFT_NO_RIGHT("MixedWayLeftNoRight", SimplifiedBikeInfrastructure.MIXED_WAY),

    // The following 4 sections + [NO] are represented by [NO] from client-perspective (only shown/selectable):
    // [SERVICE_MISC, MIT_ROAD, PEDESTRIAN, PATH_NOT_FORBIDDEN]

    // Service
    SERVICE_MISC("ServiceMisc", SimplifiedBikeInfrastructure.NO),

    // MIT roads
    MIT_ROAD_BOTH("MitRoadBoth", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_RIGHT_PEDESTRIAN_LEFT("MitRoadRightPedestrianLeft", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_RIGHT_NO_LEFT("MitRoadRightNoLeft", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_LEFT_PEDESTRIAN_RIGHT("MitRoadLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_LEFT_NO_RIGHT("MitRoadLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Pedestrian
    PEDESTRIAN_BOTH("PedestrianBoth", SimplifiedBikeInfrastructure.NO),
    PEDESTRIAN_RIGHT_NO_LEFT("PedestrianRightNoLeft", SimplifiedBikeInfrastructure.NO),
    PEDESTRIAN_LEFT_NO_RIGHT("PedestrianLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Path
    PATH_NOT_FORBIDDEN("PathNotForbidden", SimplifiedBikeInfrastructure.NO),

    // Default
    NO("No", SimplifiedBikeInfrastructure.NO);

    companion object {
        /**
         * The RadSim tag used to store the infrastructure type.
         */
        const val RADSIM_TAG = "roadStyle"

        /**
         * Tries to parse a [BikeInfrastructure] from the provided value.
         *
         * @param value The value to parse
         * @return The [BikeInfrastructure] matching the provided value
         * @throws NoSuchElementException if no matching value was found
         */
        @Suppress("unused") // Part of the API, used by `backend.Way.Properties`
        fun fromValue(value: String): BikeInfrastructure {
            return entries.first { it.value == value }
        }

        /**
         * All OSM tags that are used to determine the RadSim infrastructure type.
         *
         * This list is used by `backend.RadSimTagMerger.merge` which already implements the first step of the
         * simplified back-mapping: all tags which interfere with the [BikeInfrastructure] mapping are removed.
         *
         * This list is also used by `backend.AreaRoadNetworkProcessingVerticle` to ensure these tags are processed.
         */
        @Suppress("unused") // Part of the API
        val specificOsmTags = OsmTag.entries.map { it.key }

        /**
         * All OSM tags that are used to determine the RadSim infrastructure type.
         */
        @Suppress("SpellCheckingInspection")
        private enum class OsmTag(val key: String) {
            ACCESS("access"),
            BICYCLE("bicycle"),
            BICYCLE_ROAD("bicycle_road"),
            CYCLESTREET("cyclestreet"),
            CYCLEWAY("cycleway"),
            CYCLEWAY_BICYCLE("cycleway:bicycle"),
            CYCLEWAY_BOTH("cycleway:both"),
            CYCLEWAY_LEFT("cycleway:left"),
            CYCLEWAY_LEFT_BICYCLE("cycleway:left:bicycle"),
            CYCLEWAY_LEFT_LANE("cycleway:left:lane"),
            CYCLEWAY_RIGHT("cycleway:right"),
            CYCLEWAY_RIGHT_BICYCLE("cycleway:right:bicycle"),
            CYCLEWAY_RIGHT_LANE("cycleway:right:lane"),
            CYCLE_HIGHWAY("cycle_highway"),
            FOOT("foot"),
            HIGHWAY("highway"),
            INDOOR("indoor"),
            LEFT_BICYCLE("left:bicycle"),
            LEFT_FOOT("left:foot"),
            LEFT_LANE("left:lane"),
            LEFT_TRAFFIC_SIGN("left:traffic_sign"),
            MOTOR_VEHICLE("motor_vehicle"),
            RIGHT_BICYCLE("right:bicycle"),
            RIGHT_FOOT("right:foot"),
            RIGHT_LANE("right:lane"),
            RIGHT_TRAFFIC_SIGN("right:traffic_sign"),
            SEGREGATED("segregated"),
            SIDEWALK("sidewalk"),
            SIDEWALK_BOTH("sidewalk:both"),
            SIDEWALK_FOOT("sidewalk:foot"),
            SIDEWALK_LEFT("sidewalk:left"),
            SIDEWALK_LEFT_FOOT("sidewalk:left:foot"),
            SIDEWALK_RIGHT("sidewalk:right"),
            SIDEWALK_RIGHT_FOOT("sidewalk:right:foot"),
            TRACK_TYPE("tracktype"),
            TRAFFIC_SIGN("traffic_sign"),
            TRAFFIC_SIGN_FORWARD("traffic_sign:forward"),
            TRAM("tram"),
        }

        /**
         * All OSM Tag values used to determine the RadSim infrastructure type.
         */
        @Suppress("SpellCheckingInspection")
        enum class OsmValue(val value: String) {
            AGRICULTURAL("agricultural"),
            BOTH("both"),
            CORRIDOR("corridor"),
            CROSSING("crossing"),
            CUSTOMERS("customers"),
            CYCLEWAY("cycleway"),
            DESIGNATED("designated"),
            DISMOUNT("dismount"),
            EXCLUSIVE("exclusive"),
            FOOTWAY("footway"),
            FORESTRY("forestry"),
            GRADE1("grade1"),
            GRADE2("grade2"),
            LANE("lane"),
            LEFT("left"),
            LIVING_STREET("living_street"),
            MOTORWAY("motorway"),
            MOTORWAY_LINK("motorway_link"),
            NO("no"),
            PATH("path"),
            PEDESTRIAN("pedestrian"),
            PRIMARY("primary"),
            PRIMARY_LINK("primary_link"),
            RESIDENTIAL("residential"),
            RIGHT("right"),
            ROAD("road"),
            SECONDARY("secondary"),
            SECONDARY_LINK("secondary_link"),
            SEPARATED("separated"),
            SERVICE("service"),
            SHARED_LANE("shared_lane"),
            SHARE_BUS_WAY("share_busway"),
            SIDE_PATH("sidepath"),
            TERTIARY("tertiary"),
            TERTIARY_LINK("tertiary_link"),
            TRACK("track"),
            TRUNK("trunk"),
            TRUNK_LINK("trunk_link"),
            TWO_FOUR_ONE("241"),
            TWO_THREE_SEVEN("237"),
            UNCLASSIFIED("unclassified"),
            USE_SIDE_PATH("use_sidepath"),
            YES("yes"),
        }

        /**
         * Find the infrastructure type based on the provided OSM tags.
         *
         * This method checks specific OSM tags hierarchically and returns the first match.
         *
         * It maps to the [BikeInfrastructure] which checks left and right side separately.
         *
         * @param tags The tags to search for the infrastructure type.
         * @return The infrastructure type based on the provided tags.
         */
        // We keep the method structure to be easier comparable with the mapping from our project partner:
        // https://github.com/1prk/osm_categorizer/blob/radsim/netapy/assessor_free.py
        @Suppress("CyclomaticComplexMethod", "LongMethod", "ComplexMethod", "ReturnCount", "ComplexCondition")
        fun toRadSim(tags: Map<String, Any>): BikeInfrastructure {
            if ((tags.containsKey(OsmTag.ACCESS.key) && isNotAccessible(tags)) ||
                (tags.containsKey(OsmTag.TRAM.key) && tags[OsmTag.TRAM.key] == OsmValue.YES.value)
            ) {
                return NO // unpacked from `service`
            }

            if (isService(tags)) return SERVICE_MISC
            if (isCycleHighway(tags)) return CYCLE_HIGHWAY
            if (isBikeRoad(tags)) return BICYCLE_ROAD

            if (bicycleWayRight(tags)) {
                return when {
                    bicycleWayLeft(tags) -> BICYCLE_WAY_BOTH
                    isBikeLaneLeft(tags) -> BICYCLE_WAY_RIGHT_LANE_LEFT
                    isBusLaneLeft(tags) -> BICYCLE_WAY_RIGHT_BUS_LEFT
                    mixedWayLeft(tags) -> BICYCLE_WAY_RIGHT_MIXED_LEFT
                    mitRoadLeft(tags) -> BICYCLE_WAY_RIGHT_MIT_LEFT
                    isPedestrianLeft(tags) -> BICYCLE_WAY_RIGHT_PEDESTRIAN_LEFT
                    else -> BICYCLE_WAY_RIGHT_NO_LEFT
                }
            }

            if (bicycleWayLeft(tags)) {
                return when {
                    isBikeLaneRight(tags) -> BICYCLE_WAY_LEFT_LANE_RIGHT
                    isBusLaneRight(tags) -> BICYCLE_WAY_LEFT_BUS_RIGHT
                    mixedWayRight(tags) -> BICYCLE_WAY_LEFT_MIXED_RIGHT
                    mitRoadRight(tags) -> BICYCLE_WAY_LEFT_MIT_RIGHT
                    isPedestrianRight(tags) -> BICYCLE_WAY_LEFT_PEDESTRIAN_RIGHT
                    else -> BICYCLE_WAY_LEFT_NO_RIGHT
                }
            }

            if (isBikeLaneRight(tags)) {
                return when {
                    isBikeLaneLeft(tags) -> BICYCLE_LANE_BOTH
                    isBusLaneLeft(tags) -> BICYCLE_LANE_RIGHT_BUS_LEFT
                    mixedWayLeft(tags) -> BICYCLE_LANE_RIGHT_MIXED_LEFT
                    mitRoadLeft(tags) -> BICYCLE_LANE_RIGHT_MIT_LEFT
                    isPedestrianLeft(tags) -> BICYCLE_LANE_RIGHT_PEDESTRIAN_LEFT
                    else -> BICYCLE_LANE_RIGHT_NO_LEFT
                }
            }

            if (isBikeLaneLeft(tags)) {
                return when {
                    isBusLaneRight(tags) -> BICYCLE_LANE_LEFT_BUS_RIGHT
                    mixedWayRight(tags) -> BICYCLE_LANE_LEFT_MIXED_RIGHT
                    mitRoadRight(tags) -> BICYCLE_LANE_LEFT_MIT_RIGHT
                    isPedestrianRight(tags) -> BICYCLE_LANE_LEFT_PEDESTRIAN_RIGHT
                    else -> BICYCLE_LANE_LEFT_NO_RIGHT
                }
            }

            if (isBusLaneRight(tags)) {
                return when {
                    isBusLaneLeft(tags) -> BUS_LANE_BOTH
                    mixedWayLeft(tags) -> BUS_LANE_RIGHT_MIXED_LEFT
                    mitRoadLeft(tags) -> BUS_LANE_RIGHT_MIT_LEFT
                    isPedestrianLeft(tags) -> BUS_LANE_RIGHT_PEDESTRIAN_LEFT
                    else -> BUS_LANE_RIGHT_NO_LEFT
                }
            }

            if (isBusLaneLeft(tags)) {
                return when {
                    mixedWayRight(tags) -> BUS_LANE_LEFT_MIXED_RIGHT
                    mitRoadRight(tags) -> BUS_LANE_LEFT_MIT_RIGHT
                    isPedestrianRight(tags) -> BUS_LANE_LEFT_PEDESTRIAN_RIGHT
                    else -> BUS_LANE_LEFT_NO_RIGHT
                }
            }

            if (mixedWayRight(tags)) {
                return when {
                    mixedWayLeft(tags) -> MIXED_WAY_BOTH
                    mitRoadLeft(tags) -> MIXED_WAY_RIGHT_MIT_LEFT
                    isPedestrianLeft(tags) -> MIXED_WAY_RIGHT_PEDESTRIAN_LEFT
                    else -> MIXED_WAY_RIGHT_NO_LEFT
                }
            }

            if (mixedWayLeft(tags)) {
                return when {
                    mitRoadRight(tags) -> MIXED_WAY_LEFT_MIT_RIGHT
                    isPedestrianRight(tags) -> MIXED_WAY_LEFT_PEDESTRIAN_RIGHT
                    else -> MIXED_WAY_LEFT_NO_RIGHT
                }
            }

            if (mitRoadRight(tags)) {
                return when {
                    mitRoadLeft(tags) -> MIT_ROAD_BOTH
                    isPedestrianLeft(tags) -> MIT_ROAD_RIGHT_PEDESTRIAN_LEFT
                    else -> MIT_ROAD_RIGHT_NO_LEFT
                }
            }

            if (mitRoadLeft(tags)) {
                return when {
                    isPedestrianRight(tags) -> MIT_ROAD_LEFT_PEDESTRIAN_RIGHT
                    else -> MIT_ROAD_LEFT_NO_RIGHT
                }
            }

            if (isPedestrianRight(tags) && tags[OsmTag.INDOOR.key] != OsmValue.YES.value) {
                return if (isPedestrianLeft(tags) && tags[OsmTag.INDOOR.key] != OsmValue.YES.value) {
                    if (tags[OsmTag.ACCESS.key] == OsmValue.CUSTOMERS.value) {
                        NO
                    } else {
                        PEDESTRIAN_BOTH
                    }
                } else {
                    PEDESTRIAN_RIGHT_NO_LEFT
                }
            }

            if (isPedestrianLeft(tags) && tags[OsmTag.INDOOR.key] != OsmValue.YES.value) {
                return if (tags[OsmTag.ACCESS.key] == OsmValue.CUSTOMERS.value) {
                    NO
                } else {
                    PEDESTRIAN_LEFT_NO_RIGHT
                }
            }

            if (isPathNotForbidden(tags)) return PATH_NOT_FORBIDDEN

            // Fallback option
            return NO
        }

        // `bicycle_way`

        private fun bicycleWayRight(tags: Map<String, Any>): Boolean {
            return listOf(
                isBikePathRight(tags) && !canWalkRight(tags), // 0 and 1
                isBikePathRight(tags) && isSegregated(tags), // 0 and 2
                canBike(tags) && (isPath(tags) || isTrack(tags)) && !canWalkRight(tags), // 3, 4, 1
                // b_way_right_5
                canBike(tags) && (isTrack(tags) || isFootpath(tags) || isPath(tags)) && isSegregated(tags),
                canBike(tags) && isObligatedSegregated(tags), // 3,7
                isBicycleDesignatedRight(tags) && isPedestrianDesignatedRight(tags) && isSegregated(tags)
            ).any { it }
        }

        private fun bicycleWayLeft(tags: Map<String, Any>): Boolean {
            return listOf(
                isBikePathLeft(tags) && !canWalkLeft(tags), // 0 and 1
                isBikePathLeft(tags) && isSegregated(tags), // 0 and 2
                canBike(tags) && (isPath(tags) || isTrack(tags)) && !canWalkLeft(tags), // 3, 4, 1
                // b_way_left_5
                canBike(tags) && (isTrack(tags) || isFootpath(tags) || isPath(tags)) && isSegregated(tags),
                canBike(tags) && isObligatedSegregated(tags), // 3,7
                isBicycleDesignatedLeft(tags) && isPedestrianDesignatedLeft(tags) && isSegregated(tags)
            ).any { it }
        }

        // `mixed_way`

        private fun mixedWayRight(tags: Map<String, Any>): Boolean {
            return listOf(
                isBikePathRight(tags) && canWalkRight(tags) && !isSegregated(tags), // 0 and 1 and 2
                isFootpath(tags) && canBike(tags) && !isSegregated(tags), // 3 and 4 and 2
                // 5 and 4 and 1 and 2
                (isPath(tags) || isTrack(tags)) && canBike(tags) && canWalkRight(tags) && !isSegregated(tags)
            ).any { it }
        }

        private fun mixedWayLeft(tags: Map<String, Any>): Boolean {
            return listOf(
                isBikePathLeft(tags) && canWalkLeft(tags) && !isSegregated(tags), // 0 and 1 and 2
                isFootpath(tags) && canBike(tags) && !isSegregated(tags), // 3 and 4 and 2
                // 5 and 4 and 1 and 2
                (isPath(tags) || isTrack(tags)) && canBike(tags) && canWalkLeft(tags) && !isSegregated(tags)
            ).any { it }
        }

        // `mit_road` - both, cars and bikes are allowed, but no dedicated bike infrastructure
        // MIT is used as an abbreviation for _motorized transport_

        private fun mitRoadRight(tags: Map<String, Any>): Boolean {
            return listOf(
                canCarDrive(tags) && !isBikePathRight(tags) && !isBikeRoad(tags) && !isFootpath(tags) &&
                    !isBikeLaneRight(tags) && !isBusLaneRight(tags) && !isPath(tags) && !isTrack(tags) &&
                    !cannotBike(tags)
            ).any { it }
        }

        private fun mitRoadLeft(tags: Map<String, Any>): Boolean {
            return listOf(
                canCarDrive(tags) && !isBikePathLeft(tags) && !isBikeRoad(tags) && !isFootpath(tags) &&
                    !isBikeLaneLeft(tags) && !isBusLaneLeft(tags) && !isPath(tags) && !isTrack(tags) &&
                    !cannotBike(tags)
            ).any { it }
        }

        private val isSegregated: (Map<String, Any>) -> Boolean = { tags ->
            tags.any { (key, value) ->
                OsmTag.SEGREGATED.key in key && value == OsmValue.YES.value
            }
        }

        private val isFootpath: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            highway != null && highway in listOf(OsmValue.FOOTWAY.value, OsmValue.PEDESTRIAN.value)
        }

        val isNotAccessible: (Map<String, Any>) -> Boolean = { tags ->
            val access = tags[OsmTag.ACCESS.key] as? String
            access == OsmValue.NO.value
        }

        private val isIndoor: (Map<String, Any>) -> Boolean = { tags ->
            val indoor = tags[OsmTag.INDOOR.key] as? String
            indoor == OsmValue.YES.value
        }

        private val isPath: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            highway == OsmValue.PATH.value
        }

        private val isTrack: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            highway == OsmValue.TRACK.value
        }

        private val canWalkRight: (Map<String, Any>) -> Boolean = { tags ->
            val foot = tags[OsmTag.FOOT.key] as? String
            val sidewalk = tags[OsmTag.SIDEWALK.key] as? String
            val sidewalkRight = tags[OsmTag.SIDEWALK_RIGHT.key] as? String
            val sidewalkBoth = tags[OsmTag.SIDEWALK_BOTH.key] as? String

            (foot in listOf(OsmValue.YES.value, OsmValue.DESIGNATED.value)) ||
                tags.any { (key, value) ->
                    OsmTag.RIGHT_FOOT.key in key && value in listOf(
                        OsmValue.YES.value,
                        OsmValue.DESIGNATED.value
                    )
                } ||
                (
                    sidewalk in listOf(
                        OsmValue.YES.value,
                        OsmValue.SEPARATED.value,
                        OsmValue.BOTH.value,
                        OsmValue.RIGHT.value,
                        OsmValue.LEFT.value
                    )
                    ) ||
                (
                    sidewalkRight in listOf(
                        OsmValue.YES.value,
                        OsmValue.SEPARATED.value,
                        OsmValue.BOTH.value,
                        OsmValue.RIGHT.value
                    )
                    ) ||
                (
                    sidewalkBoth in listOf(OsmValue.YES.value, OsmValue.SEPARATED.value, OsmValue.BOTH.value)
                    )
        }

        private val canWalkLeft: (Map<String, Any>) -> Boolean = { tags ->
            val foot = tags[OsmTag.FOOT.key] as? String
            val sidewalk = tags[OsmTag.SIDEWALK.key] as? String
            val sidewalkLeft = tags[OsmTag.SIDEWALK_LEFT.key] as? String
            val sidewalkBoth = tags[OsmTag.SIDEWALK_BOTH.key] as? String

            (foot in listOf(OsmValue.YES.value, OsmValue.DESIGNATED.value)) ||
                tags.any { (key, value) ->
                    OsmTag.LEFT_FOOT.key in key && value in listOf(
                        OsmValue.YES.value,
                        OsmValue.DESIGNATED.value
                    )
                } ||
                (
                    sidewalk in listOf(
                        OsmValue.YES.value,
                        OsmValue.SEPARATED.value,
                        OsmValue.BOTH.value,
                        OsmValue.RIGHT.value,
                        OsmValue.LEFT.value
                    )
                    ) ||
                (
                    sidewalkLeft in listOf(
                        OsmValue.YES.value,
                        OsmValue.SEPARATED.value,
                        OsmValue.BOTH.value,
                        OsmValue.LEFT.value
                    )
                    ) ||
                (
                    sidewalkBoth in listOf(OsmValue.YES.value, OsmValue.SEPARATED.value, OsmValue.BOTH.value)
                    )
        }

        private val canBike: (Map<String, Any>) -> Boolean = { tags ->
            val bicycle = tags[OsmTag.BICYCLE.key] as? String
            val highway = tags[OsmTag.HIGHWAY.key] as? String

            (bicycle in listOf(OsmValue.YES.value, OsmValue.DESIGNATED.value)) &&
                (highway !in listOf(OsmValue.MOTORWAY.value, OsmValue.MOTORWAY_LINK.value))
        }

        private val cannotBike: (Map<String, Any>) -> Boolean = { tags ->
            val bicycle = tags[OsmTag.BICYCLE.key] as? String
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            val access = tags[OsmTag.ACCESS.key] as? String

            (
                bicycle in listOf(OsmValue.NO.value, OsmValue.DISMOUNT.value, OsmValue.USE_SIDE_PATH.value)
                ) ||
                (
                    highway in listOf(
                        OsmValue.CORRIDOR.value,
                        OsmValue.MOTORWAY.value,
                        OsmValue.MOTORWAY_LINK.value,
                        OsmValue.TRUNK.value,
                        OsmValue.TRUNK_LINK.value
                    )
                    ) ||
                (access == OsmValue.CUSTOMERS.value)
        }

        private val isObligatedSegregated: (Map<String, Any>) -> Boolean = { tags ->
            val trafficSign = tags[OsmTag.TRAFFIC_SIGN.key] as? String
            val trafficSignForward = tags[OsmTag.TRAFFIC_SIGN_FORWARD.key] as? String

            (trafficSign != null && trafficSign.contains(OsmValue.TWO_FOUR_ONE.value)) ||
                (trafficSignForward != null && trafficSignForward.contains(OsmValue.TWO_FOUR_ONE.value))
        }

        private val isDesignated: (Map<String, Any>) -> Boolean = { tags ->
            val bicycle = tags[OsmTag.BICYCLE.key] as? String
            bicycle == OsmValue.DESIGNATED.value
        }

        private val isBicycleDesignatedLeft: (Map<String, Any>) -> Boolean = { tags ->
            val cyclewayLeftBicycle = tags[OsmTag.CYCLEWAY_LEFT_BICYCLE.key] as? String
            val cyclewayBicycle = tags[OsmTag.CYCLEWAY_BICYCLE.key] as? String

            isDesignated(tags) ||
                (cyclewayLeftBicycle == OsmValue.DESIGNATED.value) ||
                (cyclewayBicycle == OsmValue.DESIGNATED.value)
        }

        private val isBicycleDesignatedRight: (Map<String, Any>) -> Boolean = { tags ->
            val cyclewayRightBicycle = tags[OsmTag.CYCLEWAY_RIGHT_BICYCLE.key] as? String
            val cyclewayBicycle = tags[OsmTag.CYCLEWAY_BICYCLE.key] as? String

            isDesignated(tags) ||
                (cyclewayRightBicycle == OsmValue.DESIGNATED.value) ||
                (cyclewayBicycle == OsmValue.DESIGNATED.value)
        }

        private val isPedestrianDesignatedLeft: (Map<String, Any>) -> Boolean = { tags ->
            val foot = tags[OsmTag.FOOT.key] as? String
            val sidewalkLeftFoot = tags[OsmTag.SIDEWALK_LEFT_FOOT.key] as? String
            val sidewalkFoot = tags[OsmTag.SIDEWALK_FOOT.key] as? String

            (foot == OsmValue.DESIGNATED.value) ||
                (sidewalkLeftFoot == OsmValue.DESIGNATED.value) ||
                (sidewalkFoot == OsmValue.DESIGNATED.value)
        }

        private val isPedestrianDesignatedRight: (Map<String, Any>) -> Boolean = { tags ->
            val foot = tags[OsmTag.FOOT.key] as? String
            val sidewalkRightFoot = tags[OsmTag.SIDEWALK_RIGHT_FOOT.key] as? String
            val sidewalkFoot = tags[OsmTag.SIDEWALK_FOOT.key] as? String

            (foot == OsmValue.DESIGNATED.value) ||
                (sidewalkRightFoot == OsmValue.DESIGNATED.value) ||
                (sidewalkFoot == OsmValue.DESIGNATED.value)
        }

        private val isServiceTag: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            highway == OsmValue.SERVICE.value
        }

        private val isAgricultural: (Map<String, Any>) -> Boolean = { tags ->
            val motorVehicle = tags[OsmTag.MOTOR_VEHICLE.key] as? String
            motorVehicle in listOf(OsmValue.AGRICULTURAL.value, OsmValue.FORESTRY.value)
        }

        private val isAccessible: (Map<String, Any>) -> Boolean = { tags ->
            val accessValue = tags[OsmTag.ACCESS.key] as? String
            accessValue == null || !isNotAccessible(tags)
        }

        private val isSmooth: (Map<String, Any>) -> Boolean = { tags ->
            val trackType = tags[OsmTag.TRACK_TYPE.key] as? String
            trackType == null || trackType in listOf(OsmValue.GRADE1.value, OsmValue.GRADE2.value)
        }

        private val isVehicleAllowed: (Map<String, Any>) -> Boolean = { tags ->
            val motorVehicle = tags[OsmTag.MOTOR_VEHICLE.key] as? String
            motorVehicle == null || motorVehicle != OsmValue.NO.value
        }

        val isService: (Map<String, Any>) -> Boolean = { tags ->
            val designated = isDesignated(tags)
            (
                isServiceTag(tags) ||
                    (isAgricultural(tags) && isAccessible(tags)) ||
                    (isPath(tags) && isAccessible(tags)) ||
                    (isTrack(tags) && isAccessible(tags) && isSmooth(tags) && isVehicleAllowed(tags))
                ) &&
                !designated
        }

        private val canCarDrive: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            highway in listOf(
                OsmValue.MOTORWAY.value,
                OsmValue.TRUNK.value,
                OsmValue.PRIMARY.value,
                OsmValue.SECONDARY.value,
                OsmValue.TERTIARY.value,
                OsmValue.UNCLASSIFIED.value,
                OsmValue.ROAD.value,
                OsmValue.RESIDENTIAL.value,
                OsmValue.LIVING_STREET.value,
                OsmValue.PRIMARY_LINK.value,
                OsmValue.SECONDARY_LINK.value,
                OsmValue.TERTIARY_LINK.value,
                OsmValue.MOTORWAY_LINK.value,
                OsmValue.TRUNK_LINK.value
            )
        }

        val isPathNotForbidden: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            (
                highway in listOf(OsmValue.CYCLEWAY.value, OsmValue.TRACK.value, OsmValue.PATH.value)
                ) &&
                !cannotBike(tags)
        }

        private val isBikePathRight: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayRight = tags[OsmTag.CYCLEWAY_RIGHT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (highway == OsmValue.CYCLEWAY.value) ||
                (
                    tags.any { (key, value) ->
                        OsmTag.RIGHT_BICYCLE.key in key && value == OsmValue.DESIGNATED.value
                    } &&
                        tags.none { (key, _) -> key == OsmTag.CYCLEWAY_RIGHT_LANE.key }
                    ) ||
                (
                    cycleway in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)
                    ) ||
                (
                    cyclewayRight in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)
                    ) ||
                (
                    cyclewayBoth in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)
                    ) ||
                tags.any { (key, value) ->
                    OsmTag.RIGHT_TRAFFIC_SIGN.key in key && value == OsmValue.TWO_THREE_SEVEN.value
                }
        }

        private val isBikePathLeft: (Map<String, Any>) -> Boolean = { tags ->
            val highway = tags[OsmTag.HIGHWAY.key] as? String
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayLeft = tags[OsmTag.CYCLEWAY_LEFT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (highway == OsmValue.CYCLEWAY.value) ||
                (
                    tags.any { (key, value) -> OsmTag.LEFT_BICYCLE.key in key && value == OsmValue.DESIGNATED.value } &&
                        tags.none { (key, _) -> key == OsmTag.CYCLEWAY_LEFT_LANE.key }
                    ) ||
                (cycleway in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)) ||
                (cyclewayLeft in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)) ||
                (cyclewayBoth in listOf(OsmValue.TRACK.value, OsmValue.SIDE_PATH.value, OsmValue.CROSSING.value)) ||
                tags.any { (key, value) ->
                    OsmTag.LEFT_TRAFFIC_SIGN.key in key && value == OsmValue.TWO_THREE_SEVEN.value
                }
        }

        val isPedestrianRight: (Map<String, Any>) -> Boolean = { tags ->
            !isIndoor(tags) && (
                (isFootpath(tags) && !canBike(tags)) ||
                    (isPath(tags) && canWalkRight(tags) && !canBike(tags))
                )
        }

        val isPedestrianLeft: (Map<String, Any>) -> Boolean = { tags ->
            !isIndoor(tags) && (
                (isFootpath(tags) && !canBike(tags)) || (isPath(tags) && canWalkLeft(tags) && !canBike(tags))
                )
        }

        val isCycleHighway: (Map<String, Any>) -> Boolean = { tags ->
            val cycleHighway = tags[OsmTag.CYCLE_HIGHWAY.key] as? String
            cycleHighway == OsmValue.YES.value
        }

        val isBikeRoad: (Map<String, Any>) -> Boolean = { tags ->
            val bicycleRoad = tags[OsmTag.BICYCLE_ROAD.key] as? String
            val cycleStreet = tags[OsmTag.CYCLESTREET.key] as? String
            bicycleRoad == OsmValue.YES.value || cycleStreet == OsmValue.YES.value
        }

        val isBikeLaneRight: (Map<String, Any>) -> Boolean = { tags ->
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayRight = tags[OsmTag.CYCLEWAY_RIGHT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (cycleway in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                (cyclewayRight in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                (cyclewayBoth in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                tags.any { (key, value) ->
                    OsmTag.RIGHT_LANE.key in key && value == OsmValue.EXCLUSIVE.value
                }
        }

        val isBikeLaneLeft: (Map<String, Any>) -> Boolean = { tags ->
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayLeft = tags[OsmTag.CYCLEWAY_LEFT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (cycleway in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                (cyclewayLeft in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                (cyclewayBoth in listOf(OsmValue.LANE.value, OsmValue.SHARED_LANE.value)) ||
                tags.any { (key, value) ->
                    OsmTag.LEFT_LANE.key in key && value == OsmValue.EXCLUSIVE.value
                }
        }

        val isBusLaneRight: (Map<String, Any>) -> Boolean = { tags ->
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayRight = tags[OsmTag.CYCLEWAY_RIGHT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (cycleway == OsmValue.SHARE_BUS_WAY.value) ||
                (cyclewayRight == OsmValue.SHARE_BUS_WAY.value) ||
                (cyclewayBoth == OsmValue.SHARE_BUS_WAY.value)
        }

        val isBusLaneLeft: (Map<String, Any>) -> Boolean = { tags ->
            val cycleway = tags[OsmTag.CYCLEWAY.key] as? String
            val cyclewayLeft = tags[OsmTag.CYCLEWAY_LEFT.key] as? String
            val cyclewayBoth = tags[OsmTag.CYCLEWAY_BOTH.key] as? String

            (cycleway == OsmValue.SHARE_BUS_WAY.value) ||
                (cyclewayLeft == OsmValue.SHARE_BUS_WAY.value) ||
                (cyclewayBoth == OsmValue.SHARE_BUS_WAY.value)
        }
    }
}
