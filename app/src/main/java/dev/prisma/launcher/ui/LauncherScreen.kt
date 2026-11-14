package dev.prisma.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.prisma.launcher.packages.InstalledGameDetector

private val PrismaPurple = Color(0xFF9B72FF)
private val PrismaBackground = Color(0xFF100D1B)
private val PrismaPanel = Color(0xFF201A32)

/**
 * First Prisma Launcher home screen.
 *
 * Host apps should provide navigation callbacks. The Play action is only exposed
 * when Android reports that Geometry Dash is installed and launchable.
 */
@Composable
fun LauncherScreen(
    detector: InstalledGameDetector,
    onPlay: () -> Unit,
    onMods: () -> Unit,
    onSettings: () -> Unit,
) {
    var detection by remember { mutableStateOf<InstalledGameDetector.DetectionResult?>(null) }

    LaunchedEffect(detector) {
        detection = detector.detect()
    }

    val installed = detection as? InstalledGameDetector.DetectionResult.Installed
    val canPlay = installed?.enabled == true && installed.launchable

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF21143D), PrismaBackground, Color(0xFF090810)),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("PRISMA", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("GEOMETRY DASH LAUNCHER", color = PrismaPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(PrismaPurple.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("P", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = PrismaPanel),
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SEU JOGO", color = PrismaPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Text("Geometry Dash", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        when (val result = detection) {
                            null -> Text("Verificando instalação…", color = Color.LightGray)
                            is InstalledGameDetector.DetectionResult.Installed -> {
                                Text(
                                    if (canPlay) "●  Instalado e pronto para abrir" else "●  Instalado, mas não pode ser iniciado",
                                    color = if (canPlay) Color(0xFF7DE6A5) else Color(0xFFFFC66D),
                                    fontSize = 13.sp,
                                )
                                Text("Versão ${result.versionName}  •  código ${result.versionCode}", color = Color.LightGray, fontSize = 12.sp)
                                Text(if (result.supportsArm64) "Arquitetura ARM64 compatível" else "ARM64 não detectada", color = Color.LightGray, fontSize = 12.sp)
                            }
                            is InstalledGameDetector.DetectionResult.NotInstalled ->
                                Text("Jogo não encontrado neste dispositivo.", color = Color(0xFFFFC66D), fontSize = 13.sp)
                            is InstalledGameDetector.DetectionResult.CheckFailed ->
                                Text("Não foi possível verificar: ${result.message}", color = Color(0xFFFFC66D), fontSize = 13.sp)
                        }
                        Button(
                            onClick = onPlay,
                            enabled = canPlay,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrismaPurple),
                        ) {
                            Text("JOGAR", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                    }
                }

                Text("CENTRAL PRISMA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard("MODS", "Gerencie seus\npacotes .prism", Modifier.weight(1f), onMods)
                    FeatureCard("AJUSTES", "Preferências e\ndiagnóstico", Modifier.weight(1f), onSettings)
                }

                Spacer(Modifier.weight(1f))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF171323),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("PRISMA", color = PrismaPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Launcher independente • Preview", color = Color.LightGray, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, color = PrismaPurple, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = Color.LightGray, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}
