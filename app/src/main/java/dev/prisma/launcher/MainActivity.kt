package dev.prisma.launcher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.prisma.launcher.game.PrismaGameActivity
import dev.prisma.launcher.packages.GamePackageLayoutInspector
import dev.prisma.launcher.packages.InstalledGameDetector
import dev.prisma.launcher.runtime.NativeOverlayRuntime
import dev.prisma.launcher.ui.LauncherScreen

class MainActivity : ComponentActivity() {
    private lateinit var nativeRuntime: NativeOverlayRuntime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nativeRuntime = NativeOverlayRuntime()

        setContent {
            LauncherScreen(
                detector = InstalledGameDetector(applicationContext),
                onPlay = {
                    val intent = packageManager.getLaunchIntentForPackage(
                        InstalledGameDetector.GEOMETRY_DASH_PACKAGE
                    )
                    if (intent == null) {
                        Toast.makeText(this, "Geometry Dash não pode ser iniciado.", Toast.LENGTH_SHORT).show()
                    } else {
                        try {
                            startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(this, "Não foi possível abrir Geometry Dash.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onHostPreview = {
                    startActivity(Intent(this, PrismaGameActivity::class.java))
                },
                onSettings = {
                    val runtimeStatus = if (nativeRuntime.isLoaded) {
                        "Runtime Prisma: carregado no processo do launcher.\n\n"
                    } else {
                        "Runtime Prisma: indisponível no processo do launcher.\n\n"
                    }
                    val packageReport = GamePackageLayoutInspector(applicationContext)
                        .inspect()
                        .format()
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Diagnóstico do Geometry Dash")
                        .setMessage(runtimeStatus + packageReport)
                        .setPositiveButton("OK", null)
                        .show()
                }
            )
        }
    }

    override fun onDestroy() {
        if (::nativeRuntime.isInitialized) nativeRuntime.close()
        super.onDestroy()
    }
}
