package dev.prisma.launcher.packages

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

/**
 * Read-only detector for the official Geometry Dash installation.
 *
 * This class does not copy, patch, modify, launch, or execute the game.
 */
class InstalledGameDetector(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    fun detect(): DetectionResult {
        val packageInfo = try {
            getPackageInfo(GEOMETRY_DASH_PACKAGE)
        } catch (_: PackageManager.NameNotFoundException) {
            return DetectionResult.NotInstalled(GEOMETRY_DASH_PACKAGE)
        } catch (error: RuntimeException) {
            return DetectionResult.CheckFailed(
                message = error.message ?: "PackageManager could not inspect the game.",
            )
        }

        val appInfo = packageInfo.applicationInfo
            ?: return DetectionResult.CheckFailed("The installed package has no ApplicationInfo.")

        val launchIntent = packageManager.getLaunchIntentForPackage(GEOMETRY_DASH_PACKAGE)
        val nativeLibraryDir = appInfo.nativeLibraryDir
            ?.takeIf { it.isNotBlank() }
            ?.let(::File)

        return DetectionResult.Installed(
            packageName = packageInfo.packageName,
            versionName = packageInfo.versionName ?: "unknown",
            versionCode = packageInfo.compatVersionCode(),
            enabled = appInfo.enabled,
            launchable = launchIntent != null,
            nativeLibraryDirectory = nativeLibraryDir?.absolutePath,
            nativeLibraryDirectoryExists = nativeLibraryDir?.isDirectory == true,
            deviceAbis = Build.SUPPORTED_ABIS.toList(),
            supportsArm64 = Build.SUPPORTED_ABIS.any { it == ARM64_ABI },
        )
    }

    @Suppress("DEPRECATION")
    private fun getPackageInfo(packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0L),
            )
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }

    @Suppress("DEPRECATION")
    private fun PackageInfo.compatVersionCode(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            versionCode.toLong()
        }
    }

    sealed interface DetectionResult {
        data class Installed(
            val packageName: String,
            val versionName: String,
            val versionCode: Long,
            val enabled: Boolean,
            val launchable: Boolean,
            val nativeLibraryDirectory: String?,
            val nativeLibraryDirectoryExists: Boolean,
            val deviceAbis: List<String>,
            val supportsArm64: Boolean,
        ) : DetectionResult

        data class NotInstalled(
            val packageName: String,
        ) : DetectionResult

        data class CheckFailed(
            val message: String,
        ) : DetectionResult
    }

    companion object {
        const val GEOMETRY_DASH_PACKAGE = "com.robtopx.geometryjump"
        const val ARM64_ABI = "arm64-v8a"
    }
}
