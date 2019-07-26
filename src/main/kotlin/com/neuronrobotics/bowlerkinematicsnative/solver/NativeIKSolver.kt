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

import java.nio.file.Paths

object NativeIKSolver {

    init {
        RuntimeLoader.loadLibrary(
            "bowler_kinematics_native_native_library",
            Paths.get(
                System.getProperty("user.home"),
                "Bowler",
                "nativecache"
            ).toString()
        )
    }

    external fun solve(
        numberOfLinks: Int,
        dhParams: FloatArray,
        upperJointLimits: FloatArray,
        lowerJointLimits: FloatArray,
        initialJointAngles: FloatArray,
        target: FloatArray
    ): FloatArray
}
