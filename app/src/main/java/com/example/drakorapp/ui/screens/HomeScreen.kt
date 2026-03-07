package com.example.drakorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.drakorapp.data.model.Drakor
import com.example.drakorapp.data.network.ApiClient
import com.example.drakorapp.ui.theme.C
import com.example.drakorapp.viewmodel.DrakorViewModel
import com.example.drakorapp.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: DrakorViewModel,
    onDetail: (String) -> Unit,
    onAdd: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val state by vm.listState.collectAsState()
    var search by remember { mutableStateOf("") }
    var selStatus by remember { mutableStateOf("") }
    var selGenre  by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadDrakors() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎬 DrakorDB", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = onProfile)  { Icon(Icons.Default.Person,   "Profil",      tint = Color.White) }
                    IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Pengaturan",  tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = C.Surface, titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd, containerColor = C.Primary, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Tambah")
            }
        },
        containerColor = C.BG
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))

            // Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it; vm.searchQuery.value = it; vm.loadDrakors() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari judul...", color = C.Muted) },
                leadingIcon  = { Icon(Icons.Default.Search, null, tint = C.Muted) },
                trailingIcon = if (search.isEmpty()) null else ({ IconButton({
                    search = ""; vm.searchQuery.value = ""; vm.loadDrakors()
                }) { Icon(Icons.Default.Clear, null, tint = C.Muted) } }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(8.dp))

            // Filter chips
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterPill(if (selStatus.isEmpty()) "Status" else selStatus, selStatus.isNotEmpty(), Modifier.weight(1f)) { showSheet = true }
                FilterPill(if (selGenre.isEmpty())  "Genre"  else selGenre,  selGenre.isNotEmpty(),  Modifier.weight(1f)) { showSheet = true }
                if (selStatus.isNotEmpty() || selGenre.isNotEmpty()) {
                    IconButton(onClick = {
                        selStatus = ""; selGenre = ""
                        vm.filterStatus.value = ""; vm.filterGenre.value = ""
                        vm.loadDrakors()
                    }) { Icon(Icons.Default.FilterListOff, null, tint = C.Muted, modifier = Modifier.size(20.dp)) }
                }
            }

            Spacer(Modifier.height(12.dp))

            when (val s = state) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = C.Primary)
                }
                is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.CloudOff, null, tint = C.Muted, modifier = Modifier.size(52.dp))
                        Text(s.message, color = C.Muted, fontSize = 13.sp)
                        Button(onClick = { vm.loadDrakors() }, colors = ButtonDefaults.buttonColors(containerColor = C.Primary)) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is UiState.Success -> {
                    val list = s.data
                    if (list.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MovieFilter, null, tint = C.Muted, modifier = Modifier.size(52.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Belum ada drakor", color = C.Muted)
                            }
                        }
                    } else {
                        Text("${list.size} drakor", color = C.Muted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(list, key = { it.id }) { d -> DrakorCard(d) { onDetail(d.id) } }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, containerColor = C.Card) {
            Column(Modifier.padding(20.dp)) {
                SheetSection("Filter Status", listOf("", "Ongoing", "Completed", "Upcoming"), selStatus) {
                    selStatus = it; vm.filterStatus.value = it; vm.loadDrakors(); showSheet = false
                }
                Spacer(Modifier.height(16.dp))
                SheetSection("Filter Genre", listOf("", "Romance", "Action", "Comedy", "Thriller", "Fantasy", "Historical", "Drama"), selGenre) {
                    selGenre = it; vm.filterGenre.value = it; vm.loadDrakors(); showSheet = false
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SheetSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    Spacer(Modifier.height(4.dp))
    options.forEach { opt ->
        Row(
            Modifier.fillMaxWidth().clickable { onSelect(opt) }.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected == opt, { onSelect(opt) }, colors = RadioButtonDefaults.colors(selectedColor = C.Primary))
            Spacer(Modifier.width(8.dp))
            Text(if (opt.isEmpty()) "Semua" else opt, color = Color.White)
        }
    }
}

@Composable
fun FilterPill(text: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(RoundedCornerShape(8.dp))
            .background(if (active) C.Primary.copy(.18f) else C.Card)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        Alignment.Center
    ) {
        Text(text, color = if (active) C.Primary else C.Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun DrakorCard(drakor: Drakor, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = C.Card),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            Box(Modifier.width(76.dp).height(106.dp).clip(RoundedCornerShape(10.dp)).background(C.Border)) {
                AsyncImage(
                    model = "${ApiClient.getBaseUrl()}drakors/${drakor.id}/poster",
                    contentDescription = drakor.judul,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(drakor.judul, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = C.Gold, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${drakor.rating}  ·  ${drakor.tahun}  ·  ${drakor.episode} eps", color = C.Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text(drakor.genre, color = C.Muted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text(drakor.sinopsis, color = C.SubText, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                StatusChip(drakor.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "Ongoing"   -> Color(0xFF1A3A2A) to C.Green
        "Completed" -> Color(0xFF1A2A3A) to C.Blue
        "Upcoming"  -> Color(0xFF3A2A1A) to C.Orange
        else        -> C.Border to C.Muted
    }
    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(status, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = C.Primary,
    unfocusedBorderColor    = C.Border,
    focusedTextColor        = Color.White,
    unfocusedTextColor      = Color.White,
    cursorColor             = C.Primary,
    focusedContainerColor   = C.Surface,
    unfocusedContainerColor = C.Surface
)
