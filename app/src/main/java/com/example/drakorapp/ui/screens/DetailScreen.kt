package com.example.drakorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun DetailScreen(
    vm: DrakorViewModel,
    id: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val detailState by vm.detailState.collectAsState()
    val actionState by vm.actionState.collectAsState()
    var showDelete by remember { mutableStateOf(false) }

    LaunchedEffect(id) { vm.loadDrakorById(id) }
    LaunchedEffect(actionState) {
        if (actionState is UiState.Success) { vm.resetAction(); onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Drakor", color = Color.White) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                actions = {
                    if (detailState is UiState.Success) {
                        IconButton({ onEdit(id) })        { Icon(Icons.Default.Edit,   null, tint = Color.White) }
                        IconButton({ showDelete = true }) { Icon(Icons.Default.Delete, null, tint = C.Red) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = C.Surface)
            )
        },
        containerColor = C.BG
    ) { pad ->
        when (val s = detailState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(pad), Alignment.Center) { CircularProgressIndicator(color = C.Primary) }
            is UiState.Error   -> Box(Modifier.fillMaxSize().padding(pad), Alignment.Center) { Text(s.message, color = C.Muted) }
            is UiState.Success -> {
                val d = s.data
                Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState())) {
                    Box(Modifier.fillMaxWidth().height(260.dp).background(C.Surface)) {
                        AsyncImage(
                            model = "${ApiClient.getBaseUrl()}drakors/${d.id}/poster",
                            contentDescription = d.judul,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Text(d.judul, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(10.dp))
                            StatusChip(d.status)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                            StatItem(Icons.Default.Star,        "${d.rating}",     "Rating",  C.Gold)
                            StatItem(Icons.Default.DateRange,   "${d.tahun}",      "Tahun",   C.Blue)
                            StatItem(Icons.Default.PlayCircle,  "${d.episode} ep", "Episode", C.Green)
                        }
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = C.Border)
                        Spacer(Modifier.height(16.dp))
                        Row { Text("Genre  ", color = C.Muted, fontSize = 14.sp); Text(d.genre, color = Color.White, fontSize = 14.sp) }
                        Spacer(Modifier.height(16.dp))
                        Text("Sinopsis", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(d.sinopsis, color = Color(0xFFB0AABF), fontSize = 14.sp, lineHeight = 22.sp)
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
            else -> {}
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            containerColor = C.Card,
            title = { Text("Hapus Drakor?", color = Color.White) },
            text  = { Text("Data akan dihapus permanen.", color = C.Muted) },
            confirmButton = {
                Button({ showDelete = false; vm.deleteDrakor(id) { } }, colors = ButtonDefaults.buttonColors(containerColor = C.Red)) {
                    Text("Hapus")
                }
            },
            dismissButton = { TextButton({ showDelete = false }) { Text("Batal", color = C.Muted) } }
        )
    }

    if (actionState is UiState.Loading) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(.5f)), Alignment.Center) {
            CircularProgressIndicator(color = C.Primary)
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = C.Muted, fontSize = 11.sp)
    }
}
