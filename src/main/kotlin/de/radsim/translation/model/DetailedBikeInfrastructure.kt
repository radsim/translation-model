package de.radsim.translation.model

/**
 * An enum with the infrastructure types in the "detailed" format.
 *
 * "Detailed" represents the way OpenStreetMap (OSM) annotated bicycle infrastructure:
 * - Bicycle infrastructure and usage is tagged to the road's OSM ways and geometry.
 * - e.g. a road with a bicycle lane on the right
 *
 * The [DetailedBikeInfrastructure] includes can be used to map from OSM to Radsim tags.
 * For back-mapping (Radsim to OSM) we currently use a [SimplifiedBikeInfrastructure].
 *
 * Notes:
 * - The mapping assumes cyclists only use the infrastructure as allowed (German "STVO").
 *
 * @property value The value for the RadSim infrastructure type tag.
 * @property backMappingType The simplified infrastructure type that this detail maps to.
 */
enum class DetailedBikeInfrastructure(val value: String, val backMappingType: SimplifiedBikeInfrastructure) {
    // FIXME: check the back-mapping (generated / guessed)
    // Ask this in a Telco but maybe based on test cases, so the use-cases are clearer
    // ! Maybe we do not even need a mapping from Detailed to Simplified !

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
    MIT_ROAD_BOTH("MitRoadBoth", SimplifiedBikeInfrastructure.MIT_ROAD),
    MIT_ROAD_RIGHT_PEDESTRIAN_LEFT("MitRoadRightPedestrianLeft", SimplifiedBikeInfrastructure.MIT_ROAD),
    MIT_ROAD_RIGHT_NO_LEFT("MitRoadRightNoLeft", SimplifiedBikeInfrastructure.MIT_ROAD),

    MIT_ROAD_LEFT_PEDESTRIAN_RIGHT("MitRoadLeftPedestrianRight", SimplifiedBikeInfrastructure.NO),
    MIT_ROAD_LEFT_NO_RIGHT("MitRoadLeftNoRight", SimplifiedBikeInfrastructure.NO),

