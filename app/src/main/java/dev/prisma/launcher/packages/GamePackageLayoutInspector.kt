package dev.prisma.launcher.packages

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.util.zip.ZipFile

/**
 * Read-only inspection of the installed Geometry Dash package layout.
 *
 * This does not load, extract, patch, or modify game files.
 */
class GamePackageLayoutInspector(context: Context) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    fun inspect(): Report {
        val packageInfo = try {
            getPackageInfo()
        } catch (error: Exception) {
            return Report(
                installed = false,
                versionName = null,
                versionCode = null,
                abis = Build.SUPPORTED_ABIS.toList(),
                apkPaths = emptyList(),
                nativeLibraryDirectory = null,
                nativeLibraryDirectoryExists = false,
                libraries = emptyMap(),
                error = error.message ?: error.javaClass.simpleName,
            )
        }

        val appInfo = packageInfo.applicationInfo
        val apkPaths = buildList {
            appInfo?.sourceDir?.let(::add)
            appInfo?.splitSourceDirs?.forEach(::add)
        }.distinct()

        val nativeDirectory = appInfo?.nativeLibraryDir?.let(::File)
        val libraryNames = listOf("libfmod.so", "libcocos2dcpp.so")
        val libraryPresence = libraryNames.associateWith { library ->
            val extracted = nativeDirectory?.let { File(it, library).isFile } == true
            val inApk = apkPaths.any { apkPath -> containsNativeLibrary(apkPath, library) }
            LibraryPresence(extracted = extracted, packaged = inApk)
        }

        return Report(
            installed = true,
            versionName = packageInfo.versionName,
            versionCode = packageInfo.compatVersionCode(),
            abis = Build.SUPPORTED_ABIS.toList(),
            apkPaths = apkPaths,
            nativeLibraryDirectory = nativeDirectory?.absolutePath,
            nativeLibraryDirectoryExists = nativeDirectory?.isDirectory == true,
            libraries = libraryPresence,
            error = null,
        )
    }

    private fun containsNativeLibrary(apkPath: String, libraryName: String): Boolean {
        return try {
            ZipFile(apkPath).use { zip ->
                val prefix = "lib/"
                val suffix = "/$libraryName"
                zip.entries().asSequence().any { entry ->
                    !entry.isDirectory &&
                        entry.name.startsWith(prefix) &&
                        entry.name.endsWith(suffix)
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                InstalledGameDetector.GEOMETRY_DASH_PACKAGE,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(
                InstalledGameDetector.GEOMETRY_DASH_PACKAGE,
                PackageManager.GET_META_DATA,
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun PackageInfo.compatVersionCode(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode else versionCode.toLong()

    data class LibraryPresence(
        val extracted: Boolean,
        val packaged: Boolean,
    ) {
        fun found(): Boolean = extracted || packaged
    }

    data class Report(
        val installed: Boolean,
        val versionName: String?,
        val versionCode: Long?,
        val abis: List<String>,
        val apkPaths: List<String>,
        val nativeLibraryDirectory: String?,
        val nativeLibraryDirectoryExists: Boolean,
        val libraries: Map<String, LibraryPresence>,
        val error: String?,
    ) {
        fun format(): String = buildString {
            if (!installed) {
                append("Geometry Dash não encontrado.\n")
                append(error ?: "PackageManager não encontrou o pacote.")
                return@buildString
            }

            append("Versão: ")
            append(versionName ?: "desconhecida")
            append(" (código ")
            append(versionCode ?: -1)
            append(")\n")
            append("ABI do dispositivo: ")
            append(abis.joinToString().ifBlank { "desconhecida" })
            append("\nAPK base/splits: ")
            append(apkPaths.size)
            append("\nDiretório nativo extraído: ")
            append(if (nativeLibraryDirectoryExists) "presente" else "ausente")
            append("\n")
            libraries.forEach { (name, presence) ->
                append(name)
                append(": ")
                append(
                    when {
                        presence.extracted -> "extraída"
                        presence.packaged -> "dentro do APK/split"
                        else -> "não encontrada"
                    },
                )
                append("\n")
            }
            append("\nInspeção somente leitura; nenhuma biblioteca foi carregada.")
        }
    }
}
