/*
 * This file is part of bowler-kinematics-native.
 *
 * bowler-kinematics-native is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bowler-kinematics-native is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with bowler-kinematics-native.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.neuronrobotics.bowlerkinematicsnative.solver

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class NativeIKSolverTest {

    @Test
    fun testSolve() {
        try {
            val expected =
                doubleArrayOf(0.6091736, 0.32373807, 1.2073439, -0.032999218, 1.1978451, 0.0)

            val dhParams = doubleArrayOf(
                13.0,
                Math.toRadians(90.0),
                32.0,
                Math.toRadians(-90.0),
                25.0,
                Math.toRadians(-90.0),
                93.0,
                Math.toRadians(180.0),
                11.0,
                Math.toRadians(90.0),
                24.0,
                Math.toRadians(90.0),
                128.0,
                Math.toRadians(-90.0),
                0.0,
                Math.toRadians(90.0),
                0.0,
                0.0,
                0.0,
                Math.toRadians(-90.0),
                25.0,
                Math.toRadians(90.0),
                0.0,
                0.0
            )

            val upperJointLimits = doubleArrayOf(
                Math.toRadians(180.0),
                Math.toRadians(180.0),
                Math.toRadians(180.0),
                Math.toRadians(180.0),
                Math.toRadians(180.0),
                Math.toRadians(180.0)
            )

            val lowerJointLimits = doubleArrayOf(
                Math.toRadians(-180.0),
                Math.toRadians(-180.0),
                Math.toRadians(-180.0),
                Math.toRadians(-180.0),
                Math.toRadians(-180.0),
                Math.toRadians(-180.0)
            )

            val initialJointAngles = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

            val target = doubleArrayOf(
                1.0,
                0.0,
                0.0,
                41.999999999999986,
                0.0,
                1.0,
                0.0,
                -44.0,
                0.0,
                0.0,
                1.0,
                169.0,
                0.0,
                0.0,
                0.0,
                1.0
            )

            val actual = NativeIKSolver.solve(
                6,
                dhParams,
                upperJointLimits,
                lowerJointLimits,
                initialJointAngles,
                target
            )

            assertArrayEquals(
                expected,
                actual,
                0.07,
                """
                Expected:
                ${expected.joinToString()}
                Actual:
                ${actual.joinToString()}
                """
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            fail(ex.message)
        }
    }
}
