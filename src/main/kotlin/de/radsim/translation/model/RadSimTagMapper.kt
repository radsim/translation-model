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
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector

/**
 * Mapped for conversion between RadSim tags and appropriate Open Street Map representations.
 *
 * @property tags The source tags, ether RadSim or OSM tags.
 */
@Suppress("unused") // Part of the API
class RadSimTagMapper(private val tags: List<OsmTag>) {

    /**
     * Convert RadSim Tags back to an appropriate Open Street Map representation, which is required by the Simulation.
     *
     * @return The converted list of Open Street Map tags
     */
    fun toOsm(): List<OsmTag> {
        return tags.stream().collect(object : Collector<OsmTag, MutableList<OsmTag>, List<OsmTag>> {
            override fun supplier(): Supplier<MutableList<OsmTag>> {
                return Supplier<MutableList<OsmTag>> { ArrayList() }
            }

            @SuppressWarnings("LongMethod") // TODO
            override fun accumulator(): BiConsumer<MutableList<OsmTag>, OsmTag> {
                return BiConsumer<MutableList<OsmTag>, OsmTag> { osmTags: MutableList<OsmTag>, radsSimTag: OsmTag ->
                    val key = radsSimTag.key
                    val value = radsSimTag.value
                    when (key) {
                        // For an overview see https://wiki.openstreetmap.org/wiki/Bicycle
                        BikeInfrastructure.RADSIM_TAG ->
                            when (value) {
                                BikeInfrastructure.TRACK.value -> {
                                    // See https://wiki.openstreetmap.org/wiki/DE:Tag:cycleway%3Dtrack
                                    osmTags.add(BikeInfrastructure.TRACK.backMappingTag!!)
                                }

                                BikeInfrastructure.LANE.value -> {
                                    // See https://wiki.openstreetmap.org/wiki/DE:Tag:cycleway%3Dlane
                                    osmTags.add(BikeInfrastructure.LANE.backMappingTag!!)
                                }

                                BikeInfrastructure.MIXED.value -> {
                                    // explicitly overwrite bicycle permission, often implicated by highway-type
                                    osmTags.add(BikeInfrastructure.MIXED.backMappingTag!!)
                                }

                                else -> {
                                    require(value == BikeInfrastructure.NO_INFORMATION.value) {
                                        "Unknown RadSim tag: $radsSimTag"
                                    }
                                }
                            }

                        // To check the penalty during routing, see:
                        // https://github.com/abrensch/brouter/blob/372a04a6cf4608cf14bc7045aed9499012a23f52/misc/profiles2/fastbike-verylowtraffic.brf#L136
                        SurfaceType.RADSIM_TAG ->
                            when (value) {
                                SurfaceType.COMFORT_1_ASPHALT.value -> {
                                    osmTags.add(SurfaceType.COMFORT_1_ASPHALT.backMappingTag)
                                }

                                SurfaceType.COMFORT_2_COMPACTED.value -> {
                                    osmTags.add(SurfaceType.COMFORT_2_COMPACTED.backMappingTag)
                                }

                                SurfaceType.COMFORT_3_COBBLESTONE.value -> {
                                    osmTags.add(SurfaceType.COMFORT_3_COBBLESTONE.backMappingTag)
                                }

                                SurfaceType.COMFORT_4_GRAVEL.value -> {
                                    osmTags.add(SurfaceType.COMFORT_4_GRAVEL.backMappingTag)
                                }

                                else -> throw IllegalArgumentException("Unknown RadSim tag: $radsSimTag")
                            }

                        // To check the penalty during routing, see:
                        // https://github.com/abrensch/brouter/blob/372a04a6cf4608cf14bc7045aed9499012a23f52/misc/profiles2/fastbike-verylowtraffic.brf#L197
                        SurfaceQuality.RADSIM_TAG ->
                            when (value) {
                                SurfaceQuality.GOOD.value -> {
                                    osmTags.add(SurfaceQuality.GOOD.backMappingTag!!)
                                }

                                SurfaceQuality.MEDIUM.value -> {
                                    osmTags.add(SurfaceQuality.MEDIUM.backMappingTag!!)
                                }

                                SurfaceQuality.BAD.value -> {
                                    // To cover the whole range. "Bad" originate ~ 50:50 from "bad", "very_bad"
                                    osmTags.add(SurfaceQuality.BAD.backMappingTag!!)
                                }

                                else -> {
                                    require(SurfaceQuality.NO_INFORMATION.value == value) {
                                        "Unknown RadSim tag: $radsSimTag"
                                    }
                                }
                            }

                        Speed.RADSIM_TAG -> when (value) {
                            Speed.MAX_SPEED_MIV_LTE_30.value -> {
                                osmTags.add(Speed.MAX_SPEED_MIV_LTE_30.backMappingTag!!)
                            }

                            Speed.MAX_SPEED_MIV_GT_30_LTE_50.value -> {
                                osmTags.add(Speed.MAX_SPEED_MIV_GT_30_LTE_50.backMappingTag!!)
                            }

                            Speed.MAX_SPEED_MIV_GT_50.value -> {
                                osmTags.add(Speed.MAX_SPEED_MIV_GT_50.backMappingTag!!)
                            }

                            else -> {
                                require(Speed.NO_INFORMATION.value == value) {
                                    "Unknown RadSim tag: $radsSimTag"
                                }
                            }
                        }

                        else -> throw IllegalArgumentException("Unknown RadSim tag key: $key")
                    }
                }
            }

            override fun combiner(): BinaryOperator<MutableList<OsmTag>> {
                return BinaryOperator<MutableList<OsmTag>> { osmTags: MutableList<OsmTag>, osmTags2: List<OsmTag> ->
                    osmTags.addAll(osmTags2)
                    osmTags
                }
            }

            override fun finisher(): Function<MutableList<OsmTag>, List<OsmTag>?> {
                return Function<MutableList<OsmTag>, List<OsmTag>?> { osmTags: List<OsmTag>? -> osmTags }
            }

            override fun characteristics(): Set<Collector.Characteristics> {
                return emptySet()
            }
        })
    }

