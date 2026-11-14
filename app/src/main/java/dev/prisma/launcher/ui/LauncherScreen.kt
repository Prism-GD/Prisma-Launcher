package dev.prisma.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

/** Launcher home screen. Mod management is intentionally inside the game overlay. */
@Composable
fun LauncherScreen(
    detector: InstalledGameDetector,
    onPlay: () -> Unit,
    onHostPreview: () -> Unit,
    onSettings: () -> Unit,
) {
    var detection by remember { mutableStateOf<InstalledGameDetector.DetectionResult?>(null) }
    LaunchedEffect(detector) { detection = detector.detect() }
    val installed = detection as? InstalledGameDetector.DetectionResult.Installed
    val canPlay = installed?.enabled == true && installed.launchable

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF21143D), PrismaBackground, Color(0xFF090810)))).padding(20.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("PRISMA", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("GEOMETRY DASH LAUNCHER", color = PrismaPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    Box(Modifier.size(44.dp).background(PrismaPurple.copy(alpha = 0.18f), CircleShape), contentAlignment = Alignment.Center) {
                        Text("P", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = PrismaPanel)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SEU JOGO", color = PrismaPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Text("Geometry Dash", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        when (val result = detection) {
                            null -> Text("Verificando instalação…", color = Color.LightGray)
                            is InstalledGameDetector.DetectionResult.Installed -> {
                                Text(if (canPlay) "●  Instalado e pronto para abrir" else "●  Instalado, mas não pode ser iniciado", color = if (canPlay) Color(0xFF7DE6A5) else Color(0xFFFFC66D), fontSize = 13.sp)
                                Text("Versão " + result.versionName + "  •  código " + result.versionCode, color = Color.LightGray, fontSize = 12.sp)
                                Text(if (result.supportsArm64) "Arquitetura ARM64 compatível" else "ARM64 não detectada", color = Color.LightGray, fontSize = 12.sp)
                            }
                            is InstalledGameDetector.DetectionResult.NotInstalled -> Text("Jogo não encontrado neste dispositivo.", color = Color(0xFFFFC66D), fontSize = 13.sp)
                            is InstalledGameDetector.DetectionResult.CheckFailed -> Text("Não foi possível verificar: " + result.message, color = Color(0xFFFFC66D), fontSize = 13.sp)
                        }
                        Button(onClick = onPlay, enabled = canPlay, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = PrismaPurple)) {
                            Text("JOGAR", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                    }
                }
                OutlinedButton(
                    onClick = onHostPreview,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrismaPurple),
                ) {
                    Text("TESTAR HOST OPENGL (EXPERIMENTAL)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("AJUSTES  •  Preferências e diagnóstico", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                Surface(Modifier.fillMaxWidth(), color = Color(0xFF171323), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PRISMA", color = PrismaPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Mods são gerenciados dentro do jogo", color = Color.LightGray, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
