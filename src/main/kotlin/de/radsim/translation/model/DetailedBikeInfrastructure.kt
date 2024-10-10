package de.radsim.translation.model

/**
 * An enum with the infrastructure types in the "detailed" format.
 *
 * "Detailed" represents the way OpenStreetMap (OSM) annotated bicycle infrastructure:
 * - Bicycle infrastructure and usage is tagged to the road's OSM ways and geometry.
 * - e.g. a road with a bicycle lane on the right
 *
 * The [DetailedBikeInfrastructure] includes can be used to map from OSM to Radsim tags.
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
 * @property backMappingType The simplified infrastructure type that this detail maps to.
 */
enum class DetailedBikeInfrastructure(val value: String, /*FIXME: REMOVE */ val backMappingType: SimplifiedBikeInfrastructure) {
    // FIXME: check the back-mapping (generated / guessed)
    // - mit/no => always use the first part, e.g. `bicycle_way_right_no_left` = `bicycle_way`
    // - later, the UI and mapping should handle both directions separately:
    //  `bicycle_way_right_lane_left` should be
    //  a) shown in the UI and Map as `bicycle_way` right, `lane` left
    //  b) the user should be able to change both sides individually
    //  c) the back-mapping merges both directions into one or multiple tags (or maybe even splits the geometry)
    //     But this then needs to take the forward/backend, one-way and formal direction into account.

    // Service
    SERVICE_MISC("ServiceMisc", SimplifiedBikeInfrastructure.SERVICE_MISC),

    // Cycle highway
    CYCLE_HIGHWAY("CycleHighway", SimplifiedBikeInfrastructure.CYCLE_HIGHWAY),

    // Bicycle road
    BICYCLE_ROAD("BicycleRoad", SimplifiedBikeInfrastructure.BICYCLE_ROAD),

    // Bicycle way (both sides)
    BICYCLE_WAY_BOTH("BicycleWayBoth", SimplifiedBikeInfrastructure.BICYCLE_WAY),