    /**
     * Maps the `OsmTag`s to `RadSimTag`s.
     *
     * @param defaultInfrastructure If `true` the default `BikeInfrastructure.MIXED` tags is used when the original tags
     * contain no information about the infrastructure as expected by the (deprecated) `Application` `translation` mode.
     * If `false` the `BikeInfrastructure.NO_INFORMATION` is used instead as expected by the `RadSimTagMerger`.
     * @return the `RadSimTag`s
     */
    @SuppressWarnings("CyclomaticComplexMethod") // TODO
    fun toRadSim(defaultInfrastructure: Boolean): HashMap<String, String> {
        val original = HashMap<String, Any>()
        tags.forEach(Consumer { osmTag: OsmTag -> original[osmTag.key] = osmTag.value })
        val originalTags = HashMap<String, String>()

        when (SurfaceQuality.toRadSim(original)) {
            SurfaceQuality.GOOD ->
                originalTags[SurfaceQuality.RADSIM_TAG] = SurfaceQuality.GOOD.value
            SurfaceQuality.MEDIUM ->
                originalTags[SurfaceQuality.RADSIM_TAG] = SurfaceQuality.MEDIUM.value
            SurfaceQuality.BAD ->
                originalTags[SurfaceQuality.RADSIM_TAG] = SurfaceQuality.BAD.value
            else ->
                originalTags[SurfaceQuality.RADSIM_TAG] = SurfaceQuality.NO_INFORMATION.value
        }

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

        // Cannot add another BikeInfrastructure type as this would affect the Mapper.map() functionality
        val default = if (defaultInfrastructure) { BikeInfrastructure.MIXED } else { BikeInfrastructure.NO_INFORMATION }
        when (BikeInfrastructure.toRadSim(original, default)) {
            BikeInfrastructure.LANE ->
                originalTags[BikeInfrastructure.RADSIM_TAG] = BikeInfrastructure.LANE.value
            BikeInfrastructure.TRACK ->
                originalTags[BikeInfrastructure.RADSIM_TAG] = BikeInfrastructure.TRACK.value
            BikeInfrastructure.MIXED ->
                originalTags[BikeInfrastructure.RADSIM_TAG] = BikeInfrastructure.MIXED.value
            else ->
                originalTags[BikeInfrastructure.RADSIM_TAG] = BikeInfrastructure.NO_INFORMATION.value
        }

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
