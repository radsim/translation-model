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
 * A mapping from an OSM tag into some feature enumeration.
 *
 * @property tagKey The key of the mapping
 * @property tagValue The value of the mapping
 * @property target The target enumeration value of the mapping
 * @property <T> The type this mapping maps to
 */
class Mapping<T : Enum<T>>(
    val tagKey: String,
    val tagValue: Regex,
    val target: T
) {

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + tagKey.hashCode()
        result = prime * result + tagValue.hashCode()
        result = prime * result + target.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Mapping<*>

        if (tagKey != other.tagKey) return false
        if (tagValue != other.tagValue) return false
        if (target != other.target) return false

        return true
    }

    override fun toString(): String {
        return "Mapping(tagKey='$tagKey', tagValue=$tagValue, target=$target)"
    }
}