    // Bicycle way (right side)
    BICYCLE_WAY_RIGHT_LANE_LEFT("BicycleWayRightLaneLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_BUS_LEFT("BicycleWayRightBusLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_MIXED_LEFT("BicycleWayRightMixedLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_MIT_LEFT("BicycleWayRightMitLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_PEDESTRIAN_LEFT("BicycleWayRightPedestrianLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),
    BICYCLE_WAY_RIGHT_NO_LEFT("BicycleWayRightNoLeft", SimplifiedBikeInfrastructure.BICYCLE_WAY),

    // Bicycle way (left side)
    BICYCLE_WAY_LEFT_LANE_RIGHT("BicycleWayLeftLaneRight", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_WAY_LEFT_BUS_RIGHT("BicycleWayLeftBusRight", SimplifiedBikeInfrastructure.BUS_LANE),
    BICYCLE_WAY_LEFT_MIXED_RIGHT("BicycleWayLeftMixedRight", SimplifiedBikeInfrastructure.MIXED_WAY),
    BICYCLE_WAY_LEFT_MIT_RIGHT("BicycleWayLeftMitRight", SimplifiedBikeInfrastructure.MIT_ROAD),
    BICYCLE_WAY_LEFT_PEDESTRIAN_RIGHT("BicycleWayLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    BICYCLE_WAY_LEFT_NO_RIGHT("BicycleWayLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Bicycle lane (both sides)
    BICYCLE_LANE_BOTH("BicycleLaneBoth", SimplifiedBikeInfrastructure.BICYCLE_LANE),

    // Bicycle lane (right side)
    BICYCLE_LANE_RIGHT_BUS_LEFT("BicycleLaneRightBusLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_MIXED_LEFT("BicycleLaneRightMixedLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_MIT_LEFT("BicycleLaneRightMitLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_PEDESTRIAN_LEFT("BicycleLaneRightPedestrianLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),
    BICYCLE_LANE_RIGHT_NO_LEFT("BicycleLaneRightNoLeft", SimplifiedBikeInfrastructure.BICYCLE_LANE),

    // Bicycle lane (left side)
    BICYCLE_LANE_LEFT_BUS_RIGHT("BicycleLaneLeftBusRight", SimplifiedBikeInfrastructure.BUS_LANE),
    BICYCLE_LANE_LEFT_MIXED_RIGHT("BicycleLaneLeftMixedRight", SimplifiedBikeInfrastructure.MIXED_WAY),
    BICYCLE_LANE_LEFT_MIT_RIGHT("BicycleLaneLeftMitRight", SimplifiedBikeInfrastructure.MIT_ROAD),
    BICYCLE_LANE_LEFT_PEDESTRIAN_RIGHT("BicycleLaneLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    BICYCLE_LANE_LEFT_NO_RIGHT("BicycleLaneLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Bus lanes
    BUS_LANE_BOTH("BusLaneBoth", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_MIXED_LEFT("BusLaneRightMixedLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_MIT_LEFT("BusLaneRightMitLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_PEDESTRIAN_LEFT("BusLaneRightPedestrianLeft", SimplifiedBikeInfrastructure.BUS_LANE),
    BUS_LANE_RIGHT_NO_LEFT("BusLaneRightNoLeft", SimplifiedBikeInfrastructure.BUS_LANE),

    BUS_LANE_LEFT_MIXED_RIGHT("BusLaneLeftMixedRight", SimplifiedBikeInfrastructure.MIXED_WAY),
    BUS_LANE_LEFT_MIT_RIGHT("BusLaneLeftMitRight", SimplifiedBikeInfrastructure.MIT_ROAD),
    BUS_LANE_LEFT_PEDESTRIAN_RIGHT("BusLaneLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    BUS_LANE_LEFT_NO_RIGHT("BusLaneLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Mixed ways
    MIXED_WAY_BOTH("MixedWayBoth", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_MIT_LEFT("MixedWayRightMitLeft", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_PEDESTRIAN_LEFT("MixedWayRightPedestrianLeft", SimplifiedBikeInfrastructure.MIXED_WAY),
    MIXED_WAY_RIGHT_NO_LEFT("MixedWayRightNoLeft", SimplifiedBikeInfrastructure.MIXED_WAY),

    MIXED_WAY_LEFT_MIT_RIGHT("MixedWayLeftMitRight", SimplifiedBikeInfrastructure.MIT_ROAD),
    MIXED_WAY_LEFT_PEDESTRIAN_RIGHT("MixedWayLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    MIXED_WAY_LEFT_NO_RIGHT("MixedWayLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // MIT roads
    MIT_ROAD_BOTH("MitRoadBoth", SimplifiedBikeInfrastructure.NO), // both = definitely "no"
    MIT_ROAD_RIGHT_PEDESTRIAN_LEFT("MitRoadRightPedestrianLeft", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_RIGHT_NO_LEFT("MitRoadRightNoLeft", SimplifiedBikeInfrastructure.NO),

    MIT_ROAD_LEFT_PEDESTRIAN_RIGHT("MitRoadLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_LEFT_NO_RIGHT("MitRoadLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Pedestrian paths
    PEDESTRIAN_BOTH("PedestrianBoth", SimplifiedBikeInfrastructure.NO), // both = definitely "no"
    PEDESTRIAN_RIGHT_NO_LEFT("PedestrianRightNoLeft", SimplifiedBikeInfrastructure.NO),
    PEDESTRIAN_LEFT_NO_RIGHT("PedestrianLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Path not forbidden
    PATH_NOT_FORBIDDEN("PathNotForbidden", SimplifiedBikeInfrastructure.NO),

    // Default
    NO("No", SimplifiedBikeInfrastructure.NO);

    companion object {
        /**
         * The RadSim tag used to store the infrastructure type.
         *
         * FIXME: See if we need this in BikeInfrastructure or DetailedBikeInfrastructure
         */
        const val RADSIM_TAG = "roadStyle"

        /**
         * All OSM tags that are used to determine the RadSim infrastructure type.
         *
         * This list is used in our simplified back-mapping:
         * - the mapping is hierarchical, so we need to ensure the mapping does not return early because of other tags
         * - thus, the simplified back-mapping sets all interpreted tags to a null-like value (not interpreted)
         * - and then only sets the relevant tag(s) to ensure the mapping returns at the new category
         * - this way, after a user selects a new category, backward and forward mapping ends up there again
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
            ;
        }

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
        fun toRadSim(tags: Map<String, String>): DetailedBikeInfrastructure {
            // Map to complex categories, separating right and left as required for OSM to Radsim mapping
            if (tags.containsValue(OsmTag.ACCESS.key) && isNotAccessible(tags) || tags[OsmTag.TRAM.key] == "yes") {
                return NO // unpacked from "service"
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

            if (isPedestrianRight(tags) && tags[OsmTag.INDOOR.key] != "yes") {
                return if (isPedestrianLeft(tags) && tags[OsmTag.INDOOR.key] != "yes") {
                    if (tags[OsmTag.ACCESS.key] == "customers") NO
                    else PEDESTRIAN_BOTH
                } else {
                    PEDESTRIAN_RIGHT_NO_LEFT
                }
            }

            if (isPedestrianLeft(tags) && tags[OsmTag.INDOOR.key] != "yes") {
                return if (tags[OsmTag.ACCESS.key] == "customers") NO
                else PEDESTRIAN_LEFT_NO_RIGHT
            }

            if (isPathNotForbidden(tags)) return PATH_NOT_FORBIDDEN

            // Fallback option: "no"
            return NO
        }

        // `bicycle_way`

        fun bicycleWayRight (tags: Map<String, String>) : Boolean {
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

        fun bicycleWayLeft (tags: Map<String, String>) : Boolean {
            return listOf(
                isBikePathLeft(tags) && !canWalkLeft(tags), // 0 and 1
                isBikePathLeft(tags) && isSegregated(tags), // 0 and 2
                canBike(tags) && (isPath(tags) || isTrack(tags)) && !canWalkLeft(tags), // 3, 4, 1
                // b_way_left_5
                canBike(tags) && (isTrack(tags) || isFootpath(tags) || isPath(tags)) && isSegregated(tags),
                canBike(tags) && isObligatedSegregated(tags), // 3,7
                isBicycleDesignatedLeft(tags) && isPedestrianDesignatedLeft(tags) && isSegregated(tags)
            ).any{ it }
        }

        // `mixed_way`

        fun mixedWayRight (tags: Map<String, String>) : Boolean{
            return listOf(
                isBikePathRight(tags) && canWalkRight(tags) && !isSegregated(tags), // 0 and 1 and 2
                isFootpath(tags) && canBike(tags) && !isSegregated(tags), // 3 and 4 and 2
                // 5 and 4 and 1 and 2
                (isPath(tags) || isTrack(tags)) && canBike(tags) && canWalkRight(tags) && !isSegregated(tags)
            ).any{ it }
        }

        fun mixedWayLeft (tags: Map<String, String>) : Boolean {
            return listOf(
                isBikePathLeft(tags) && canWalkLeft(tags) && !isSegregated(tags), // 0 and 1 and 2
                isFootpath(tags) && canBike(tags) && !isSegregated(tags), // 3 and 4 and 2
                // 5 and 4 and 1 and 2
                (isPath(tags) || isTrack(tags)) && canBike(tags) && canWalkLeft(tags) && !isSegregated(tags)
            ).any { it }
        }

        // `mit_road` - both, cars and bikes are allowed, but no dedicated bike infrastructure
        // MIT is used as an abbreviation for "motorized transport"

        fun mitRoadRight (tags: Map<String, String>) : Boolean {
            return listOf(
                canCarDrive(tags) && !isBikePathRight(tags) && !isBikeRoad(tags) && !isFootpath(tags)
                        && !isBikeLaneRight(tags) && !isBusLaneRight(tags) && !isPath(tags) && !isTrack(tags)
                        && !cannotBike(tags)
            ).any{ it }
        }

        fun mitRoadLeft (tags: Map<String, String>) : Boolean {
            return listOf(
                canCarDrive(tags) && !isBikePathLeft(tags) && !isBikeRoad(tags) && !isFootpath(tags)
                        && !isBikeLaneLeft(tags) && !isBusLaneLeft(tags) && !isPath(tags) && !isTrack(tags)
                        && !cannotBike(tags)
            ).any{ it }
        }

        private val isSegregated: (Map<String, String>) -> Boolean = { tags ->
            tags.any { (key, value) -> OsmTag.SEGREGATED.key in key && value == "yes" }
        }

        private val isFootpath: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] in listOf("footway", "pedestrian")
        }

        val isNotAccessible: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.ACCESS.key] == "no"
        }

        private val isIndoor: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.INDOOR.key] == "yes"
        }

        private val isPath: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] == "path"
        }

        private val isTrack: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] == "track"
        }

        private val canWalkRight: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.FOOT.key] in listOf("yes", "designated") ||
                    tags.any { (key, value) -> OsmTag.RIGHT_FOOT.key in key && value in listOf("yes", "designated") } ||
                    tags[OsmTag.SIDEWALK.key] in listOf("yes", "separated", "both", "right", "left") ||
                    tags[OsmTag.SIDEWALK_RIGHT.key] in listOf("yes", "separated", "both", "right") ||
                    tags[OsmTag.SIDEWALK_BOTH.key] in listOf("yes", "separated", "both")
        }

        private val canWalkLeft: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.FOOT.key] in listOf("yes", "designated") ||
                    tags.any { (key, value) -> OsmTag.LEFT_FOOT.key in key && value in listOf("yes", "designated") } ||
                    tags[OsmTag.SIDEWALK.key] in listOf("yes", "separated", "both", "right", "left") ||
                    tags[OsmTag.SIDEWALK_LEFT.key] in listOf("yes", "separated", "both", "left") ||
                    tags[OsmTag.SIDEWALK_BOTH.key] in listOf("yes", "separated", "both")
        }

        private val canBike: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.BICYCLE.key] in listOf("yes", "designated") &&
                    tags[OsmTag.HIGHWAY.key] !in listOf("motorway", "motorway_link")
        }

        private val cannotBike: (Map<String, String>) -> Boolean = { tags ->
            @Suppress("SpellCheckingInspection")
            tags[OsmTag.BICYCLE.key] in listOf("no", "dismount", "use_sidepath") ||
                    tags[OsmTag.HIGHWAY.key] in listOf("corridor", "motorway", "motorway_link", "trunk", "trunk_link") ||
                    tags[OsmTag.ACCESS.key] == "customers"
        }

        private val isObligatedSegregated: (Map<String, String>) -> Boolean = { tags ->
            (OsmTag.TRAFFIC_SIGN.key in tags.keys && "241" in tags[OsmTag.TRAFFIC_SIGN.key]!!) ||
                    (OsmTag.TRAFFIC_SIGN_FORWARD.key in tags.keys && "241" in tags[OsmTag.TRAFFIC_SIGN_FORWARD.key]!!)
        }

        private val isDesignated: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.BICYCLE.key] == "designated"
        }

        private val isBicycleDesignatedLeft: (Map<String, String>) -> Boolean = { tags ->
            isDesignated(tags) || tags[OsmTag.CYCLEWAY_LEFT_BICYCLE.key] == "designated" || tags[OsmTag.CYCLEWAY_BICYCLE.key] == "designated"
        }

        private val isBicycleDesignatedRight: (Map<String, String>) -> Boolean = { tags ->
            isDesignated(tags) || tags[OsmTag.CYCLEWAY_RIGHT_BICYCLE.key] == "designated" || tags[OsmTag.CYCLEWAY_BICYCLE.key] == "designated"
        }

        private val isPedestrianDesignatedLeft: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.FOOT.key] == "designated" || tags[OsmTag.SIDEWALK_LEFT_FOOT.key] == "designated" || tags[OsmTag.SIDEWALK_FOOT.key] == "designated"
        }

        private val isPedestrianDesignatedRight: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.FOOT.key] == "designated" || tags[OsmTag.SIDEWALK_RIGHT_FOOT.key] == "designated" || tags[OsmTag.SIDEWALK_FOOT.key] == "designated"
        }

        private val isServiceTag: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] == "service"
        }

        private val isAgricultural: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.MOTOR_VEHICLE.key] in listOf("agricultural", "forestry")
        }

        private val isAccessible: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.ACCESS.key].isNullOrEmpty() || !isNotAccessible(tags)
        }

        private val isSmooth: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.TRACK_TYPE.key].isNullOrEmpty() || tags[OsmTag.TRACK_TYPE.key] in listOf("grade1", "grade2")
        }

        private val isVehicleAllowed: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.MOTOR_VEHICLE.key].isNullOrEmpty() || tags[OsmTag.MOTOR_VEHICLE.key] != "no"
        }

        val isService: (Map<String, String>) -> Boolean = { tags ->
            (isServiceTag(tags) ||
                    (isAgricultural(tags) && isAccessible(tags)) ||
                    (isPath(tags) && isAccessible(tags)) ||
                    (isTrack(tags) && isAccessible(tags) && isSmooth(tags) && isVehicleAllowed(tags))) &&
                    !isDesignated(tags)
        }

        private val canCarDrive: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] in listOf("motorway", "trunk", "primary", "secondary", "tertiary",
                "unclassified", "road", "residential", "living_street",
                "primary_link", "secondary_link", "tertiary_link",
                "motorway_link", "trunk_link")
        }

        val isPathNotForbidden: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.HIGHWAY.key] in listOf("cycleway", "track", "path") && !cannotBike(tags)
        }

        private val isBikePathRight: (Map<String, String>) -> Boolean = { tags ->
            @Suppress("SpellCheckingInspection")
            tags[OsmTag.HIGHWAY.key] == "cycleway" ||
                    tags.any { (key, value) -> OsmTag.RIGHT_BICYCLE.key in key && value == "designated" } &&
                    tags.none { (key, _) -> key == OsmTag.CYCLEWAY_RIGHT_LANE.key } ||
                    tags[OsmTag.CYCLEWAY.key] in listOf("track", "sidepath", "crossing") ||
                    tags[OsmTag.CYCLEWAY_RIGHT.key] in listOf("track", "sidepath", "crossing") ||
                    tags[OsmTag.CYCLEWAY_BOTH.key] in listOf("track", "sidepath", "crossing") ||
                    tags.any { (key, value) -> OsmTag.RIGHT_TRAFFIC_SIGN.key in key && value == "237" }
        }

        private val isBikePathLeft: (Map<String, String>) -> Boolean = { tags ->
            @Suppress("SpellCheckingInspection")
            tags[OsmTag.HIGHWAY.key] == "cycleway" ||
                    tags.any { (key, value) -> OsmTag.LEFT_BICYCLE.key in key && value == "designated" } &&
                    tags.none { (key, _) -> key == OsmTag.CYCLEWAY_LEFT_LANE.key } ||
                    tags[OsmTag.CYCLEWAY.key] in listOf("track", "sidepath", "crossing") ||
                    tags[OsmTag.CYCLEWAY_LEFT.key] in listOf("track", "sidepath", "crossing") ||
                    tags[OsmTag.CYCLEWAY_BOTH.key] in listOf("track", "sidepath", "crossing") ||
                    tags.any { (key, value) -> OsmTag.LEFT_TRAFFIC_SIGN.key in key && value == "237" }
        }

        val isPedestrianRight: (Map<String, String>) -> Boolean = { tags ->
            (isFootpath(tags) && !canBike(tags) && !isIndoor(tags)) ||
                    (isPath(tags) && canWalkRight(tags) && !canBike(tags) && !isIndoor(tags))
        }

        val isPedestrianLeft: (Map<String, String>) -> Boolean = { tags ->
            (isFootpath(tags) && !canBike(tags) && !isIndoor(tags)) ||
                    (isPath(tags) && canWalkLeft(tags) && !canBike(tags) && !isIndoor(tags))
        }

        val isCycleHighway: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.CYCLE_HIGHWAY.key] == "yes"
        }

        val isBikeRoad: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.BICYCLE_ROAD.key] == "yes" || tags[OsmTag.CYCLESTREET.key] == "yes"
        }

        val isBikeLaneRight: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.CYCLEWAY.key] in listOf("lane", "shared_lane") ||
                    tags[OsmTag.CYCLEWAY_RIGHT.key] in listOf("lane", "shared_lane") ||
                    tags[OsmTag.CYCLEWAY_BOTH.key] in listOf("lane", "shared_lane") ||
                    tags.any { (key, value) -> OsmTag.RIGHT_LANE.key in key && value == "exclusive" }
        }

        val isBikeLaneLeft: (Map<String, String>) -> Boolean = { tags ->
            tags[OsmTag.CYCLEWAY.key] in listOf("lane", "shared_lane") ||
                    tags[OsmTag.CYCLEWAY_LEFT.key] in listOf("lane", "shared_lane") ||
                    tags[OsmTag.CYCLEWAY_BOTH.key] in listOf("lane", "shared_lane") ||
                    tags.any { (key, value) -> OsmTag.LEFT_LANE.key in key && value == "exclusive" }
        }

        val isBusLaneRight: (Map<String, String>) -> Boolean = { tags ->
            @Suppress("SpellCheckingInspection")
            tags[OsmTag.CYCLEWAY.key] == "share_busway" || tags[OsmTag.CYCLEWAY_RIGHT.key] == "share_busway"
                    || tags[OsmTag.CYCLEWAY_BOTH.key] == "share_busway"
        }

        val isBusLaneLeft: (Map<String, String>) -> Boolean = { tags ->
            @Suppress("SpellCheckingInspection")
            tags[OsmTag.CYCLEWAY.key] == "share_busway" || tags[OsmTag.CYCLEWAY_LEFT.key] == "share_busway"
                    || tags[OsmTag.CYCLEWAY_BOTH.key] == "share_busway"
        }
    }
}