    // Pedestrian paths
    PEDESTRIAN_BOTH("PedestrianBoth", SimplifiedBikeInfrastructure.NO),
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
         * The specific OSM tags that are used to determine the RadSim infrastructure type.
         *
         * "cycleway" is the default key used by BRouter (probably, check again if unsure)
         * "cycleway[:l/r/b]": used e.g. by the profile "fastbike-verylowtraffic"
         *
         * FIXME: See if we need this in BikeInfrastructure or DetailedBikeInfrastructure
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
        fun toRadSim(tags: Map<String, String>): DetailedBikeInfrastructure {
            // Map to complex categories, separating right and left as required for OSM to Radsim mapping
            if (tags.containsValue("access") && isNotAccessible(tags) || tags["tram"] == "yes") {
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

            if (isPedestrianRight(tags) && tags["indoor"] != "yes") {
                return if (isPedestrianLeft(tags) && tags["indoor"] != "yes") {
                    if (tags["access"] == "customers") NO
                    else PEDESTRIAN_BOTH
                } else {
                    PEDESTRIAN_RIGHT_NO_LEFT
                }
            }

            if (isPedestrianLeft(tags) && tags["indoor"] != "yes") {
                return if (tags["access"] == "customers") NO
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

        // `mit_road` - cars are allowed, bikes also, but no dedicated infrastructure
        // Unclear what "mit" stands for, MIV or German "with"? FIXME

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

        private val isSegregated: (Map<String, String>) -> Boolean = { x ->
            x.any { (key, value) -> "segregated" in key && value == "yes" }
        }

        private val isFootpath: (Map<String, String>) -> Boolean = { x ->
            x["highway"] in listOf("footway", "pedestrian")
        }

        val isNotAccessible: (Map<String, String>) -> Boolean = { x ->
            x["access"] == "no"
        }

        private val useSidePath: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x.any { (key, value) -> "bicycle" in key && value == "use_sidepath" }
        }

        private val isIndoor: (Map<String, String>) -> Boolean = { x ->
            x["indoor"] == "yes"
        }

        private val isPath: (Map<String, String>) -> Boolean = { x ->
            x["highway"] == "path"
        }

        private val isTrack: (Map<String, String>) -> Boolean = { x ->
            x["highway"] == "track"
        }

        private val canWalkRight: (Map<String, String>) -> Boolean = { x ->
            x["foot"] in listOf("yes", "designated") ||
                    x.any { (key, value) -> "right:foot" in key && value in listOf("yes", "designated") } ||
                    x["sidewalk"] in listOf("yes", "separated", "both", "right", "left") ||
                    x["sidewalk:right"] in listOf("yes", "separated", "both", "right") ||
                    x["sidewalk:both"] in listOf("yes", "separated", "both")
        }

        private val canWalkLeft: (Map<String, String>) -> Boolean = { x ->
            x["foot"] in listOf("yes", "designated") ||
                    x.any { (key, value) -> "left:foot" in key && value in listOf("yes", "designated") } ||
                    x["sidewalk"] in listOf("yes", "separated", "both", "right", "left") ||
                    x["sidewalk:left"] in listOf("yes", "separated", "both", "left") ||
                    x["sidewalk:both"] in listOf("yes", "separated", "both")
        }

        private val canBike: (Map<String, String>) -> Boolean = { x ->
            x["bicycle"] in listOf("yes", "designated") &&
                    x["highway"] !in listOf("motorway", "motorway_link")
        }

        private val cannotBike: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["bicycle"] in listOf("no", "dismount", "use_sidepath") ||
                    x["highway"] in listOf("corridor", "motorway", "motorway_link", "trunk", "trunk_link") ||
                    x["access"] == "customers"
        }

        private val isObligatedSegregated: (Map<String, String>) -> Boolean = { x ->
            ("traffic_sign" in x.keys && "241" in x["traffic_sign"]!!) ||
                    ("traffic_sign:forward" in x.keys && "241" in x["traffic_sign:forward"]!!)
        }

        private val isDesignated: (Map<String, String>) -> Boolean = { x ->
            x["bicycle"] == "designated"
        }

        private val isBicycleDesignatedLeft: (Map<String, String>) -> Boolean = { x ->
            isDesignated(x) || x["cycleway:left:bicycle"] == "designated" || x["cycleway:bicycle"] == "designated"
        }

        private val isBicycleDesignatedRight: (Map<String, String>) -> Boolean = { x ->
            isDesignated(x) || x["cycleway:right:bicycle"] == "designated" || x["cycleway:bicycle"] == "designated"
        }

        private val isPedestrianDesignatedLeft: (Map<String, String>) -> Boolean = { x ->
            x["foot"] == "designated" || x["sidewalk:left:foot"] == "designated" || x["sidewalk:foot"] == "designated"
        }

        private val isPedestrianDesignatedRight: (Map<String, String>) -> Boolean = { x ->
            x["foot"] == "designated" || x["sidewalk:right:foot"] == "designated" || x["sidewalk:foot"] == "designated"
        }

        private val isServiceTag: (Map<String, String>) -> Boolean = { x ->
            x["highway"] == "service"
        }

        private val isAgricultural: (Map<String, String>) -> Boolean = { x ->
            x["motor_vehicle"] in listOf("agricultural", "forestry")
        }

        private val isAccessible: (Map<String, String>) -> Boolean = { x ->
            x["access"].isNullOrEmpty() || !isNotAccessible(x)
        }

        private val isSmooth: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["tracktype"].isNullOrEmpty() || x["tracktype"] in listOf("grade1", "grade2")
        }

        private val isVehicleAllowed: (Map<String, String>) -> Boolean = { x ->
            x["motor_vehicle"].isNullOrEmpty() || x["motor_vehicle"] != "no"
        }

