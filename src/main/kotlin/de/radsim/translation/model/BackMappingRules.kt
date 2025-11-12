package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import de.radsim.translation.model.SimplifiedBikeInfrastructure.BICYCLE_LANE
import de.radsim.translation.model.SimplifiedBikeInfrastructure.BICYCLE_ROAD
import de.radsim.translation.model.SimplifiedBikeInfrastructure.BICYCLE_WAY
import de.radsim.translation.model.SimplifiedBikeInfrastructure.BUS_LANE
import de.radsim.translation.model.SimplifiedBikeInfrastructure.CYCLE_HIGHWAY
import de.radsim.translation.model.SimplifiedBikeInfrastructure.MIXED_WAY
import de.radsim.translation.model.SimplifiedBikeInfrastructure.NO

/**
 * Rule-based back-mapping dispatcher implementing R1–R22.
 *
 * Rules follow the official matrix from 2025-08-21 [BIK-1440].
 *
 * Each rule transforms the current OSM tag set to the required delta
 * to morph from one SimplifiedBikeInfrastructure category to another.
 */
object BackMappingRules {

    data class RuleKey(
        val from: SimplifiedBikeInfrastructure,
        val to: SimplifiedBikeInfrastructure
    )

    private val rules: Map<RuleKey, (Map<String, Any>) -> Set<OsmTag>> = mapOf(

        // -------------------------------------------------------------------
        // R1–R3: NO → category
        // -------------------------------------------------------------------
        // R1
        RuleKey(NO, BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        RuleKey(NO, CYCLE_HIGHWAY) to { tags ->
            // Added, even though this is not a way tags (cycle_highway)
            val highway = tags["highway"] as? String
            val maybeHighwayChange = if (highway == "service") {
                setOf(OsmTag("highway", "cycleway"))
            } else {
                emptySet()
            }
            maybeHighwayChange + OsmTag("cycle_highway", "yes")
        },
        // R19
        RuleKey(NO, BICYCLE_WAY) to { _ ->
            setOf(OsmTag("bicycle", "designated"), OsmTag("foot", "designated"), OsmTag("segregated", "yes"))
        },
        // R20
        RuleKey(NO, BICYCLE_LANE) to { _ ->
            setOf(OsmTag("cycleway", "lane"))
        },
        // R21
        RuleKey(NO, BUS_LANE) to { _ ->
            setOf(OsmTag("cycleway", "share_busway"))
        },
        // CHANGED: R22 must include highway=path for classification
        RuleKey(NO, MIXED_WAY) to { _ ->
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "designated"),
                OsmTag("foot", "designated"),
                OsmTag("segregated", "no")
            )
        },

        // -------------------------------------------------------------------
        // R4–R8: BICYCLE_ROAD → other categories
        // -------------------------------------------------------------------
        // R2 + R3
        RuleKey(BICYCLE_ROAD, BICYCLE_WAY) to { _ ->
            setOf(
                OsmTag("bicycle_road", ""), // R2: remove
                OsmTag("highway", "cycleway"), // R3
                OsmTag("segregated", "yes") // R3
            )
        },
        // R2 + R4
        RuleKey(BICYCLE_ROAD, BICYCLE_LANE) to { tags ->
            val cleaned = setOf(OsmTag("bicycle_road", "")) // R2: remove
            val highway = tags["highway"] as? String
            val maybeReset = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            cleaned + maybeReset + OsmTag("cycleway", "lane") // R4
        },
        // R2 + R5
        RuleKey(BICYCLE_ROAD, BUS_LANE) to { tags ->
            val cleaned = setOf(OsmTag("bicycle_road", "")) // R2: remove
            val highway = tags["highway"] as? String
            val maybeReset = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            cleaned + maybeReset + OsmTag("cycleway", "share_busway") // R5
        },
        // R2 + R6
        RuleKey(BICYCLE_ROAD, MIXED_WAY) to { _ ->
            setOf(
                OsmTag("bicycle_road", ""), // R2: remove
                OsmTag("highway", "cycleway"), // R6
                OsmTag("foot", "yes"), // R6
                OsmTag("segregated", "no") // R6
            )
        },
        // R2 + R7
        RuleKey(BICYCLE_ROAD, NO) to { _ ->
            setOf(
                OsmTag("bicycle_road", ""), // R2: remove
                OsmTag("highway", "path"), // R7
                OsmTag("bicycle", "") // R7
            )
        },
        RuleKey(BICYCLE_ROAD, CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes")) // Added, even though this is not a way tags
        },

