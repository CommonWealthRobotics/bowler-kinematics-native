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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.apache.commons.lang3.SystemUtils;

/**
 * Loads native libraries at runtime by extracting them from the system JAR. This implementation is
 * based on https://github.com/wpilibsuite/allwpilib/blob/v2019.4.1/wpiutil/src/main/java/edu/wpi/first/wpiutil/RuntimeLoader.java.
 */
public final class RuntimeLoader
{

	/**
	 * Loads a library by name.
	 *
	 * @param libName        The library name without platform-dependent prefix or suffix.
	 * @param extractionRoot The folder to extract the library into.
	 */
	public static void loadLibrary(final String libName, final String extractionRoot)
			throws IOException
	{
		try {
			System.loadLibrary(libName);
		} catch (final UnsatisfiedLinkError ule) {
			final String libResource = getLibraryResource(libName);
			try (final InputStream libStream = ClassLoader.getSystemResourceAsStream(libResource)) {
				if (libStream == null) {
					throw new IOException(getLoadErrorMessage(libName));
				}

				final File jniLibrary = new File(extractionRoot, libResource);

				//noinspection ResultOfMethodCallIgnored
				jniLibrary.getParentFile().mkdirs();

				try (final OutputStream os = Files.newOutputStream(jniLibrary.toPath())) {
					final byte[] buffer = new byte[0xFFFF]; // 64K copy buffer
					int readBytes;
					while ((readBytes = libStream.read(buffer)) != -1) {
						os.write(buffer, 0, readBytes);
					}
				}

				System.load(jniLibrary.getAbsolutePath());
			}
		}
	}

	/**
	 * @return The library name with its platform-dependent prefix and suffix.
	 */
	private static String getLibraryResource(final String libName)
	{
		String prefix;
		String suffix;

		if (SystemUtils.IS_OS_WINDOWS) {
			prefix = "";
			suffix = ".dll";
		} else if (SystemUtils.IS_OS_MAC) {
			prefix = "lib";
			suffix = ".dylib";
		} else if (SystemUtils.IS_OS_LINUX) {
			prefix = "lib";
			suffix = ".so";
		} else {
			throw new IllegalStateException("Failed to determine OS");
		}

		return prefix + libName + suffix;
	}

	private static String getLoadErrorMessage(final String libName)
	{
		return libName + " could not be loaded from path or an embedded resource.\n"
				+ "\tattempted to load for platform " + SystemUtils.OS_NAME + '\n';
	}

	private RuntimeLoader()
	{
	}
}
