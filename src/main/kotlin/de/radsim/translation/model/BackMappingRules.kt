package de.radsim.translation.model

import de.cyface.model.osm.OsmTag

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
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },

        // R2+R4 (NO → BICYCLE_LANE)
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane"))
        },

        // R2+R5 (NO → BUS_LANE)
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BUS_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "share_busway"))
        },

        // R2+R6 (NO → MIXED_WAY)
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.MIXED_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
        },

        // -------------------------------------------------------------------
        // R4–R8: BICYCLE_ROAD → other categories
        // -------------------------------------------------------------------
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { _ ->
            setOf(OsmTag("bicycle_road", "")) // R2
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { tags ->
            val highway = tags["highway"] ?: "secondary"
            val cleaned = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            cleaned + OsmTag("cycleway", "lane")
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.BUS_LANE) to { tags ->
            val highway = tags["highway"] ?: "secondary"
            val cleaned = if (highway == "cycleway") setOf(OsmTag("highway", "secondary")) else emptySet()
            cleaned + OsmTag("cycleway", "share_busway")
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.MIXED_WAY) to { tags ->
            val highway = tags["highway"] ?: "cycleway"
            setOf(OsmTag("highway", highway), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_ROAD, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },

        // -------------------------------------------------------------------
        // R9–R12: BICYCLE_WAY → others
        // -------------------------------------------------------------------
        // R1 for BICYCLE_WAY → BICYCLE_ROAD
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.BUS_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "share_busway"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.MIXED_WAY) to { tags ->
            when (tags["highway"]) {
                "cycleway" -> setOf(OsmTag("foot", "yes"), OsmTag("segregated", "no"))
                "track", "path" -> setOf(OsmTag("bicycle", "yes"), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
                "footway" -> setOf(OsmTag("bicycle", "yes"), OsmTag("segregated", "no"))
                else -> setOf(OsmTag("cycleway", "track"), OsmTag("foot", "yes"), OsmTag("segregated", "no"))
            }
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },

        // -------------------------------------------------------------------
        // R11–R13: BICYCLE_LANE → others
        // -------------------------------------------------------------------
        // R1 for BICYCLE_LANE → BICYCLE_ROAD
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R11
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.BUS_LANE) to { _ ->
            setOf(OsmTag("cycleway", "share_busway"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.MIXED_WAY) to { _ ->
            setOf(
                OsmTag("highway", "path"),
                OsmTag("bicycle", "yes"),
                OsmTag("segregated", "no"),
                OsmTag("cycleway", "") // remove lane marker
            )
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },

        // -------------------------------------------------------------------
        // R14–R18: BUS_LANE → others
        // -------------------------------------------------------------------
        // R1 for BUS_LANE → BICYCLE_ROAD
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R14
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },
        // R15
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { _ ->
            setOf(OsmTag("cycleway", "lane"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.MIXED_WAY) to { _ ->
            setOf(OsmTag("highway", "footway"), OsmTag("bicycle", "yes"), OsmTag("segregated", "no"))
        },
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },
        RuleKey(SimplifiedBikeInfrastructure.BUS_LANE, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },

        // -------------------------------------------------------------------
        // R16–R22: MIXED_WAY → others
        // -------------------------------------------------------------------
        // R1 for MIXED_WAY → BICYCLE_ROAD
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { _ ->
            setOf(OsmTag("highway", "residential"), OsmTag("bicycle_road", "yes"))
        },
        // R16
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { tags ->
            val highway = tags["highway"] as? String
            when (highway) {
                "cycleway" -> setOf(OsmTag("foot", "no"))
                "track", "path", "footway" -> setOf(OsmTag("bicycle", "designated"), OsmTag("segregated", "yes"))
                else -> setOf(OsmTag("cycleway", "track"), OsmTag("segregated", "yes"))
            }
        },
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "lane"))
        },
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.BUS_LANE) to { _ ->
            setOf(OsmTag("highway", "secondary"), OsmTag("cycleway", "share_busway"))
        },
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("bicycle", "designated"), OsmTag("foot", "designated"), OsmTag("segregated", "no"))
        }
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