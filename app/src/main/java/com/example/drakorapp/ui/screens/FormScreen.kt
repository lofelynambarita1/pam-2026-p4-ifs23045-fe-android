package com.example.drakorapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.drakorapp.data.model.Drakor
import com.example.drakorapp.data.network.ApiClient
import com.example.drakorapp.ui.theme.C
import com.example.drakorapp.viewmodel.DrakorViewModel
import com.example.drakorapp.viewmodel.UiState
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    vm: DrakorViewModel,
    existing: Drakor? = null,
    onBack: () -> Unit
) {
    val ctx    = LocalContext.current
    val isEdit = existing != null
    val action by vm.actionState.collectAsState()

    var judul    by remember { mutableStateOf(existing?.judul    ?: "") }
    var genre    by remember { mutableStateOf(existing?.genre    ?: "") }
    var tahun    by remember { mutableStateOf(existing?.tahun?.toString()   ?: "") }
    var episode  by remember { mutableStateOf(existing?.episode?.toString() ?: "") }
    var rating   by remember { mutableStateOf(existing?.rating?.toString()  ?: "") }
    var sinopsis by remember { mutableStateOf(existing?.sinopsis ?: "") }
    var status   by remember { mutableStateOf(existing?.status   ?: "Ongoing") }
    var ddOpen   by remember { mutableStateOf(false) }

    var uri   by remember { mutableStateOf<Uri?>(null) }
    var file  by remember { mutableStateOf<File?>(null) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(action) {
        if (action is UiState.Success) { vm.resetAction(); onBack() }
        if (action is UiState.Error)   error = (action as UiState.Error).message
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { u ->
        u?.let {
            uri = it
            val mime = ctx.contentResolver.getType(it) ?: "image/jpeg"
            val ext  = mime.substringAfter("/").let { e -> if (e == "jpeg") "jpg" else e }
            val tmp  = File(ctx.cacheDir, "poster.$ext")
            ctx.contentResolver.openInputStream(it)?.use { i -> FileOutputStream(tmp).use { o -> i.copyTo(o) } }
            file = tmp
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Drakor" else "Tambah Drakor", color = Color.White) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = C.Surface)
            )
        },
        containerColor = C.BG
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Poster picker
            Box(
                Modifier.fillMaxWidth().height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(C.Card)
                    .border(1.dp, if (uri != null) C.Primary else C.Border, RoundedCornerShape(14.dp))
                    .clickable { picker.launch("image/*") },
                Alignment.Center
            ) {
                when {
                    uri != null -> AsyncImage(uri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    isEdit && existing != null -> {
                        AsyncImage("${ApiClient.getBaseUrl()}drakors/${existing.id}/poster", null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(.45f)), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                Text("Ganti poster", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = C.Muted, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Pilih Poster *", color = C.Muted, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            FormField("Judul *", judul, { judul = it }, "Masukkan judul")
            Spacer(Modifier.height(12.dp))
            FormField("Genre *", genre, { genre = it }, "Contoh: Romance, Action")
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) { FormField("Tahun *", tahun, { tahun = it }, "1990", KeyboardType.Number) }
                Column(Modifier.weight(1f)) { FormField("Episode *", episode, { episode = it }, "16", KeyboardType.Number) }
            }

            Spacer(Modifier.height(12.dp))
            FormField("Rating", rating, { rating = it }, "8.5", KeyboardType.Decimal)
            Spacer(Modifier.height(12.dp))

            Text("Status *", color = C.Muted, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(ddOpen, { ddOpen = it }) {
                OutlinedTextField(
                    value = status, onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(ddOpen) },
                    shape = RoundedCornerShape(10.dp), colors = tfColors()
                )
                ExposedDropdownMenu(ddOpen, { ddOpen = false }, Modifier.background(C.Card)) {
                    listOf("Ongoing","Completed","Upcoming").forEach { opt ->
                        DropdownMenuItem({ Text(opt, color = Color.White) }, { status = opt; ddOpen = false })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Sinopsis *", color = C.Muted, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                sinopsis, { sinopsis = it },
                Modifier.fillMaxWidth().height(130.dp),
                placeholder = { Text("Tulis sinopsis...", color = C.SubText) },
                shape = RoundedCornerShape(10.dp), colors = tfColors()
            )

            Spacer(Modifier.height(16.dp))

            if (error.isNotEmpty()) {
                Text(error, color = C.Red, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    error = ""
                    when {
                        judul.isBlank()    -> { error = "Judul tidak boleh kosong"; return@Button }
                        genre.isBlank()    -> { error = "Genre tidak boleh kosong"; return@Button }
                        sinopsis.isBlank() -> { error = "Sinopsis tidak boleh kosong"; return@Button }
                        !isEdit && file == null -> { error = "Poster harus dipilih"; return@Button }
                    }
                    val t = tahun.toIntOrNull() ?: 0
                    val e = episode.toIntOrNull() ?: 0
                    val r = rating.toDoubleOrNull() ?: 0.0
                    if (isEdit && existing != null) vm.updateDrakor(existing.id, judul, genre, t, e, r, sinopsis, status, file) { onBack() }
                    else file?.let { vm.createDrakor(judul, genre, t, e, r, sinopsis, status, it) { onBack() } }
                },
                Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = C.Primary),
                enabled = action !is UiState.Loading
            ) {
                if (action is UiState.Loading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                else
                    Text(if (isEdit) "Simpan Perubahan" else "Tambah Drakor", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onChange: (String) -> Unit, placeholder: String = "", kb: KeyboardType = KeyboardType.Text) {
    Text(label, color = C.Muted, fontSize = 13.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value, onChange, Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = C.SubText) },
        singleLine = (kb != KeyboardType.Text),
        keyboardOptions = KeyboardOptions(keyboardType = kb),
        shape = RoundedCornerShape(10.dp), colors = tfColors()
    )
}

@Composable
private fun tfColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = C.Primary, unfocusedBorderColor = C.Border,
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    cursorColor = C.Primary,
    focusedContainerColor = C.Surface, unfocusedContainerColor = C.Surface
)
