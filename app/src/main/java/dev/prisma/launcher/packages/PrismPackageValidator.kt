package dev.prisma.launcher.packages

import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.zip.ZipFile

/**
 * Reads and validates Prisma .prism archives without extracting or executing content.
 *
 * This is a metadata/package-structure validator only. It does not load native code.
 */
object PrismPackageValidator {
    private const val MANIFEST_PATH = "manifest.json"
    private const val MAX_ARCHIVE_BYTES = 100L * 1024L * 1024L
    private const val MAX_TOTAL_UNCOMPRESSED_BYTES = 200L * 1024L * 1024L
    private const val MAX_ENTRY_BYTES = 50L * 1024L * 1024L
    private const val MAX_ENTRIES = 2_000
    private const val MAX_COMPRESSION_RATIO = 100L
    private const val MAX_MANIFEST_BYTES = 256L * 1024L

    data class Metadata(
        val id: String,
        val name: String,
        val version: String,
        val description: String,
        val authors: List<String>,
        val gameId: String,
        val gameVersions: List<String>,
        val runtimeId: String,
        val runtimeApi: String,
        val targets: List<String>,
        val entrypoint: String?,
        val permissions: List<String>
    )

    sealed class Result {
        data class Valid(val metadata: Metadata, val archiveEntries: Int) : Result()
        data class Invalid(val reason: String) : Result()
    }

    @JvmStatic
    fun validate(file: File): Result {
        if (!file.isFile) return Result.Invalid("O arquivo não existe ou não é um arquivo regular.")
        if (!file.name.lowercase(Locale.ROOT).endsWith(".prism")) {
            return Result.Invalid("A extensão do pacote deve ser .prism.")
        }
        if (file.length() <= 0L || file.length() > MAX_ARCHIVE_BYTES) {
            return Result.Invalid("Tamanho do arquivo fora do limite permitido.")
        }

        return try {
            ZipFile(file).use { zip ->
                val names = HashSet<String>()
                var count = 0
                var totalSize = 0L
                val manifestEntry = zip.getEntry(MANIFEST_PATH)
                    ?: return Result.Invalid("manifest.json não encontrado na raiz.")

                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    count++
                    if (count > MAX_ENTRIES) return Result.Invalid("O pacote contém arquivos demais.")

                    val pathError = validateEntryPath(entry.name)
                    if (pathError != null) return Result.Invalid(pathError)
                    if (!names.add(entry.name)) return Result.Invalid("Entrada ZIP duplicada: ${entry.name}")

                    if (!entry.isDirectory) {
                        val size = entry.size
                        val compressed = entry.compressedSize
                        if (size < 0L || size > MAX_ENTRY_BYTES) {
                            return Result.Invalid("Tamanho inválido ou excessivo em: ${entry.name}")
                        }
                        totalSize += size
                        if (totalSize > MAX_TOTAL_UNCOMPRESSED_BYTES) {
                            return Result.Invalid("O conteúdo descompactado excede o limite.")
                        }
                        if (size > 0L && compressed == 0L) {
                            return Result.Invalid("Razão de compressão inválida em: ${entry.name}")
                        }
                        if (compressed > 0L && size / compressed.coerceAtLeast(1L) > MAX_COMPRESSION_RATIO) {
                            return Result.Invalid("Razão de compressão excessiva em: ${entry.name}")
                        }
                    }
                }

                if (manifestEntry.size < 0L || manifestEntry.size > MAX_MANIFEST_BYTES) {
                    return Result.Invalid("manifest.json excede o limite permitido.")
                }

                val manifestBytes = zip.getInputStream(manifestEntry).use {
                    readBounded(it, MAX_MANIFEST_BYTES)
                }
                val manifestText = manifestBytes.toString(Charsets.UTF_8)
                val metadata = parseManifest(manifestText)
                    ?: return Result.Invalid("manifest.json inválido ou incompleto.")

                if (metadata.entrypoint != null && !names.contains(metadata.entrypoint)) {
                    return Result.Invalid("O entrypoint declarado não existe no pacote.")
                }

                Result.Valid(metadata, count)
            }
        } catch (e: IOException) {
            Result.Invalid("Não foi possível ler o pacote ZIP: ${e.message ?: "erro de leitura"}.")
        } catch (e: SecurityException) {
            Result.Invalid("Acesso ao pacote negado.")
        } catch (e: JSONException) {
            Result.Invalid("manifest.json contém JSON inválido.")
        } catch (e: IllegalArgumentException) {
            Result.Invalid("O pacote contém dados inválidos.")
        }
    }

    private fun readBounded(input: java.io.InputStream, limit: Long): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8 * 1024)
        var total = 0L
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            total += read
            if (total > limit) throw IOException("manifest.json excede o limite durante a leitura")
            output.write(buffer, 0, read)
        }
        return output.toByteArray()
    }

    private fun validateEntryPath(path: String): String? {
        if (path.isBlank()) return "O pacote contém um caminho vazio."
        if (path.startsWith("/") || path.startsWith("\\") || path.contains('\\')) {
            return "Caminho absoluto ou separador inválido: $path"
        }
        val segments = path.split('/')
        if (segments.any { it == ".." || it == "." || it.isEmpty() }) {
            // A final empty segment is allowed only for a directory entry.
            val normalized = if (path.endsWith("/") && segments.dropLast(1).none { it.isEmpty() || it == "." || it == ".." }) null
                else "Caminho inseguro no pacote: $path"
            return normalized
        }
        return null
    }

    private fun parseManifest(text: String): Metadata? {
        val json = JSONObject(text)
        if (json.optString("format_version") != "1") return null

        val id = json.optString("id")
        val name = json.optString("name")
        val version = json.optString("version")
        val description = json.optString("description")
        val authors = json.optJSONArray("authors") ?: return null
        val game = json.optJSONObject("game") ?: return null
        val runtime = json.optJSONObject("runtime") ?: return null

        if (!id.matches(Regex("[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)+"))) return null
        if (name.isBlank() || version.isBlank() || description.isBlank() || authors.length() == 0) return null

        val authorList = jsonArrayStrings(authors) ?: return null
        if (authorList.any { it.isBlank() }) return null

        val gameId = game.optString("id")
        val gameVersions = jsonArrayStrings(game.optJSONArray("versions")) ?: return null
        val runtimeId = runtime.optString("id")
        val runtimeApi = runtime.optString("api")
        val targets = jsonArrayStrings(runtime.optJSONArray("targets")) ?: return null
        if (gameId.isBlank() || gameVersions.isEmpty() || runtimeId != "prisma" ||
            runtimeApi.isBlank() || targets.isEmpty()) return null

        val entrypoint = if (json.has("entrypoint")) json.optString("entrypoint").takeIf { it.isNotBlank() } else null
        if (entrypoint != null && validateEntryPath(entrypoint) != null) return null

        val permissions = if (json.has("permissions")) {
            jsonArrayStrings(json.optJSONArray("permissions")) ?: return null
        } else emptyList()

        return Metadata(id, name, version, description, authorList, gameId, gameVersions,
            runtimeId, runtimeApi, targets, entrypoint, permissions)
    }

    private fun jsonArrayStrings(array: org.json.JSONArray?): List<String>? {
        if (array == null) return null
        val values = ArrayList<String>(array.length())
        for (index in 0 until array.length()) {
            val value = array.opt(index)
            if (value !is String) return null
            values.add(value)
        }
        return values
    }
}
