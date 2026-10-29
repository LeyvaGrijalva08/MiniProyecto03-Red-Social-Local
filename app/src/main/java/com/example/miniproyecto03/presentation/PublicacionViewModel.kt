package com.example.miniproyecto3.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto3.data.AppDatabase
import com.example.miniproyecto3.data.Publicacion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).publicacionDao()

    // Lista reactiva conectada a Room que se actualiza automáticamente
    val listaPublicaciones: StateFlow<List<Publicacion>> = dao.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarPublicacion(titulo: String, descripcion: String, fotoUri: String, fecha: String) {
        viewModelScope.launch {
            val nueva = Publicacion(
                titulo = titulo,
                descripcion = descripcion,
                fotoUri = fotoUri,
                fecha = fecha
            )
            dao.insertar(nueva)
        }
    }

    fun eliminarPublicacion(publicacion: Publicacion) {
        viewModelScope.launch {
            dao.eliminar(publicacion)
        }
    }
}