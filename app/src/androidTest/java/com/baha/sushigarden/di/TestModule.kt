package com.baha.sushigarden.di

import android.content.Context
import androidx.room.Room
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.AuthService
import com.baha.sushigarden.data.services.auth.FakeAuthService
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import com.baha.sushigarden.data.services.catalog.LocalMenuRepository
import com.baha.sushigarden.data.services.catalog.MenuRepository
import com.baha.sushigarden.data.services.delivery.CourierSimulator
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.SushiGardenDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
@Module
object TestModule {
    @Provides @Singleton
    fun provideAuthService(): AuthService = FakeAuthService(
        preSeededUser = UserProfile(
            uid = "test-uid",
            name = "Тест Пользователь",
            email = "test@test.com",
            phone = "+7 900 123-45-67"
        )
    )

    @Provides @Singleton
    fun provideMenuRepository(): MenuRepository = LocalMenuRepository()

    @Provides @Singleton
    fun provideCartService(): CartService = InMemoryCartService()

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): SushiGardenDatabase =
        Room.inMemoryDatabaseBuilder(ctx, SushiGardenDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideOrderDao(db: SushiGardenDatabase): OrderDao = db.orderDao()

    @Provides @Singleton
    fun provideCourierSimulator(): CourierSimulator = CourierSimulator()
}
