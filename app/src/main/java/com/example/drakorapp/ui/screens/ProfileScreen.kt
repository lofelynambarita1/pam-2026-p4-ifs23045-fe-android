package com.example.drakorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.drakorapp.data.network.ApiClient
import com.example.drakorapp.ui.theme.C
import com.example.drakorapp.viewmodel.DrakorViewModel
import com.example.drakorapp.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: DrakorViewModel, onBack: () -> Unit) {
    val state by vm.profileState.collectAsState()
    LaunchedEffect(Unit) { vm.loadProfile() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Developer", color = Color.White) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = C.Surface)
            )
        },
        containerColor = C.BG
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(24.dp))
            Box(Modifier.size(110.dp).clip(CircleShape).background(C.Card), Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = C.Muted, modifier = Modifier.size(50.dp))
                AsyncImage(
                    model = "${ApiClient.getBaseUrl()}profile/photo",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(20.dp))
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(color = C.Primary)
                is UiState.Error   -> Text(s.message, color = C.Muted)
                is UiState.Success -> {
                    val d = s.data
                    Text(d["nama"] ?: "-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("@${d["username"] ?: ""}", color = C.Primary, fontSize = 14.sp)
                    Spacer(Modifier.height(24.dp))
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(14.dp), CardDefaults.cardColors(containerColor = C.Card)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Tentang", color = C.Muted, fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(d["tentang"] ?: "-", color = Color(0xFFB0AABF), fontSize = 14.sp, lineHeight = 22.sp)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
