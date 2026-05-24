package com.baha.sushigarden.di

import android.content.Context
import androidx.room.Room
import com.baha.sushigarden.data.services.auth.AuthService
import com.baha.sushigarden.data.services.auth.FirebaseAuthService
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import com.baha.sushigarden.data.services.catalog.LocalMenuRepository
import com.baha.sushigarden.data.services.catalog.MenuRepository
import com.baha.sushigarden.data.services.delivery.CourierSimulator
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.SushiGardenDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideAuthService(): AuthService = FirebaseAuthService()

    @Provides @Singleton
    fun provideMenuRepository(): MenuRepository = LocalMenuRepository()

    @Provides @Singleton
    fun provideCartService(): CartService = InMemoryCartService()

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): SushiGardenDatabase =
        Room.databaseBuilder(ctx, SushiGardenDatabase::class.java, "sushi_garden.db").build()

    @Provides
    fun provideOrderDao(db: SushiGardenDatabase): OrderDao = db.orderDao()

    @Provides @Singleton
    fun provideCourierSimulator(): CourierSimulator = CourierSimulator()
}