        // -------------------------------------------------------------------
        // R9–R12: BICYCLE_WAY → others
        // -------------------------------------------------------------------
        // R1 for BICYCLE_WAY → BICYCLE_ROAD
        RuleKey(BICYCLE_WAY, BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        RuleKey(BICYCLE_WAY, BICYCLE_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane"))
        },
        RuleKey(BICYCLE_WAY, BUS_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "share_busway"))
        },
        RuleKey(BICYCLE_WAY, MIXED_WAY) to { tags ->
            when (tags["highway"]) {
                "cycleway" -> setOf(OsmTag("foot", "yes"), OsmTag("segregated", "no"))
                "track", "path" -> setOf(OsmTag("bicycle", "yes"), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
                "footway" -> setOf(OsmTag("bicycle", "yes"), OsmTag("segregated", "no"))
                else -> setOf(OsmTag("cycleway", "track"), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
            }
        },
        RuleKey(BICYCLE_WAY, NO) to { _ ->
            setOf(OsmTag("highway", "path"), OsmTag("bicycle", "")) // R7
        },
        RuleKey(BICYCLE_WAY, CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes")) // Added, even though this is not a way tags
        },

        // -------------------------------------------------------------------
        // R11–R13: BICYCLE_LANE → others
        // -------------------------------------------------------------------
        // R1 for BICYCLE_LANE → BICYCLE_ROAD
        RuleKey(BICYCLE_LANE, BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R11
        RuleKey(BICYCLE_LANE, BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },
        RuleKey(BICYCLE_LANE, BUS_LANE) to { _ ->
            setOf(OsmTag("cycleway", "share_busway"))
        },
        RuleKey(BICYCLE_LANE, MIXED_WAY) to { _ ->
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "yes"),
                OsmTag("segregated", "no"),
                OsmTag("cycleway", "") // remove lane marker
            )
        },
        RuleKey(BICYCLE_LANE, NO) to { _ ->
            setOf(OsmTag("highway", "path"), OsmTag("bicycle", "")) // R7
        },
        RuleKey(BICYCLE_LANE, CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes")) // Added, even though this is not a way tags
        },

        // -------------------------------------------------------------------
        // R14–R18: BUS_LANE → others
        // -------------------------------------------------------------------
        // R1 for BUS_LANE → BICYCLE_ROAD
        RuleKey(BUS_LANE, BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R14
        RuleKey(BUS_LANE, BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },
        // R15
        RuleKey(BUS_LANE, BICYCLE_LANE) to { _ ->
            setOf(OsmTag("cycleway", "lane"))
        },
        // R13
        RuleKey(BUS_LANE, MIXED_WAY) to { _ ->
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "yes"),
                OsmTag("segregated", "no"),
                OsmTag("cycleway", ""), // Added: remove bus-lane marker!
            ) // R13
        },
        RuleKey(BUS_LANE, NO) to { _ ->
            setOf(OsmTag("highway", "path"), OsmTag("bicycle", "")) // R7
        },
        RuleKey(BUS_LANE, CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes")) // Added, even though this is not a way tags
        },

        // -------------------------------------------------------------------
        // R16–R22: MIXED_WAY → others
        // -------------------------------------------------------------------
        // R1 for MIXED_WAY → BICYCLE_ROAD
        RuleKey(MIXED_WAY, BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R16
        RuleKey(MIXED_WAY, BICYCLE_WAY) to { tags ->
            val highway = tags["highway"] as? String
            when (highway) {
                "cycleway" -> setOf(OsmTag("foot", "no"))
                "track", "path", "footway" -> setOf(OsmTag("bicycle", "designated"), OsmTag("segregated", "yes"))
                else -> setOf(OsmTag("cycleway", "track"), OsmTag("segregated", "yes"))
            }
        },
        RuleKey(MIXED_WAY, BICYCLE_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane"))
        },
        RuleKey(MIXED_WAY, BUS_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "share_busway"))
        },
        RuleKey(MIXED_WAY, NO) to { _ ->
            setOf(OsmTag("highway", "path"), OsmTag("bicycle", "")) // R7
        },
        RuleKey(MIXED_WAY, CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes")) // Added, even though this is not a way tags
        },

        // -------------------------------------------------------------------
        // CYCLE_HIGHWAY → others (R2–R7)
        // -------------------------------------------------------------------
        RuleKey(CYCLE_HIGHWAY, BICYCLE_ROAD) to { tags ->
            val highway = tags["highway"] ?: "residential"
            setOf(
                OsmTag("highway", highway),
                OsmTag("bicycle_road", "yes"),
                OsmTag("cycle_highway", ""), // Added, even though this is not a way tags
            )
        },

        RuleKey(CYCLE_HIGHWAY, BICYCLE_WAY) to { _ ->
            // R3
            setOf(
                OsmTag("highway", "cycleway"),
                OsmTag("segregated", "yes"),
                OsmTag("cycle_highway", ""), // Added, even though this is not a way tags
            )
        },

        RuleKey(CYCLE_HIGHWAY, BICYCLE_LANE) to { tags ->
            // R4
            val highway = tags["highway"] ?: "secondary"
            val cleaned = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            // Added, even though this is not a way tags (cycle_highway)
            cleaned + OsmTag("cycleway", "lane") + OsmTag("cycle_highway", "")
        },

        RuleKey(CYCLE_HIGHWAY, BUS_LANE) to { tags ->
            // R5
            val highway = tags["highway"] ?: "secondary"
            val cleaned = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            // Added, even though this is not a way tags (cycle_highway)
            cleaned + OsmTag("cycleway", "share_busway") + OsmTag("cycle_highway", "")
        },

        RuleKey(CYCLE_HIGHWAY, MIXED_WAY) to { _ ->
            // R6
            setOf(
                OsmTag("highway", "cycleway"),
                OsmTag("foot", "yes"),
                OsmTag("segregated", "no"),
                OsmTag("cycle_highway", ""), // Added, even though this is not a way tags
            )
        },

        RuleKey(CYCLE_HIGHWAY, NO) to { _ ->
            // R7
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", ""),
                OsmTag("cycle_highway", ""), // Added, even though this is not a way tags
            )
        },
    )

    fun applyRule(
        from: SimplifiedBikeInfrastructure,
        to: SimplifiedBikeInfrastructure,
        current: Map<String, Any>
    ): Set<OsmTag> {
        val rule = rules[RuleKey(from, to)]
        return rule?.invoke(current)
            ?: error("No back-mapping rule defined for ${from.name} → ${to.name}")
    }
}
