package com.example.miniproyecto3.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageStorageHelper {
    // Guarda un Bitmap de la cámara (Práctica 10) en un archivo permanente y devuelve su URI
    fun guardarBitmapEnArchivos(context: Context, bitmap: Bitmap): String {
        val archivo = File(context.filesDir, "foto_${System.currentTimeMillis()}.jpg")
        FileOutputStream(archivo).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }
        return Uri.fromFile(archivo).toString()
    }

    // Copia una imagen seleccionada de la galería (Práctica 11) al almacenamiento permanente
    fun copiarUriEnArchivos(context: Context, uriOrigen: Uri): String {
        val archivo = File(context.filesDir, "galeria_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uriOrigen)?.use { input ->
            FileOutputStream(archivo).use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(archivo).toString()
    }
}