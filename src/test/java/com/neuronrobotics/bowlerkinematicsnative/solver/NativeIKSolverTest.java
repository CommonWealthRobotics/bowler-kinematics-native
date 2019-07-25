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
package com.neuronrobotics.bowlerkinematicsnative.solver;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

class NativeIKSolverTest
{

	@Test void testSolve()
	{
		float[] expected = { 0.6091736f, 0.32373807f, 1.2073439f, -0.032999218f, 1.1978451f, 0 };
		float[] actual = NativeIKSolver.solve(6,
				new float[] { 13, (float) Math.toRadians(90), 32, (float) Math.toRadians(-90), 25,
						(float) Math.toRadians(-90), 93, (float) Math.toRadians(180), 11,
						(float) Math.toRadians(90), 24, (float) Math.toRadians(90), 128,
						(float) Math.toRadians(-90), 0, (float) Math.toRadians(90), 0, 0, 0,
						(float) Math.toRadians(-90), 25, (float) Math.toRadians(90), 0, 0,
						(float) Math.toRadians(180), (float) Math.toRadians(180),
						(float) Math.toRadians(180), (float) Math.toRadians(180),
						(float) Math.toRadians(180), (float) Math.toRadians(180),
						(float) Math.toRadians(-180), (float) Math.toRadians(-180),
						(float) Math.toRadians(-180), (float) Math.toRadians(-180),
						(float) Math.toRadians(-180), (float) Math.toRadians(-180), 1, 0, 0,
						41.999999999999986f, 0, 1, 0, -44, 0, 0, 1, 169, 0, 0, 0, 1, 0, 0, 0, 0, 0,
						0
				});
		assertArrayEquals(expected, actual);
	}
}
