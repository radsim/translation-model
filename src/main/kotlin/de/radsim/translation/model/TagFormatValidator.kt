package de.radsim.translation.model

/**
 * Validates tag format to ensure `.toRadSim()` functions receive OSM tags, not RadSim tags.
 *
 * The three formats are distinguished by their keys:
 * - **OSM format**: Uses OSM tag keys like "highway", "cycleway", "bicycle", "maxspeed" (lowercase), etc.
 * - **RadSim Simplified format**: Uses "roadStyleSimplified", "maxSpeed", "surface"
 * - **RadSim Full format**: Uses "roadStyle", "maxSpeed", "surface"
 */
internal object TagFormatValidator {

    /**
     * The distinguishing key for RadSim Simplified format.
     */
    private const val RADSIM_SIMPLIFIED_KEY = "roadStyleSimplified"

    /**
     * The distinguishing key for RadSim Full format.
     */
    private const val RADSIM_FULL_KEY = "roadStyle"

    /**
     * Checks if the tags are in OSM format.
     *
     * @param tags The tags to check
     * @return true if tags appear to be in OSM format (no RadSim keys detected)
     */
    fun isOsmFormat(tags: Map<String, Any>): Boolean {
        return !tags.containsKey(RADSIM_SIMPLIFIED_KEY) && !tags.containsKey(RADSIM_FULL_KEY)
    }

    /**
     * Checks if the tags are in RadSim Simplified format.
     *
     * @param tags The tags to check
     * @return true if tags contain the "roadStyleSimplified" key
     */
    fun isRadSimSimplifiedFormat(tags: Map<String, Any>): Boolean {
        return tags.containsKey(RADSIM_SIMPLIFIED_KEY)
    }

    /**
     * Checks if the tags are in RadSim Full format.
     *
     * @param tags The tags to check
     * @return true if tags contain the "roadStyle" key (without "Simplified")
     */
    fun isRadSimFullFormat(tags: Map<String, Any>): Boolean {
        return tags.containsKey(RADSIM_FULL_KEY)
    }

    /**
     * Validates that the given tags are in OSM format (not RadSim format).
     *
     * @param tags The tags to validate
     * @param callerFunction The name of the function calling this (for error messages)
     * @throws IllegalArgumentException if RadSim tags are detected
     */
    fun requireOsmFormat(tags: Map<String, Any>, callerFunction: String) {
        when {
            isRadSimSimplifiedFormat(tags) -> {
                throw IllegalArgumentException(
                    "$callerFunction expects OSM tags but received RadSim Simplified tags! " +
                        "Found key '$RADSIM_SIMPLIFIED_KEY' in: ${tags.keys}. " +
                        "This indicates Way.properties.attributes (RadSim tags) are being passed instead of " +
                        "WayAttributes.wayTags (OSM tags). " +
                        "Check PlanChangesetMerger.mergeImmutable() or Simulator.annotateTagsAndRelationIds()."
                )
            }
            isRadSimFullFormat(tags) -> {
                throw IllegalArgumentException(
                    "$callerFunction expects OSM tags but received RadSim Full tags! " +
                        "Found key '$RADSIM_FULL_KEY' in: ${tags.keys}. " +
                        "This indicates Way.properties.attributes (RadSim tags) are being passed instead of " +
                        "WayAttributes.wayTags (OSM tags). " +
                        "Check PlanChangesetMerger.mergeImmutable() or Simulator.annotateTagsAndRelationIds()."
                )
            }
        }
    }

    /**
     * Gets the detected tag format as a string for debugging.
     *
     * @param tags The tags to check
     * @return "OSM", "RadSim Simplified", or "RadSim Full"
     */
    fun getTagFormat(tags: Map<String, Any>): String {
        return when {
            isRadSimSimplifiedFormat(tags) -> "RadSim Simplified"
            isRadSimFullFormat(tags) -> "RadSim Full"
            else -> "OSM"
        }
    }
}
