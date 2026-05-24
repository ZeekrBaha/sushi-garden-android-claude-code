package com.baha.sushigarden.data.services.orders

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
abstract class SushiGardenDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
