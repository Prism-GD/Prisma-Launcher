package dev.prisma.launcher

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                onSettings = {
                    val status = if (nativeRuntime.isLoaded) {
                        "Runtime nativo Prisma carregado."
                    } else {
                        "Runtime nativo Prisma indisponível."
                    }
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroy() {
        if (::nativeRuntime.isInitialized) nativeRuntime.close()
        super.onDestroy()
    }
}
