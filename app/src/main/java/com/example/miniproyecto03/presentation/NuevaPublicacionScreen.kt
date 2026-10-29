package com.example.miniproyecto3.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.miniproyecto3.data.ImageStorageHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPublicacionScreen(
    viewModel: PublicacionViewModel,
    onRegresar: () -> Unit
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf("") }

    // Fecha generada automáticamente con opción de cambiarla mediante DatePicker
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var fecha by remember { mutableStateOf(formatoFecha.format(Date())) }
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // 1. Lanzador de Cámara (Práctica 10: TakePicturePreview)
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoUri = ImageStorageHelper.guardarBitmapEnArchivos(context, bitmap)
        }
    }

    // 2. Lanzador de Permiso de Cámara en tiempo de ejecución (Práctica 10)
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            camaraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Lanzador de Galería (Práctica 11: GetContent)
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = ImageStorageHelper.copiarUriEnArchivos(context, uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Publicación") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la publicación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Componente para visualizar y/o seleccionar la Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de publicación") },
                trailingIcon = {
                    IconButton(onClick = { mostrarSelectorFecha = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Cambiar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones para capturar con Cámara o elegir de Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val status = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        )
                        if (status == PackageManager.PERMISSION_GRANTED) {
                            camaraLauncher.launch(null)
                        } else {
                            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara")
                }

                FilledTonalButton(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }
            }

            // Vista previa de la fotografía seleccionada/capturada
            if (fotoUri.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Vista previa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Toma una foto o selecciona una imagen de la galería")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón para guardar en Room
            Button(
                onClick = {
                    if (titulo.isBlank() || descripcion.isBlank() || fotoUri.isBlank()) {
                        Toast.makeText(
                            context,
                            "Completa título, descripción y fotografía",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.guardarPublicacion(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            fotoUri = fotoUri,
                            fecha = fecha
                        )
                        Toast.makeText(context, "Publicación guardada", Toast.LENGTH_SHORT).show()
                        onRegresar()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Publicación")
            }
        }

        // Selector de Fecha opcional
        if (mostrarSelectorFecha) {
            DatePickerDialog(
                onDismissRequest = { mostrarSelectorFecha = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val soloFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(Date(millis))
                                val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(Date())
                                fecha = "$soloFecha $horaActual"
                            }
                            mostrarSelectorFecha = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarSelectorFecha = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}