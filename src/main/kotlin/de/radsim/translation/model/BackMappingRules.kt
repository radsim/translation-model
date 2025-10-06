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
        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },

        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BICYCLE_ROAD) to { tags ->
            val highway = tags["highway"] ?: "residential"
            setOf(OsmTag("highway", highway), OsmTag("bicycle_road", "yes"))
        },

        RuleKey(SimplifiedBikeInfrastructure.NO, SimplifiedBikeInfrastructure.BICYCLE_WAY) to { _ ->
            setOf(OsmTag("highway", "cycleway"), OsmTag("segregated", "yes"))
        },

        // -------------------------------------------------------------------
        // R4–R8: BICYCLE_ROAD → other categories
        // -------------------------------------------------------------------
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
            val highway = tags["highway"] ?: "footway"
            setOf(OsmTag("highway", highway), OsmTag("bicycle", "yes"), OsmTag("segregated", "no"))
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
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { tags ->
            val highway = tags["highway"] ?: "secondary"
            val base = if (highway == "cycleway") {
                // FIXME: < suggestion to remove conflicting highway=cycleway
                setOf(OsmTag("highway", /*highway*/"secondary"))
            } else {
                emptySet()
            }
            base + OsmTag("cycleway", "lane")
        },

        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_WAY, SimplifiedBikeInfrastructure.BUS_LANE) to { tags ->
            val highway = tags["highway"] ?: "secondary"
            val base = if (highway == "cycleway") setOf(OsmTag("highway", highway)) else emptySet()
            base + OsmTag("cycleway", "share_busway")
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
        // R13–R16: BICYCLE_LANE → others
        // -------------------------------------------------------------------
        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.BUS_LANE) to { _ ->
            setOf(OsmTag("cycleway", "share_busway"))
        },

        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.MIXED_WAY) to { _ ->
            setOf(
                OsmTag("highway", "footway"),
                OsmTag("bicycle", "yes"),
                OsmTag("segregated", "no"),
                // remove the lane marker
                OsmTag("cycleway", "") // FIXME < suggestion to fix when from tags contain cycleway=lane
            )
        },

        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },

        RuleKey(SimplifiedBikeInfrastructure.BICYCLE_LANE, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
        },

        // -------------------------------------------------------------------
        // R17–R19: BUS_LANE → others
        // -------------------------------------------------------------------
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
        // R20–R22: MIXED_WAY → others
        // -------------------------------------------------------------------
        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.NO) to { _ ->
            emptySet()
        },

        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.BICYCLE_LANE) to { tags ->
            val highway = tags["highway"] ?: "secondary"
            setOf(OsmTag("highway", highway), OsmTag("cycleway", "lane"))
        },

        RuleKey(SimplifiedBikeInfrastructure.MIXED_WAY, SimplifiedBikeInfrastructure.CYCLE_HIGHWAY) to { _ ->
            setOf(OsmTag("cycle_highway", "yes"))
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