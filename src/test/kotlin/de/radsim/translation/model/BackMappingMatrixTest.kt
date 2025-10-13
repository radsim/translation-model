/*
 * Copyright (C) 2025 Cyface GmbH
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

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class BackMappingMatrixTest {

    @TestFactory
    fun `all infrastructure combinations should back-map without recursion or stall`(): List<DynamicTest> {
        val values = SimplifiedBikeInfrastructure.entries

        return values.flatMap { from ->
            values.filter { it != from }.map { to ->
                DynamicTest.dynamicTest("$from → $to") {
                    assertDoesNotThrow {
                        RadSimTagMapper(emptyList()).recursiveBackMap(from, to, emptyMap(), false)
                    }
                }
            }
        }
    }
}
