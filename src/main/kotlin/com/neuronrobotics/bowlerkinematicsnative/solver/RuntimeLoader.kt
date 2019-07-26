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

import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files

/**
 * Loads native libraries at runtime by extracting them from the system JAR. This implementation is
 * based on https://github.com/wpilibsuite/allwpilib/blob/v2019.4.1/wpiutil/src/main/java/edu/wpi/first/wpiutil/RuntimeLoader.java.
 */
object RuntimeLoader {

    private const val buffer64k = 0xFFFF // 64K copy buffer

    /**
     * Loads a library by name.
     *
     * @param libName The library name without platform-dependent prefix or suffix.
     * @param extractionRoot The folder to extract the library into.
     */
    @Throws(IOException::class)
    fun loadLibrary(libName: String, extractionRoot: String) {
        try {
            System.loadLibrary(libName)
        } catch (ule: UnsatisfiedLinkError) {
            val libResource = getLibraryResource(libName)
            ClassLoader.getSystemResourceAsStream(libResource).use { libStream ->
                if (libStream == null) {
                    throw IOException(getLoadErrorMessage(libName))
                }

                val jniLibrary = File(extractionRoot, libResource).apply {
                    parentFile.mkdirs()
                }

                copyStreamToFile(jniLibrary, libStream)

                System.load(jniLibrary.absolutePath)
            }
        }
    }

    private fun copyStreamToFile(jniLibrary: File, libStream: InputStream) {
        Files.newOutputStream(jniLibrary.toPath()).use { os ->
            val buffer = ByteArray(buffer64k)
            var readBytes: Int
            do {
                readBytes = libStream.read(buffer)
                os.write(buffer, 0, readBytes)
            } while (readBytes != -1)
        }
    }

    /**
     * @return The library name with its platform-dependent prefix and suffix.
     */
    private fun getLibraryResource(libName: String): String {
        val (prefix, suffix) = when {
            SystemUtils.IS_OS_WINDOWS -> "" to ".dll"
            SystemUtils.IS_OS_MAC -> "lib" to ".dylib"
            SystemUtils.IS_OS_LINUX -> "lib" to ".so"
            else -> throw IllegalStateException("Failed to determine OS")
        }

        return prefix + libName + suffix
    }

    private fun getLoadErrorMessage(libName: String) =
        "$libName could not be loaded from path or an embedded resource. " +
            "Attempted to load for platform: ${SystemUtils.OS_NAME}"
}
