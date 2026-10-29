package com.example.miniproyecto3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publicaciones")
data class Publicacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fotoUri: String,
    val fecha: String
)

@Dao
interface PublicacionDao {
    @Query("SELECT * FROM publicaciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Publicacion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(publicacion: Publicacion)

    @Delete
    suspend fun eliminar(publicacion: Publicacion)
}