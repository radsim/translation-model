package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import java.util.function.Consumer

@Suppress("unused") // Part of the API
class ToRadSimMapper(private val tags: List<OsmTag>) {
    /**
     * Maps the `OsmTag`s to `RadSimTag`s.
     *
     * @return the `RadSimTag`s
     */
    fun map(): HashMap<String, String> {
        val original = HashMap<String, Any>()
        tags.forEach(Consumer { osmTag: OsmTag -> original[osmTag.key] = osmTag.value })
        val originalTags = HashMap<String, String>()

        // `findSurfaceType` ensures the osm tags are checked hierarchically [BIK-1086]
        when (SurfaceType.toRadSim(original)) {
            SurfaceType.COMFORT_1_ASPHALT ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_1_ASPHALT.value
            SurfaceType.COMFORT_2_COMPACTED ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_2_COMPACTED.value
            SurfaceType.COMFORT_3_COBBLESTONE ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_3_COBBLESTONE.value
            SurfaceType.COMFORT_4_GRAVEL ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_4_GRAVEL.value
        }

        val bikeInfrastructureMap = BikeInfrastructure.entries.associateWith { it.value }
        val bikeInfrastructure = BikeInfrastructure.toRadSim(original)
        val radSimTagValue = bikeInfrastructureMap[bikeInfrastructure] ?: error("Invalid value: $bikeInfrastructure")
        originalTags[BikeInfrastructure.RADSIM_TAG] = radSimTagValue

        when (Speed.toRadSim(original)) {
            Speed.MAX_SPEED_MIV_LTE_30 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_LTE_30.value
            Speed.MAX_SPEED_MIV_GT_30_LTE_50 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_GT_30_LTE_50.value
            Speed.MAX_SPEED_MIV_GT_50 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_GT_50.value
            else ->
                originalTags[Speed.RADSIM_TAG] = Speed.NO_INFORMATION.value
        }
        return originalTags
    }
}