package com.example.miniproyecto3.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.miniproyecto3.data.Publicacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: PublicacionViewModel,
    onIrANuevaPublicacion: () -> Unit
) {
    val publicaciones by viewModel.listaPublicaciones.collectAsState()
    // Estado para ver la foto en grande al tocarla (Reto Práctica 09)
    var fotoSeleccionadaZoom by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Red Social Local") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onIrANuevaPublicacion,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva Publicación") },
                text = { Text("Publicar") }
            )
        }
    ) { paddingValues ->
        if (publicaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay publicaciones aún. ¡Crea la primera!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(publicaciones) { publicacion ->
                    PublicacionCard(
                        publicacion = publicacion,
                        onFotoClick = { fotoSeleccionadaZoom = publicacion.fotoUri },
                        onDeleteClick = { viewModel.eliminarPublicacion(publicacion) }
                    )
                }
            }
        }

        // Diálogo emergente para ver la imagen en grande (Práctica 09)
        if (fotoSeleccionadaZoom != null) {
            Dialog(onDismissRequest = { fotoSeleccionadaZoom = null }) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    AsyncImage(
                        model = fotoSeleccionadaZoom,
                        contentDescription = "Vista ampliada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { fotoSeleccionadaZoom = null }
                    )
                }
            }
        }
    }
}

@Composable
fun PublicacionCard(
    publicacion: Publicacion,
    onFotoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = publicacion.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = publicacion.fecha,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fotografía renderizada con AsyncImage de Coil (Práctica 09 y 12)
            AsyncImage(
                model = publicacion.fotoUri,
                contentDescription = publicacion.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFotoClick() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = publicacion.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}