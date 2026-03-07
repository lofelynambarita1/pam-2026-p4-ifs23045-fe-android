package com.example.drakorapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drakorapp.ui.theme.C
import com.example.drakorapp.viewmodel.DrakorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: DrakorViewModel, onBack: () -> Unit) {
    var url   by remember { mutableStateOf(vm.getBaseUrl()) }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", color = Color.White) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = C.Surface)
            )
        },
        containerColor = C.BG
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(24.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("Base URL Server", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                url, { url = it; saved = false }, Modifier.fillMaxWidth(),
                placeholder = { Text("http://10.0.2.2:8080", color = C.SubText) },
                singleLine = true, shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = C.Primary, unfocusedBorderColor = C.Border,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    cursorColor = C.Primary,
                    focusedContainerColor = C.Surface, unfocusedContainerColor = C.Surface
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "💡 Emulator  → http://10.0.2.2:8080\n💡 HP fisik  → http://192.168.x.x:8080",
                color = C.Muted, fontSize = 12.sp, lineHeight = 18.sp
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { vm.setBaseUrl(url); vm.loadDrakors(); saved = true },
                Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = C.Primary)
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Simpan & Terapkan", fontWeight = FontWeight.Bold)
            }
            if (saved) {
                Spacer(Modifier.height(14.dp))
                Row {
                    Icon(Icons.Default.CheckCircle, null, tint = C.Green, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("URL berhasil disimpan!", color = C.Green, fontSize = 13.sp)
                }
            }
        }
    }
}
