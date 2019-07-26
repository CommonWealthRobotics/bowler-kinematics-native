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

internal class NativeIKSolverTest {

    @Test
    fun testSolve() {
        val expected =
            floatArrayOf(0.6091736f, 0.32373807f, 1.2073439f, -0.032999218f, 1.1978451f, 0f)
        val actual = NativeIKSolver.solve(
            6,
            floatArrayOf(
                13f,
                Math.toRadians(90.0).toFloat(),
                32f,
                Math.toRadians(-90.0).toFloat(),
                25f,
                Math.toRadians(-90.0).toFloat(),
                93f,
                Math.toRadians(180.0).toFloat(),
                11f,
                Math.toRadians(90.0).toFloat(),
                24f,
                Math.toRadians(90.0).toFloat(),
                128f,
                Math.toRadians(-90.0).toFloat(),
                0f,
                Math.toRadians(90.0).toFloat(),
                0f,
                0f,
                0f,
                Math.toRadians(-90.0).toFloat(),
                25f,
                Math.toRadians(90.0).toFloat(),
                0f,
                0f
            ),
            floatArrayOf(
                Math.toRadians(180.0).toFloat(),
                Math.toRadians(180.0).toFloat(),
                Math.toRadians(180.0).toFloat(),
                Math.toRadians(180.0).toFloat(),
                Math.toRadians(180.0).toFloat(),
                Math.toRadians(180.0).toFloat()
            ),
            floatArrayOf(
                Math.toRadians(-180.0).toFloat(),
                Math.toRadians(-180.0).toFloat(),
                Math.toRadians(-180.0).toFloat(),
                Math.toRadians(-180.0).toFloat(),
                Math.toRadians(-180.0).toFloat(),
                Math.toRadians(-180.0).toFloat()
            ),
            floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                0f,
                0f
            ),
            floatArrayOf(
                1f,
                0f,
                0f,
                41.999999999999986f,
                0f,
                1f,
                0f,
                -44f,
                0f,
                0f,
                1f,
                169f,
                0f,
                0f,
                0f,
                1f
            )
        )

        assertArrayEquals(
            expected,
            actual,
            0.07f,
            """
                Expected:
                ${expected.joinToString()}
                Actual:
                ${actual.joinToString()}
                """
        )
    }
}
