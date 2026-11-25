package com.tecsup.avisoscampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AvisoEntity::class], version = 1, exportSchema = false)
abstract class AvisosDatabase : RoomDatabase() {
    abstract fun avisoDao(): AvisoDao

    companion object {
        fun crear(context: Context): AvisosDatabase =
            Room.databaseBuilder(context, AvisosDatabase::class.java, "avisos.db").build()
    }
}
