package com.deltabugged.kernelmanager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { DeltaApp() } }
}
private fun cpuName() = try {
 File("/proc/cpuinfo").useLines { it.firstOrNull { l -> l.startsWith("Hardware") || l.startsWith("model name") }?.substringAfter(":")?.trim() ?: "Unavailable" }
} catch (_: Exception) { "Unavailable" }
private fun thermal() = try {
 val f=File("/sys/class/thermal").listFiles()?.filter { it.name.startsWith("thermal_zone") }.orEmpty()
 val t=f.firstNotNullOfOrNull { z -> File(z,"temp").takeIf { it.canRead() }?.readText()?.trim()?.toLongOrNull() }
 t?.let { "%.1f °C".format(if(it>1000) it/1000.0 else it.toDouble()) } ?: "Unavailable"
} catch (_: Exception) { "Unavailable" }
private suspend fun rootCheck()=withContext(Dispatchers.IO) {
 try { val p=Runtime.getRuntime().exec(arrayOf("su","-c","id -u")); val out=p.inputStream.bufferedReader().readText().trim(); if(p.waitFor()==0 && out=="0") "Root access granted" else "Root not granted" }
 catch(_:Exception) { "Root unavailable" }
}
@Composable private fun DeltaApp() {
 var root by remember { mutableStateOf("Not checked") }
 var checking by remember { mutableStateOf(false) }
 val bg=Brush.linearGradient(listOf(Color(0xFF071321),Color(0xFF15334A),Color(0xFF2A1D42)))
 MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF9CE7F2),background=Color(0xFF071321),surface=Color(0xFF183047))) {
  Box(Modifier.fillMaxSize().background(bg)) {
   Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement=Arrangement.spacedBy(15.dp)) {
    Row(verticalAlignment=Alignment.CenterVertically) {
     Column(Modifier.weight(1f)) {
      Text("DELTA",color=Color(0xFF9CE7F2),fontSize=12.sp,fontWeight=FontWeight.Bold,letterSpacing=3.sp)
      Text("Kernel Manager",color=Color.White,fontSize=27.sp,fontWeight=FontWeight.Bold)
      Text("Universal kernel dashboard",color=Color(0xFFB8C8D8),fontSize=13.sp)
     }
     Surface(color=Color(0x339CE7F2),shape=RoundedCornerShape(16.dp)){Text("v0.1.0",Modifier.padding(11.dp),color=Color(0xFFBDF4FA),fontSize=12.sp)}
    }
    GlassCard {
     Text("DEVICE",color=Color(0xFF9CE7F2),fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
     Text("${Build.MANUFACTURER} ${Build.MODEL}",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.SemiBold)
     Text("Android ${Build.VERSION.RELEASE} · SDK ${Build.VERSION.SDK_INT}",color=Color(0xFFB8C8D8),fontSize=13.sp)
     Info("Kernel",System.getProperty("os.version") ?: "Unknown")
     Info("CPU",cpuName())
    }
    Text("LIVE OVERVIEW",color=Color(0xFF9CE7F2),fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
     Metric(Modifier.weight(1f),"CPU","Detected",Icons.Filled.Memory)
     Metric(Modifier.weight(1f),"Thermal",thermal(),Icons.Filled.Thermostat)
    }
    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
     Metric(Modifier.weight(1f),"Battery","Coming soon",Icons.Filled.BatteryChargingFull)
     Metric(Modifier.weight(1f),"Root",root,Icons.Filled.Security)
    }
    GlassCard {
     Text("ROOT ACCESS",color=Color(0xFF9CE7F2),fontWeight=FontWeight.Bold)
     Text("Root is checked only when you tap. No kernel settings are changed.",color=Color(0xFFB8C8D8),fontSize=13.sp)
     Button(onClick={checking=true},enabled=!checking,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF9CE7F2),contentColor=Color(0xFF092033))) { Text(if(checking) "Checking…" else "Check root access") }
     LaunchedEffect(checking) { if(checking) { root=rootCheck(); checking=false } }
    }
    GlassCard {
     Text("KERNEL CONTROLS",color=Color(0xFF9CE7F2),fontWeight=FontWeight.Bold)
     Text("Read-only starter. Controls will be enabled only when supported interfaces are detected.",color=Color(0xFFB8C8D8),fontSize=13.sp)
     Info("CPU governor","Not detected yet"); Info("GPU controls","Not detected yet"); Info("Profiles","Coming later")
    }
    Text("Delta Kernel Manager · device-aware tuning",color=Color(0xFF8195AA),fontSize=11.sp,modifier=Modifier.align(Alignment.CenterHorizontally))
   }
  }
 }
}
@Composable private fun GlassCard(content:@Composable ColumnScope.()->Unit) {
 Surface(color=Color(0x331D354B),shape=RoundedCornerShape(23.dp),border=BorderStroke(1.dp,Color(0x449CE7F2)),modifier=Modifier.fillMaxWidth()) {
  Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.spacedBy(9.dp),content=content)
 }
}
@Composable private fun Info(k:String,v:String) {
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.Top) {
  Text(k,color=Color(0xFF9FB3C8),fontSize=12.sp); Text(v,color=Color.White,fontSize=12.sp,modifier=Modifier.widthIn(max=210.dp))
 }
}
@Composable private fun Metric(modifier:Modifier,title:String,value:String,icon:ImageVector) {
 Surface(modifier=modifier,color=Color(0x331D354B),shape=RoundedCornerShape(19.dp),border=BorderStroke(1.dp,Color(0x339CE7F2))) {
  Column(Modifier.padding(13.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
   Icon(icon,null,tint=Color(0xFF9CE7F2)); Text(title,color=Color(0xFFB8C8D8),fontSize=12.sp); Text(value,color=Color.White,fontSize=14.sp,fontWeight=FontWeight.SemiBold)
  }
 }
}