        val isService: (Map<String, String>) -> Boolean = { x ->
            (isServiceTag(x) ||
                    (isAgricultural(x) && isAccessible(x)) ||
                    (isPath(x) && isAccessible(x)) ||
                    (isTrack(x) && isAccessible(x) && isSmooth(x) && isVehicleAllowed(x))) &&
                    !isDesignated(x)
        }

        private val canCarDrive: (Map<String, String>) -> Boolean = { x ->
            x["highway"] in listOf("motorway", "trunk", "primary", "secondary", "tertiary",
                "unclassified", "road", "residential", "living_street",
                "primary_link", "secondary_link", "tertiary_link",
                "motorway_link", "trunk_link")
        }

        val isPathNotForbidden: (Map<String, String>) -> Boolean = { x ->
            x["highway"] in listOf("cycleway", "track", "path") && !cannotBike(x)
        }

        private val isBikePathRight: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["highway"] == "cycleway" ||
                    x.any { (key, value) -> "right:bicycle" in key && value == "designated" } &&
                    x.none { (key, _) -> key == "cycleway:right:lane" } ||
                    x["cycleway"] in listOf("track", "sidepath", "crossing") ||
                    x["cycleway:right"] in listOf("track", "sidepath", "crossing") ||
                    x["cycleway:both"] in listOf("track", "sidepath", "crossing") ||
                    x.any { (key, value) -> "right:traffic_sign" in key && value == "237" }
        }

        private val isBikePathLeft: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["highway"] == "cycleway" ||
                    x.any { (key, value) -> "left:bicycle" in key && value == "designated" } &&
                    x.none { (key, _) -> key == "cycleway:left:lane" } ||
                    x["cycleway"] in listOf("track", "sidepath", "crossing") ||
                    x["cycleway:left"] in listOf("track", "sidepath", "crossing") ||
                    x["cycleway:both"] in listOf("track", "sidepath", "crossing") ||
                    x.any { (key, value) -> "left:traffic_sign" in key && value == "237" }
        }

        val isPedestrianRight: (Map<String, String>) -> Boolean = { x ->
            (isFootpath(x) && !canBike(x) && !isIndoor(x)) ||
                    (isPath(x) && canWalkRight(x) && !canBike(x) && !isIndoor(x))
        }

        val isPedestrianLeft: (Map<String, String>) -> Boolean = { x ->
            (isFootpath(x) && !canBike(x) && !isIndoor(x)) ||
                    (isPath(x) && canWalkLeft(x) && !canBike(x) && !isIndoor(x))
        }

        val isCycleHighway: (Map<String, String>) -> Boolean = { x ->
            x["cycle_highway"] == "yes"
        }

        val isBikeRoad: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["bicycle_road"] == "yes" || x["cyclestreet"] == "yes"
        }

        val isBikeLaneRight: (Map<String, String>) -> Boolean = { x ->
            x["cycleway"] in listOf("lane", "shared_lane") ||
                    x["cycleway:right"] in listOf("lane", "shared_lane") ||
                    x["cycleway:both"] in listOf("lane", "shared_lane") ||
                    x.any { (key, value) -> "right:lane" in key && value == "exclusive" }
        }

        val isBikeLaneLeft: (Map<String, String>) -> Boolean = { x ->
            x["cycleway"] in listOf("lane", "shared_lane") ||
                    x["cycleway:left"] in listOf("lane", "shared_lane") ||
                    x["cycleway:both"] in listOf("lane", "shared_lane") ||
                    x.any { (key, value) -> "left:lane" in key && value == "exclusive" }
        }

        val isBusLaneRight: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["cycleway"] == "share_busway" || x["cycleway:right"] == "share_busway"
                    || x["cycleway:both"] == "share_busway"
        }

        val isBusLaneLeft: (Map<String, String>) -> Boolean = { x ->
            @Suppress("SpellCheckingInspection")
            x["cycleway"] == "share_busway" || x["cycleway:left"] == "share_busway"
                    || x["cycleway:both"] == "share_busway"
        }
    }
}
