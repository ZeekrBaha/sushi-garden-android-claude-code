# Sushi Garden Android Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a pixel-perfect Android (Jetpack Compose + MVVM) port of Sushi Garden iOS — same Firebase Auth project, same local mock data, same Figma design, full UI-test coverage with screenshot capture + Figma diff on every screen.

**Architecture:** Jetpack Compose UI with MVVM (ViewModel + StateFlow), Hilt DI, Navigation Compose for bottom nav + stack, Room for order persistence, Firebase Auth KTX, Google Maps Compose for tracking. Every feature is a self-contained `features/<name>/` folder with Screen + ViewModel. Hilt `@TestInstallIn` swaps all real services for fakes in UI test builds.

**Tech Stack:** Kotlin 2.0, AGP 8.5.2, Compose BOM 2024.09.00, Hilt 2.51.1, Room 2.6.1, Navigation Compose 2.8.4, Firebase BOM 33.4.0, Maps Compose 6.1.0, Coil 2.7.0, JUnit4 + MockK, Compose UI Test

---

## File Structure

```
sushi-garden-android-claude-code/
  gradle/libs.versions.toml
  settings.gradle.kts
  build.gradle.kts
  app/
    build.gradle.kts
    google-services.json          ← git-ignored, user-supplied
    src/
      main/
        assets/fonts/             Sen-Regular.ttf · Sen-Bold.ttf · Mugesta.ttf (user-supplied)
        res/drawable/             product images extracted from Figma
        java/com/baha/sushigarden/
          ui/designsystem/        Color.kt · Typography.kt · Spacing.kt · Theme.kt
          ui/components/          PrimaryButton.kt · ProductCard.kt · StepperButton.kt
          features/auth/          AuthScreen.kt · AuthViewModel.kt
          features/catalog/       CatalogScreen.kt · CatalogViewModel.kt
          features/productdetail/ ProductDetailScreen.kt · ProductDetailViewModel.kt
          features/promotions/    PromotionsScreen.kt · PromotionsViewModel.kt
          features/cart/          CartScreen.kt · CartViewModel.kt
          features/checkout/      CheckoutScreen.kt · CheckoutViewModel.kt
          features/tracking/      TrackingScreen.kt · TrackingViewModel.kt
          features/orders/        OrdersScreen.kt · OrderDetailScreen.kt · OrdersViewModel.kt
          features/profile/       ProfileScreen.kt · ProfileViewModel.kt
          data/models/            Product.kt · CartItem.kt · AddOn.kt · Order.kt · UserProfile.kt · Courier.kt · DeliveryAddress.kt
          data/services/auth/     AuthService.kt · FirebaseAuthService.kt · FakeAuthService.kt
          data/services/catalog/  MenuRepository.kt · LocalMenuRepository.kt
          data/services/cart/     CartService.kt · InMemoryCartService.kt
          data/services/orders/   OrderDao.kt · SushiGardenDatabase.kt
          data/services/delivery/ CourierSimulator.kt
          di/                     AppModule.kt
          navigation/             NavGraph.kt · BottomNavBar.kt
          SushiGardenApp.kt
          MainActivity.kt
          UiState.kt
      androidTest/java/com/baha/sushigarden/
        HiltTestRunner.kt
        ScreenshotCapture.kt
        di/TestModule.kt
        features/auth/AuthScreenTest.kt
        features/catalog/CatalogScreenTest.kt
        features/productdetail/ProductDetailScreenTest.kt
        features/promotions/PromotionsScreenTest.kt
        features/cart/CartScreenTest.kt
        features/checkout/CheckoutScreenTest.kt
        features/tracking/TrackingScreenTest.kt
        features/orders/OrdersScreenTest.kt
        features/orders/OrderDetailScreenTest.kt
        features/profile/ProfileScreenTest.kt
      test/java/com/baha/sushigarden/
        features/auth/AuthViewModelTest.kt
        features/catalog/CatalogViewModelTest.kt
        features/cart/InMemoryCartServiceTest.kt
        features/orders/OrderDaoTest.kt
        features/delivery/CourierSimulatorTest.kt
        data/LocalMenuRepositoryTest.kt
```

---

## P0 — Scaffold

### Task P0.1: Gradle project structure

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (root)
- Create: `gradle/libs.versions.toml`
- Create: `app/build.gradle.kts`

- [ ] **Step 1: Write `gradle/libs.versions.toml`**

```toml
[versions]
agp = "8.5.2"
kotlin = "2.0.0"
compose-bom = "2024.09.00"
hilt = "2.51.1"
hilt-navigation-compose = "1.2.0"
room = "2.6.1"
navigation-compose = "2.8.4"
firebase-bom = "33.4.0"
maps-compose = "6.1.0"
coil = "2.7.0"
mockk = "1.13.12"
coroutines = "1.8.1"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version = "1.9.2" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation-compose" }
hilt-android-testing = { group = "com.google.dagger", name = "hilt-android-testing", version.ref = "hilt" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebase-bom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth-ktx" }
maps-compose = { group = "com.google.maps.android", name = "maps-compose", version.ref = "maps-compose" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
junit = { group = "junit", name = "junit", version = "4.13.2" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
androidx-test-runner = { group = "androidx.test", name = "runner", version = "1.6.2" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.8.6" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.8.6" }
core-ktx = { group = "androidx.core", name = "core-ktx", version = "1.13.1" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
google-services = { id = "com.google.gms.google-services", version = "4.4.2" }
```

- [ ] **Step 2: Write `settings.gradle.kts`**

```kotlin
pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "SushiGarden"
include(":app")
```

- [ ] **Step 3: Write root `build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
}
```

- [ ] **Step 4: Write `app/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.baha.sushigarden"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.baha.sushigarden"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.baha.sushigarden.HiltTestRunner"
        buildConfigField("boolean", "IS_UI_TEST", "false")
    }
    buildFeatures { compose = true; buildConfig = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    sourceSets["androidTest"].assets.srcDirs("src/androidTest/assets")
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.room.testing)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.test.runner)
    kapt(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
```

- [ ] **Step 5: Add `google-services.json` to `.gitignore`**

```
# .gitignore (add these lines)
app/google-services.json
app/src/main/assets/fonts/Mugesta.ttf
```

- [ ] **Step 6: Run sync**

```bash
cd ~/Desktop/llm-ai-projects/sushi-garden-android-claude-code
./gradlew dependencies --configuration releaseRuntimeClasspath
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add gradle/ settings.gradle.kts build.gradle.kts app/build.gradle.kts .gitignore
git commit -m "feat(p0): gradle project scaffold with version catalog"
```

---

### Task P0.2: Design system

**Files:**
- Create: `app/src/main/java/com/baha/sushigarden/ui/designsystem/Color.kt`
- Create: `app/src/main/java/com/baha/sushigarden/ui/designsystem/Typography.kt`
- Create: `app/src/main/java/com/baha/sushigarden/ui/designsystem/Spacing.kt`
- Create: `app/src/main/java/com/baha/sushigarden/ui/designsystem/Theme.kt`

- [ ] **Step 1: Write failing test**

```kotlin
// app/src/test/java/com/baha/sushigarden/DesignSystemTest.kt
package com.baha.sushigarden

import com.baha.sushigarden.ui.designsystem.SushiColors
import org.junit.Test
import org.junit.Assert.assertEquals

class DesignSystemTest {
    @Test fun backgroundColorToken() {
        assertEquals(0xFF0F0F11.toInt(), SushiColors.Background.value.toInt())
    }
    @Test fun accentRedToken() {
        assertEquals(0xFFEC1A35.toInt(), SushiColors.AccentRed.value.toInt())
    }
}
```

Run: `./gradlew :app:test --tests "*.DesignSystemTest"` → FAIL (SushiColors not defined)

- [ ] **Step 2: Write `Color.kt`**

```kotlin
package com.baha.sushigarden.ui.designsystem

import androidx.compose.ui.graphics.Color

object SushiColors {
    val Background   = Color(0xFF0F0F11)
    val CardSurface  = Color(0xFF292830)
    val TabBar       = Color(0xFF161616)
    val AccentRed    = Color(0xFFEC1A35)
    val PrimaryText  = Color(0xFFFFFFFF)
    val SecondaryText = Color(0xFF6C6C74)
    val IconInactive = Color(0xFF4C4C4C)
    val Divider      = Color(0xFF2A2A2A)
}
```

- [ ] **Step 3: Write `Typography.kt`**

```kotlin
package com.baha.sushigarden.ui.designsystem

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.baha.sushigarden.R

val SenFontFamily = FontFamily(
    Font(R.font.sen_regular, FontWeight.Normal),
    Font(R.font.sen_bold, FontWeight.Bold)
)
```

- [ ] **Step 4: Write `Spacing.kt`**

```kotlin
package com.baha.sushigarden.ui.designsystem

import androidx.compose.ui.unit.dp

object Spacing {
    val xs  = 4.dp
    val sm  = 8.dp
    val md  = 16.dp
    val lg  = 24.dp
    val xl  = 32.dp
    val cardCorner = 12.4.dp
}
```

- [ ] **Step 5: Write `Theme.kt`**

```kotlin
package com.baha.sushigarden.ui.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SushiColorScheme = darkColorScheme(
    background  = SushiColors.Background,
    surface     = SushiColors.CardSurface,
    primary     = SushiColors.AccentRed,
    onPrimary   = SushiColors.PrimaryText,
    onBackground = SushiColors.PrimaryText,
    onSurface   = SushiColors.PrimaryText,
    secondary   = SushiColors.SecondaryText,
    onSecondary = SushiColors.PrimaryText
)

@Composable
fun SushiGardenTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = SushiColorScheme, content = content)
}
```

- [ ] **Step 6: Add font resources**

Place `Sen-Regular.ttf` and `Sen-Bold.ttf` in `app/src/main/res/font/` renamed to `sen_regular.ttf` and `sen_bold.ttf`. Download from Google Fonts or copy from iOS project at `~/Desktop/llm-ai-projects/sushi-garden-ios/SushiGarden/DesignSystem/Fonts/`.

- [ ] **Step 7: Run tests**

```bash
./gradlew :app:test --tests "*.DesignSystemTest"
```
Expected: `BUILD SUCCESSFUL`, 2 tests pass.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/ui/ app/src/main/res/font/
git commit -m "feat(p0): design system with Figma tokens and Sen font"
```

---

### Task P0.3: Data models + UiState

**Files:**
- Create: `app/src/main/java/com/baha/sushigarden/UiState.kt`
- Create: `app/src/main/java/com/baha/sushigarden/data/models/` (all model files)

- [ ] **Step 1: Write `UiState.kt`**

```kotlin
package com.baha.sushigarden

sealed class UiState<out T> {
    data object Idle    : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

- [ ] **Step 2: Write `data/models/Models.kt`**

```kotlin
package com.baha.sushigarden.data.models

data class AddOn(val id: String, val name: String, val price: Int)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val weightGrams: Int,
    val categoryId: String,
    val imageRes: Int,
    val availableAddOns: List<AddOn> = emptyList()
)

data class Category(val id: String, val name: String)

data class CartItem(
    val product: Product,
    val quantity: Int,
    val selectedAddOns: List<AddOn> = emptyList()
) {
    val lineTotal: Int get() = (product.price + selectedAddOns.sumOf { it.price }) * quantity
}

data class CartState(val items: List<CartItem> = emptyList()) {
    val itemCount: Int get() = items.sumOf { it.quantity }
    val subtotal: Int   get() = items.sumOf { it.lineTotal }
    val deliveryFee: Int get() = if (items.isEmpty()) 0 else 199
    val serviceFee: Int  get() = if (items.isEmpty()) 0 else 49
    val total: Int       get() = subtotal + deliveryFee + serviceFee
}

data class DeliveryAddress(
    val street: String = "",
    val recipientName: String = "",
    val phone: String = "",
    val email: String = ""
)

data class OrderLine(val productName: String, val qty: Int, val lineTotal: Int)

data class Order(
    val id: String,
    val lines: List<OrderLine>,
    val subtotal: Int,
    val deliveryFee: Int,
    val serviceFee: Int,
    val total: Int,
    val address: DeliveryAddress,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String = ""
)

data class Courier(
    val name: String = "Максим Винокур",
    val title: String = "Курьер",
    val avatarRes: Int = 0
)
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/UiState.kt \
        app/src/main/java/com/baha/sushigarden/data/
git commit -m "feat(p0): data models and UiState"
```

---

### Task P0.4: Screenshot capture helper + HiltTestRunner

**Files:**
- Create: `app/src/androidTest/java/com/baha/sushigarden/ScreenshotCapture.kt`
- Create: `app/src/androidTest/java/com/baha/sushigarden/HiltTestRunner.kt`

- [ ] **Step 1: Write `ScreenshotCapture.kt`**

```kotlin
package com.baha.sushigarden

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ComposeContentTestRule
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream

fun ComposeContentTestRule.captureAndSaveScreenshot(name: String) {
    val bitmap = onRoot().captureToImage().asAndroidBitmap()
    val ctx = InstrumentationRegistry.getInstrumentation().targetContext
    val dir = File(ctx.getExternalFilesDir(null), "screenshots")
    dir.mkdirs()
    val file = File(dir, "$name.png")
    FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    println("SCREENSHOT_SAVED: ${file.absolutePath}")
}
```

- [ ] **Step 2: Write `HiltTestRunner.kt`**

```kotlin
package com.baha.sushigarden

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/androidTest/
git commit -m "feat(p0): screenshot helper and Hilt test runner"
```

---

### P0 figma-android-diff check

```
After running the emulator and verifying the app launches:
  adb exec-out screencap -p > docs/screenshots/android/p0_scaffold.png

Use mcp__figma__view_node:
  fileKey = wOK1MMzuJZF3pIOZhGHpY9
  nodeId  = (top-level app frame from Figma canvas)

Compare: background color #0F0F11 visible, Sen font loaded correctly.
Fix any divergence before advancing to P1.
```

---

## P1 — Shell + Auth

### Task P1.1: Service interfaces + implementations

**Files:**
- Create: `data/services/auth/AuthService.kt`
- Create: `data/services/auth/FirebaseAuthService.kt`
- Create: `data/services/auth/FakeAuthService.kt`

- [ ] **Step 1: Write failing unit test**

```kotlin
// app/src/test/java/com/baha/sushigarden/features/auth/FakeAuthServiceTest.kt
package com.baha.sushigarden.features.auth

import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.FakeAuthService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class FakeAuthServiceTest {
    private val user = UserProfile("uid1", "Иван", "ivan@test.com")
    private val svc  = FakeAuthService(preSeededUser = user)

    @Test fun currentUser_returnsSeededUser() = runTest {
        assertEquals(user, svc.currentUser.first())
    }
    @Test fun signIn_succeeds() = runTest {
        val result = svc.signIn("ivan@test.com", "password")
        assertEquals(user, result)
    }
    @Test fun signOut_clearsUser() = runTest {
        svc.signOut()
        assertNull(svc.currentUser.first())
    }
}
```

Run: `./gradlew :app:test --tests "*.FakeAuthServiceTest"` → FAIL

- [ ] **Step 2: Write `AuthService.kt`**

```kotlin
package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUser: Flow<UserProfile?>
    suspend fun signUp(email: String, password: String, name: String): UserProfile
    suspend fun signIn(email: String, password: String): UserProfile
    suspend fun signOut()
}
```

- [ ] **Step 3: Write `FakeAuthService.kt`**

```kotlin
package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthService(preSeededUser: UserProfile? = null) : AuthService {
    private val _user = MutableStateFlow(preSeededUser)
    override val currentUser: Flow<UserProfile?> = _user

    override suspend fun signUp(email: String, password: String, name: String): UserProfile {
        val user = UserProfile(uid = "fake-uid", name = name, email = email)
        _user.value = user
        return user
    }

    override suspend fun signIn(email: String, password: String): UserProfile {
        val user = _user.value ?: UserProfile("fake-uid", "Тест", email)
        _user.value = user
        return user
    }

    override suspend fun signOut() { _user.value = null }
}
```

- [ ] **Step 4: Write `FirebaseAuthService.kt`**

```kotlin
package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthService : AuthService {
    private val auth = FirebaseAuth.getInstance()

    override val currentUser: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            val u = a.currentUser
            trySend(u?.let { UserProfile(it.uid, it.displayName ?: "", it.email ?: "") })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(email: String, password: String, name: String): UserProfile {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest { displayName = name }
        user.updateProfile(profileUpdates).await()
        return UserProfile(user.uid, name, email)
    }

    override suspend fun signIn(email: String, password: String): UserProfile {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user!!
        return UserProfile(user.uid, user.displayName ?: "", user.email ?: "")
    }

    override suspend fun signOut() { auth.signOut() }
}
```

- [ ] **Step 5: Run tests**

```bash
./gradlew :app:test --tests "*.FakeAuthServiceTest"
```
Expected: 3 tests pass.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/data/services/auth/
git commit -m "feat(p1): AuthService interface + Firebase + Fake implementations"
```

---

### Task P1.2: Hilt modules

**Files:**
- Create: `di/AppModule.kt`
- Create: `androidTest/.../di/TestModule.kt`

- [ ] **Step 1: Write `AppModule.kt`**

```kotlin
package com.baha.sushigarden.di

import com.baha.sushigarden.data.services.auth.AuthService
import com.baha.sushigarden.data.services.auth.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideAuthService(): AuthService = FirebaseAuthService()
}
```

- [ ] **Step 2: Write `TestModule.kt`**

```kotlin
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
    @Provides @Singleton fun provideMenuRepository(): MenuRepository = LocalMenuRepository()
    @Provides @Singleton fun provideCartService(): CartService = InMemoryCartService()
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): SushiGardenDatabase =
        Room.inMemoryDatabaseBuilder(ctx, SushiGardenDatabase::class.java)
            .allowMainThreadQueries().build()
    @Provides fun provideOrderDao(db: SushiGardenDatabase): OrderDao = db.orderDao()
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/di/ \
        app/src/androidTest/java/com/baha/sushigarden/di/
git commit -m "feat(p1): Hilt AppModule + TestModule"
```

---

### Task P1.3: App shell — SushiGardenApp + MainActivity

**Files:**
- Create: `SushiGardenApp.kt`
- Create: `MainActivity.kt`
- Create: `navigation/NavGraph.kt`
- Create: `navigation/BottomNavBar.kt`

- [ ] **Step 1: Write `SushiGardenApp.kt`**

```kotlin
package com.baha.sushigarden

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SushiGardenApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
```

- [ ] **Step 2: Write `MainActivity.kt`**

```kotlin
package com.baha.sushigarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.baha.sushigarden.navigation.NavGraph
import com.baha.sushigarden.ui.designsystem.SushiGardenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { SushiGardenTheme { NavGraph() } }
    }
}
```

- [ ] **Step 3: Write `navigation/NavGraph.kt`**

```kotlin
package com.baha.sushigarden.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baha.sushigarden.features.auth.AuthScreen
import com.baha.sushigarden.features.auth.AuthViewModel
import com.baha.sushigarden.features.catalog.CatalogScreen
import com.baha.sushigarden.features.cart.CartScreen
import com.baha.sushigarden.features.orders.OrdersScreen
import com.baha.sushigarden.features.profile.ProfileScreen
import com.baha.sushigarden.features.promotions.PromotionsScreen

sealed class Screen(val route: String) {
    data object Auth     : Screen("auth")
    data object Catalog  : Screen("catalog")
    data object Promos   : Screen("promos")
    data object Orders   : Screen("orders")
    data object Cart     : Screen("cart")
    data object Profile  : Screen("profile")
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(id: String) = "product/$id"
    }
    data object Checkout : Screen("checkout")
    data object Tracking : Screen("tracking")
    data object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(id: String) = "order/$id"
    }
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn = authViewModel.isLoggedIn.collectAsState(initial = false).value

    if (!isLoggedIn) {
        AuthScreen(onAuthSuccess = { navController.navigate(Screen.Catalog.route) {
            popUpTo(Screen.Auth.route) { inclusive = true }
        }})
        return
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        NavHost(navController, startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(padding)) {
            composable(Screen.Catalog.route)  { CatalogScreen(navController) }
            composable(Screen.Promos.route)   { PromotionsScreen() }
            composable(Screen.Orders.route)   { OrdersScreen(navController) }
            composable(Screen.Cart.route)     { CartScreen(navController) }
            composable(Screen.Profile.route)  { ProfileScreen(navController) }
            composable(Screen.ProductDetail.route) {
                val id = it.arguments?.getString("productId") ?: ""
                com.baha.sushigarden.features.productdetail.ProductDetailScreen(id, navController)
            }
            composable(Screen.Checkout.route) {
                com.baha.sushigarden.features.checkout.CheckoutScreen(navController)
            }
            composable(Screen.Tracking.route) {
                com.baha.sushigarden.features.tracking.TrackingScreen(navController)
            }
            composable(Screen.OrderDetail.route) {
                val id = it.arguments?.getString("orderId") ?: ""
                com.baha.sushigarden.features.orders.OrderDetailScreen(id, navController)
            }
        }
    }
}
```

- [ ] **Step 4: Write `navigation/BottomNavBar.kt`**

```kotlin
package com.baha.sushigarden.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.baha.sushigarden.ui.designsystem.SushiColors

data class BottomNavItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Catalog, "Каталог",  Icons.Default.Home),
    BottomNavItem(Screen.Promos,  "Акции",    Icons.Default.LocalOffer),
    BottomNavItem(Screen.Orders,  "Заказы",   Icons.Default.Receipt),
    BottomNavItem(Screen.Cart,    "Корзина",  Icons.Default.ShoppingCart),
    BottomNavItem(Screen.Profile, "Профиль",  Icons.Default.Person)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    NavigationBar(containerColor = SushiColors.TabBar) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = current == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Catalog.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = SushiColors.AccentRed,
                    selectedTextColor   = SushiColors.AccentRed,
                    unselectedIconColor = SushiColors.IconInactive,
                    unselectedTextColor = SushiColors.IconInactive,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/SushiGardenApp.kt \
        app/src/main/java/com/baha/sushigarden/MainActivity.kt \
        app/src/main/java/com/baha/sushigarden/navigation/
git commit -m "feat(p1): app shell, MainActivity, NavGraph, BottomNavBar"
```

---

### Task P1.4: AuthViewModel + AuthScreen

**Files:**
- Create: `features/auth/AuthViewModel.kt`
- Create: `features/auth/AuthScreen.kt`

- [ ] **Step 1: Write failing unit test**

```kotlin
// app/src/test/java/com/baha/sushigarden/features/auth/AuthViewModelTest.kt
package com.baha.sushigarden.features.auth

import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.FakeAuthService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val service = FakeAuthService()
    private val vm = AuthViewModel(service)

    @Test fun register_updatesStateToSuccess() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@test.com")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(true)
        vm.register()
        assertTrue(vm.uiState.value is UiState.Success)
    }

    @Test fun register_requiresConsent() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@test.com")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(false)
        assertFalse(vm.canRegister.value)
    }

    @Test fun login_updatesStateToSuccess() = runTest(dispatcher) {
        vm.onEmailChange("test@test.com")
        vm.onPasswordChange("password")
        vm.login()
        assertTrue(vm.uiState.value is UiState.Success)
    }
}
```

Run: `./gradlew :app:test --tests "*.AuthViewModelTest"` → FAIL

- [ ] **Step 2: Write `AuthViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = authService.currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val uiState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)

    val name     = MutableStateFlow("")
    val email    = MutableStateFlow("")
    val password = MutableStateFlow("")
    val consent  = MutableStateFlow(false)
    val showPassword = MutableStateFlow(false)

    val canRegister: StateFlow<Boolean> = combine(name, email, password, consent) { n, e, p, c ->
        n.isNotBlank() && e.contains("@") && p.length >= 6 && c
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun onNameChange(v: String)     { name.value = v }
    fun onEmailChange(v: String)    { email.value = v }
    fun onPasswordChange(v: String) { password.value = v }
    fun onConsentChange(v: Boolean) { consent.value = v }
    fun togglePasswordVisibility()  { showPassword.value = !showPassword.value }

    fun register() = viewModelScope.launch {
        uiState.value = UiState.Loading
        uiState.value = try {
            UiState.Success(authService.signUp(email.value, password.value, name.value))
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Ошибка регистрации")
        }
    }

    fun login() = viewModelScope.launch {
        uiState.value = UiState.Loading
        uiState.value = try {
            UiState.Success(authService.signIn(email.value, password.value))
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Неверные данные")
        }
    }
}
```

- [ ] **Step 3: Write `AuthScreen.kt`**

```kotlin
package com.baha.sushigarden.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baha.sushigarden.UiState
import com.baha.sushigarden.ui.designsystem.SushiColors
import com.baha.sushigarden.ui.designsystem.SushiGardenTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    var isRegister by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val consent by viewModel.consent.collectAsState()
    val showPw by viewModel.showPassword.collectAsState()
    val canReg by viewModel.canRegister.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onAuthSuccess()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(SushiColors.Background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        Row {
            TextButton(
                onClick = { isRegister = true },
                modifier = Modifier.testTag("tab_register")
            ) {
                Text("Регистрация",
                    color = if (isRegister) SushiColors.AccentRed else SushiColors.SecondaryText)
            }
            TextButton(
                onClick = { isRegister = false },
                modifier = Modifier.testTag("tab_login")
            ) {
                Text("Войти",
                    color = if (!isRegister) SushiColors.AccentRed else SushiColors.SecondaryText)
            }
        }
        Spacer(Modifier.height(24.dp))

        if (isRegister) {
            OutlinedTextField(
                value = name, onValueChange = viewModel::onNameChange,
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth().testTag("field_name"),
                colors = authFieldColors()
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email, onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("field_email"),
            colors = authFieldColors()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password, onValueChange = viewModel::onPasswordChange,
            label = { Text("Пароль") },
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = viewModel::togglePasswordVisibility,
                    modifier = Modifier.testTag("btn_toggle_password")) {
                    Icon(if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                        tint = SushiColors.SecondaryText)
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("field_password"),
            colors = authFieldColors()
        )

        if (isRegister) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Checkbox(
                    checked = consent, onCheckedChange = viewModel::onConsentChange,
                    modifier = Modifier.testTag("checkbox_consent"),
                    colors = CheckboxDefaults.colors(checkedColor = SushiColors.AccentRed)
                )
                Text("Я согласен с условиями", color = SushiColors.SecondaryText)
            }
        }

        if (uiState is UiState.Error) {
            Text((uiState as UiState.Error).message,
                color = SushiColors.AccentRed, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { if (isRegister) viewModel.register() else viewModel.login() },
            enabled = if (isRegister) canReg else email.contains("@") && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_auth_submit"),
            colors = ButtonDefaults.buttonColors(
                containerColor = SushiColors.AccentRed,
                disabledContainerColor = SushiColors.AccentRed.copy(alpha = 0.4f)
            )
        ) {
            if (uiState is UiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text(if (isRegister) "Зарегистрироваться" else "Войти")
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor    = SushiColors.PrimaryText,
    unfocusedTextColor  = SushiColors.PrimaryText,
    focusedBorderColor  = SushiColors.AccentRed,
    unfocusedBorderColor = SushiColors.SecondaryText,
    cursorColor         = SushiColors.AccentRed,
    focusedLabelColor   = SushiColors.AccentRed,
    unfocusedLabelColor = SushiColors.SecondaryText
)
```

- [ ] **Step 4: Run unit tests**

```bash
./gradlew :app:test --tests "*.AuthViewModelTest"
```
Expected: 3 tests pass.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/auth/
git commit -m "feat(p1): AuthViewModel + AuthScreen"
```

---

### Task P1.5: Auth UI tests

**Files:**
- Create: `androidTest/.../features/auth/AuthScreenTest.kt`

- [ ] **Step 1: Write `AuthScreenTest.kt`**

```kotlin
package com.baha.sushigarden.features.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class AuthScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() { hiltRule.inject() }

    @Test fun registerSuccess() {
        // FakeAuthService pre-seeds a user; toggling to register flow
        composeRule.onNodeWithTag("tab_register").performClick()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("ivan@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        composeRule.onNodeWithTag("checkbox_consent").performClick()
        composeRule.captureAndSaveScreenshot("auth_register_filled")
        composeRule.onNodeWithTag("btn_auth_submit").performClick()
        composeRule.waitForIdle()
        // After success navigates away from auth; bottom nav should be visible
        composeRule.onNodeWithTag("btn_auth_submit").assertDoesNotExist()
    }

    @Test fun registerDisabledWithoutConsent() {
        composeRule.onNodeWithTag("tab_register").performClick()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("ivan@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        // consent not checked
        composeRule.onNodeWithTag("btn_auth_submit").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("auth_register_no_consent")
    }

    @Test fun registerInvalidEmail() {
        composeRule.onNodeWithTag("tab_register").performClick()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("notanemail")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        composeRule.onNodeWithTag("checkbox_consent").performClick()
        composeRule.onNodeWithTag("btn_auth_submit").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("auth_register_invalid_email")
    }

    @Test fun registerShortPassword() {
        composeRule.onNodeWithTag("tab_register").performClick()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("ivan@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("123")
        composeRule.onNodeWithTag("checkbox_consent").performClick()
        composeRule.onNodeWithTag("btn_auth_submit").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("auth_register_short_password")
    }

    @Test fun toggleToLogin() {
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.captureAndSaveScreenshot("auth_login_empty")
        composeRule.onNodeWithTag("field_name").assertDoesNotExist()
    }

    @Test fun loginSuccess() {
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.onNodeWithTag("field_email").performTextInput("test@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("password")
        composeRule.captureAndSaveScreenshot("auth_login_filled")
        composeRule.onNodeWithTag("btn_auth_submit").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_auth_submit").assertDoesNotExist()
    }

    @Test fun loginWrongCredentials() {
        // FakeAuthService always succeeds — test that UI doesn't crash on error
        // swap to a failing fake by overriding in the test if needed
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.onNodeWithTag("field_email").performTextInput("wrong@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("wrong")
        composeRule.captureAndSaveScreenshot("auth_login_wrong")
        // Button should be enabled (fields filled)
        composeRule.onNodeWithTag("btn_auth_submit").assertIsEnabled()
    }
}
```

- [ ] **Step 2: Run UI tests on emulator**

```bash
./gradlew :app:connectedAndroidTest --tests "*.AuthScreenTest"
```
Expected: 7 tests pass.

- [ ] **Step 3: Pull screenshots**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
```

- [ ] **Step 4: Commit**

```bash
git add app/src/androidTest/java/com/baha/sushigarden/features/auth/
git commit -m "feat(p1): AuthScreen UI tests with screenshot capture"
```

---

### P1 figma-android-diff check

```
For each captured screenshot (auth_register_filled, auth_login_empty):
  Use mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9
  nodeId = (Регистрация frame node-id from Figma)

Compare:
  - Background #0F0F11
  - Input border color when focused (#EC1A35)
  - Button color #EC1A35
  - Font: Sen, correct sizes
  - Consent checkbox visible (register only)

Fix divergences before P2.
```

---

## P2 — Catalog + Product Detail

### Task P2.1: MenuRepository + CartService

**Files:**
- Create: `data/services/catalog/MenuRepository.kt`
- Create: `data/services/catalog/LocalMenuRepository.kt`
- Create: `data/services/cart/CartService.kt`
- Create: `data/services/cart/InMemoryCartService.kt`

- [ ] **Step 1: Write failing unit tests**

```kotlin
// app/src/test/java/com/baha/sushigarden/data/LocalMenuRepositoryTest.kt
package com.baha.sushigarden.data

import com.baha.sushigarden.data.services.catalog.LocalMenuRepository
import org.junit.Test
import org.junit.Assert.*

class LocalMenuRepositoryTest {
    private val repo = LocalMenuRepository()

    @Test fun hasFiveCategories() {
        assertEquals(5, repo.getCategories().size)
    }
    @Test fun hikariIsInSushi() {
        val cat = repo.getCategories().first { it.id == "sushi" }
        val items = repo.getProducts(cat.id)
        assertTrue(items.any { it.name == "Хикари" })
    }
    @Test fun hikariPrice620() {
        val item = repo.getAllProducts().first { it.name == "Хикари" }
        assertEquals(620, item.price)
    }
    @Test fun addOnsHaveThreeItems() {
        val item = repo.getAllProducts().first()
        assertEquals(3, item.availableAddOns.size)
    }
}
```

```kotlin
// app/src/test/java/com/baha/sushigarden/features/cart/InMemoryCartServiceTest.kt
package com.baha.sushigarden.features.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class InMemoryCartServiceTest {
    private val svc = InMemoryCartService()
    private val product = Product("p1", "Хикари", 620, 255, "sushi", 0,
        listOf(AddOn("a1", "Васаби", 60), AddOn("a2", "Имбирь", 60)))

    @Test fun addItem_incrementsCount() = runTest {
        svc.addItem(product)
        assertEquals(1, svc.cartState.first().itemCount)
    }
    @Test fun addItemTwice_qty2() = runTest {
        svc.addItem(product); svc.addItem(product)
        assertEquals(2, svc.cartState.first().items.first().quantity)
    }
    @Test fun removeItem_decrementsQty() = runTest {
        svc.addItem(product); svc.addItem(product)
        svc.removeItem(product.id)
        assertEquals(1, svc.cartState.first().items.first().quantity)
    }
    @Test fun removeItem_removesWhenQtyOne() = runTest {
        svc.addItem(product); svc.removeItem(product.id)
        assertTrue(svc.cartState.first().items.isEmpty())
    }
    @Test fun totalIncludesAddOns() = runTest {
        val addOn = AddOn("a1", "Васаби", 60)
        svc.addItem(product, listOf(addOn))
        assertEquals(620 + 60 + 199 + 49, svc.cartState.first().total)
    }
    @Test fun clearCart_emptyState() = runTest {
        svc.addItem(product); svc.clearCart()
        assertEquals(0, svc.cartState.first().itemCount)
    }
}
```

Run: `./gradlew :app:test --tests "*.LocalMenuRepositoryTest" --tests "*.InMemoryCartServiceTest"` → FAIL

- [ ] **Step 2: Write `MenuRepository.kt`**

```kotlin
package com.baha.sushigarden.data.services.catalog

import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product

interface MenuRepository {
    fun getCategories(): List<Category>
    fun getProducts(categoryId: String): List<Product>
    fun getProduct(id: String): Product?
    fun getAllProducts(): List<Product>
}
```

- [ ] **Step 3: Write `LocalMenuRepository.kt`**

```kotlin
package com.baha.sushigarden.data.services.catalog

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.R

class LocalMenuRepository : MenuRepository {
    private val addOns = listOf(
        AddOn("ao1", "Васаби",        60),
        AddOn("ao2", "Имбирь",        60),
        AddOn("ao3", "Соевый соус",   60)
    )

    private val categories = listOf(
        Category("sushi",   "Суши"),
        Category("rolls",   "Роллы"),
        Category("hot",     "Горячие роллы"),
        Category("salads",  "Салаты"),
        Category("wok",     "WOK")
    )

    private val products = listOf(
        Product("p1", "Хикари",          620, 255, "sushi",  R.drawable.product_hikari,   addOns),
        Product("p2", "Лос-Анджелес",    707, 285, "rolls",  R.drawable.product_losangeles, addOns),
        Product("p3", "Айдахо маки",     810, 285, "rolls",  R.drawable.product_idaho,    addOns),
        Product("p4", "Осака маки",      740, 275, "sushi",  R.drawable.product_osaka,    addOns),
        Product("p5", "Филадельфия",     850, 300, "rolls",  R.drawable.product_losangeles, addOns),
        Product("p6", "Унаги маки",      780, 270, "hot",    R.drawable.product_hikari,   addOns),
        Product("p7", "Спайси тунец",    720, 260, "hot",    R.drawable.product_osaka,    addOns),
        Product("p8", "Греческий салат", 390, 200, "salads", R.drawable.product_idaho,    addOns),
        Product("p9", "WOK с курицей",   490, 350, "wok",    R.drawable.product_losangeles, addOns),
        Product("p10","WOK с говядиной", 540, 350, "wok",    R.drawable.product_osaka,    addOns)
    )

    override fun getCategories() = categories
    override fun getProducts(categoryId: String) = products.filter { it.categoryId == categoryId }
    override fun getProduct(id: String) = products.find { it.id == id }
    override fun getAllProducts() = products
}
```

- [ ] **Step 4: Write `CartService.kt`**

```kotlin
package com.baha.sushigarden.data.services.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.models.Product
import kotlinx.coroutines.flow.StateFlow

interface CartService {
    val cartState: StateFlow<CartState>
    fun addItem(product: Product, addOns: List<AddOn> = emptyList())
    fun removeItem(productId: String)
    fun clearCart()
}
```

- [ ] **Step 5: Write `InMemoryCartService.kt`**

```kotlin
package com.baha.sushigarden.data.services.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.CartItem
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryCartService : CartService {
    private val _state = MutableStateFlow(CartState())
    override val cartState: StateFlow<CartState> = _state.asStateFlow()

    override fun addItem(product: Product, addOns: List<AddOn>) {
        val items = _state.value.items.toMutableList()
        val existing = items.indexOfFirst { it.product.id == product.id }
        if (existing >= 0) {
            items[existing] = items[existing].copy(quantity = items[existing].quantity + 1)
        } else {
            items.add(CartItem(product, 1, addOns))
        }
        _state.value = CartState(items)
    }

    override fun removeItem(productId: String) {
        val items = _state.value.items.toMutableList()
        val idx = items.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            val item = items[idx]
            if (item.quantity > 1) items[idx] = item.copy(quantity = item.quantity - 1)
            else items.removeAt(idx)
        }
        _state.value = CartState(items)
    }

    override fun clearCart() { _state.value = CartState() }
}
```

- [ ] **Step 6: Update `AppModule.kt` to add new bindings**

```kotlin
// Add to AppModule.kt
@Provides @Singleton fun provideMenuRepository(): MenuRepository = LocalMenuRepository()
@Provides @Singleton fun provideCartService(): CartService = InMemoryCartService()
```

- [ ] **Step 7: Run unit tests**

```bash
./gradlew :app:test --tests "*.LocalMenuRepositoryTest" --tests "*.InMemoryCartServiceTest"
```
Expected: 10 tests pass.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/data/services/catalog/ \
        app/src/main/java/com/baha/sushigarden/data/services/cart/
git commit -m "feat(p2): MenuRepository, CartService, InMemoryCartService"
```

---

### Task P2.2: CatalogViewModel + CatalogScreen

**Files:**
- Create: `features/catalog/CatalogViewModel.kt`
- Create: `features/catalog/CatalogScreen.kt`

- [ ] **Step 1: Write failing unit test**

```kotlin
// app/src/test/java/com/baha/sushigarden/features/catalog/CatalogViewModelTest.kt
package com.baha.sushigarden.features.catalog

import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import com.baha.sushigarden.data.services.catalog.LocalMenuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val repo = LocalMenuRepository()
    private val cart = InMemoryCartService()
    private val vm = CatalogViewModel(repo, cart)

    @Test fun initialState_loadsCategories() = runTest(dispatcher) {
        assertTrue(vm.uiState.value is UiState.Success)
        assertEquals(5, (vm.uiState.value as UiState.Success).data.size)
    }
    @Test fun selectCategory_filtersProducts() = runTest(dispatcher) {
        vm.selectCategory("rolls")
        assertTrue(vm.products.value.isNotEmpty())
        assertTrue(vm.products.value.all { it.categoryId == "rolls" })
    }
    @Test fun cartBadge_updatesOnAdd() = runTest(dispatcher) {
        val product = repo.getAllProducts().first()
        cart.addItem(product)
        assertEquals(1, vm.cartItemCount.first())
    }
}
```

Run: `./gradlew :app:test --tests "*.CatalogViewModelTest"` → FAIL

- [ ] **Step 2: Write `CatalogViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.catalog.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartService: CartService
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("sushi")

    val uiState: StateFlow<UiState<List<Category>>> = flow {
        emit(UiState.Success(menuRepository.getCategories()))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    val products: StateFlow<List<Product>> = _selectedCategory.map { catId ->
        menuRepository.getProducts(catId)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val cartItemCount: StateFlow<Int> = cartService.cartState
        .map { it.itemCount }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val selectedCategoryId: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(id: String) { _selectedCategory.value = id }
}
```

- [ ] **Step 3: Write `CatalogScreen.kt`**

```kotlin
package com.baha.sushigarden.features.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCat by viewModel.selectedCategoryId.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(SushiColors.Background)
    ) {
        // Address header
        Text(
            text = "ул. Пушкина, 10",
            color = SushiColors.PrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(Spacing.md).testTag("catalog_address")
        )

        // Category pills
        if (uiState is UiState.Success) {
            val categories = (uiState as UiState.Success).data
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md)
                    .testTag("catalog_categories"),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                categories.forEach { cat ->
                    val selected = cat.id == selectedCat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) SushiColors.AccentRed else SushiColors.CardSurface,
                        modifier = Modifier
                            .clickable { viewModel.selectCategory(cat.id) }
                            .testTag("category_${cat.id}")
                    ) {
                        Text(
                            cat.name,
                            color = SushiColors.PrimaryText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Product grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.testTag("catalog_grid")
        ) {
            items(products) { product ->
                ProductCard(product) {
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Spacing.cardCorner),
        color = SushiColors.CardSurface,
        modifier = Modifier.clickable(onClick = onClick).testTag("product_${product.id}")
    ) {
        Column {
            AsyncImage(
                model = product.imageRes,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(140.dp).clip(
                    RoundedCornerShape(topStart = Spacing.cardCorner, topEnd = Spacing.cardCorner)
                )
            )
            Column(Modifier.padding(Spacing.sm)) {
                Text(product.name, color = SushiColors.PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${product.weightGrams}г", color = SushiColors.SecondaryText, fontSize = 12.sp)
                Text("${product.price}₽", color = SushiColors.AccentRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
```

- [ ] **Step 4: Run unit tests**

```bash
./gradlew :app:test --tests "*.CatalogViewModelTest"
```
Expected: 3 tests pass.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/catalog/
git commit -m "feat(p2): CatalogViewModel + CatalogScreen"
```

---

### Task P2.3: ProductDetailViewModel + ProductDetailScreen

**Files:**
- Create: `features/productdetail/ProductDetailViewModel.kt`
- Create: `features/productdetail/ProductDetailScreen.kt`

- [ ] **Step 1: Write `ProductDetailViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.productdetail

import androidx.lifecycle.ViewModel
import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.catalog.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartService: CartService
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    private val _selectedAddOns = MutableStateFlow<Set<String>>(emptySet())
    val selectedAddOns = _selectedAddOns.asStateFlow()

    fun loadProduct(id: String) { _product.value = menuRepository.getProduct(id) }
    fun increment() { _quantity.value++ }
    fun decrement() { if (_quantity.value > 1) _quantity.value-- }
    fun toggleAddOn(addOnId: String) {
        _selectedAddOns.value = _selectedAddOns.value.toMutableSet().also {
            if (it.contains(addOnId)) it.remove(addOnId) else it.add(addOnId)
        }
    }
    fun addToCart() {
        val p = _product.value ?: return
        val addOns = p.availableAddOns.filter { _selectedAddOns.value.contains(it.id) }
        repeat(_quantity.value) { cartService.addItem(p, addOns) }
    }
}
```

- [ ] **Step 2: Write `ProductDetailScreen.kt`**

```kotlin
package com.baha.sushigarden.features.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) { viewModel.loadProduct(productId) }
    val product by viewModel.product.collectAsState()
    val qty by viewModel.quantity.collectAsState()
    val selectedAddOns by viewModel.selectedAddOns.collectAsState()

    product?.let { p ->
        Column(Modifier.fillMaxSize().background(SushiColors.Background)) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.testTag("btn_back")) {
                Icon(Icons.Default.ArrowBack, "Назад", tint = SushiColors.PrimaryText)
            }

            AsyncImage(
                model = p.imageRes,
                contentDescription = p.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(240.dp)
                    .clip(RoundedCornerShape(bottomStart = Spacing.cardCorner, bottomEnd = Spacing.cardCorner))
            )

            Column(Modifier.padding(Spacing.md)) {
                Text(p.name, color = SushiColors.PrimaryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${p.weightGrams}г", color = SushiColors.SecondaryText, fontSize = 14.sp)
                Spacer(Modifier.height(Spacing.md))

                Text("Добавки", color = SushiColors.SecondaryText, fontSize = 14.sp)
                p.availableAddOns.forEach { addOn ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("addon_${addOn.id}")
                    ) {
                        Checkbox(
                            checked = selectedAddOns.contains(addOn.id),
                            onCheckedChange = { viewModel.toggleAddOn(addOn.id) },
                            colors = CheckboxDefaults.colors(checkedColor = SushiColors.AccentRed)
                        )
                        Text("${addOn.name} +${addOn.price}₽", color = SushiColors.PrimaryText)
                    }
                }

                Spacer(Modifier.height(Spacing.md))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = viewModel::decrement,
                        modifier = Modifier.size(44.dp).testTag("btn_decrement"),
                        colors = ButtonDefaults.buttonColors(containerColor = SushiColors.CardSurface)
                    ) { Text("−", color = SushiColors.PrimaryText, fontSize = 20.sp) }
                    Text("$qty", color = SushiColors.PrimaryText, fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp).testTag("qty_label"))
                    Button(
                        onClick = viewModel::increment,
                        modifier = Modifier.size(44.dp).testTag("btn_increment"),
                        colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
                    ) { Text("+", color = SushiColors.PrimaryText, fontSize = 20.sp) }
                }

                Spacer(Modifier.height(Spacing.lg))
                Button(
                    onClick = { viewModel.addToCart(); navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_add_to_cart"),
                    colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
                ) {
                    Text("В корзину — ${p.price}₽", color = SushiColors.PrimaryText)
                }
            }
        }
    }
}
```

- [ ] **Step 3: Write catalog + product detail UI tests**

```kotlin
// app/src/androidTest/java/com/baha/sushigarden/features/catalog/CatalogScreenTest.kt
package com.baha.sushigarden.features.catalog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class CatalogScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() { hiltRule.inject() }

    @Test fun addressHeaderVisible() {
        composeRule.onNodeWithTag("catalog_address").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("catalog_default")
    }

    @Test fun categoriesDisplay() {
        composeRule.onNodeWithTag("catalog_categories").assertIsDisplayed()
        composeRule.onNodeWithTag("category_sushi").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("catalog_categories")
    }

    @Test fun categorySwitch_rollsCategory() {
        composeRule.onNodeWithTag("category_rolls").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("catalog_rolls_selected")
        composeRule.onNodeWithTag("catalog_grid").assertIsDisplayed()
    }

    @Test fun tapProductNavigatesToDetail() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("product_detail_p1")
        composeRule.onNodeWithTag("btn_add_to_cart").assertIsDisplayed()
    }
}
```

```kotlin
// app/src/androidTest/java/com/baha/sushigarden/features/productdetail/ProductDetailScreenTest.kt
package com.baha.sushigarden.features.productdetail

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class ProductDetailScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
    }

    @Test fun stepperIncrement() {
        composeRule.onNodeWithTag("btn_increment").performClick()
        composeRule.onNodeWithTag("qty_label").assertTextEquals("2")
        composeRule.captureAndSaveScreenshot("product_detail_qty2")
    }

    @Test fun stepperDecrement_noLessThanOne() {
        composeRule.onNodeWithTag("btn_decrement").performClick()
        composeRule.onNodeWithTag("qty_label").assertTextEquals("1")
        composeRule.captureAndSaveScreenshot("product_detail_qty_min")
    }

    @Test fun addonSelection() {
        composeRule.onNodeWithTag("addon_ao1").onChildAt(0).performClick()
        composeRule.captureAndSaveScreenshot("product_detail_addon_selected")
        composeRule.onNodeWithTag("btn_add_to_cart").assertIsDisplayed()
    }

    @Test fun addToCart_navigatesBack() {
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("catalog_after_add")
        composeRule.onNodeWithTag("catalog_address").assertIsDisplayed()
    }
}
```

- [ ] **Step 4: Run all UI tests for P2**

```bash
./gradlew :app:connectedAndroidTest \
  --tests "*.CatalogScreenTest" \
  --tests "*.ProductDetailScreenTest"
```
Expected: 8 tests pass.

- [ ] **Step 5: Pull screenshots**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
```

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/productdetail/ \
        app/src/androidTest/java/com/baha/sushigarden/features/
git commit -m "feat(p2): ProductDetail + UI tests with screenshots"
```

---

### P2 figma-android-diff check

```
Compare docs/screenshots/android/catalog_default.png to Figma:
  mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=(Каталог frame)

Compare docs/screenshots/android/product_detail_p1.png to Figma:
  mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=(Детали продукта frame)

Check: 2-column grid layout, card corner radius 12.4dp, category pill colors,
accent red pricing, SecondaryText (#6C6C74) weight labels, large product image.
Fix divergences before P3.
```


## P3 — Cart + Checkout + Room

### Task P3.1: Room database + OrderDao

**Files:**
- Create: `data/services/orders/SushiGardenDatabase.kt`
- Create: `data/services/orders/OrderDao.kt`
- Create: `data/services/orders/OrderEntity.kt`

- [ ] **Step 1: Write failing unit test**

```kotlin
// app/src/test/java/com/baha/sushigarden/features/orders/OrderDaoTest.kt
package com.baha.sushigarden.features.orders

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.OrderEntity
import com.baha.sushigarden.data.services.orders.SushiGardenDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OrderDaoTest {
    private lateinit var db: SushiGardenDatabase
    private lateinit var dao: OrderDao

    @Before fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, SushiGardenDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.orderDao()
    }
    @After fun teardown() { db.close() }

    @Test fun insertAndGetOrder() = runTest {
        val order = OrderEntity("id1", "[]", 620, 199, 49, 868, "{}", System.currentTimeMillis())
        dao.insert(order)
        val fetched = dao.getById("id1")
        Assert.assertEquals("id1", fetched?.id)
    }
    @Test fun getAllOrders_returnsList() = runTest {
        dao.insert(OrderEntity("id1", "[]", 100, 199, 49, 348, "{}", System.currentTimeMillis()))
        dao.insert(OrderEntity("id2", "[]", 200, 199, 49, 448, "{}", System.currentTimeMillis()))
        val all = dao.getAll().first()
        Assert.assertEquals(2, all.size)
    }
}
```

Run: `./gradlew :app:test --tests "*.OrderDaoTest"` → FAIL

- [ ] **Step 2: Add Robolectric dependency to `app/build.gradle.kts`**

```kotlin
testImplementation("org.robolectric:robolectric:4.13")
testImplementation(libs.room.testing)
```

- [ ] **Step 3: Write `OrderEntity.kt`**

```kotlin
package com.baha.sushigarden.data.services.orders

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val linesJson: String,
    val subtotal: Int,
    val deliveryFee: Int,
    val serviceFee: Int,
    val total: Int,
    val addressJson: String,
    val createdAt: Long
)
```

- [ ] **Step 4: Write `OrderDao.kt`**

```kotlin
package com.baha.sushigarden.data.services.orders

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: String): OrderEntity?

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OrderEntity>>
}
```

- [ ] **Step 5: Write `SushiGardenDatabase.kt`**

```kotlin
package com.baha.sushigarden.data.services.orders

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
abstract class SushiGardenDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
```

- [ ] **Step 6: Update `AppModule.kt` to provide Room**

```kotlin
// Add to AppModule.kt inside the @Module object
@Provides @Singleton
fun provideDatabase(@ApplicationContext ctx: Context): SushiGardenDatabase =
    Room.databaseBuilder(ctx, SushiGardenDatabase::class.java, "sushi_garden.db").build()

@Provides
fun provideOrderDao(db: SushiGardenDatabase): OrderDao = db.orderDao()
```

- [ ] **Step 7: Add an `OrderMapper.kt` for converting between `Order` model and `OrderEntity`**

```kotlin
package com.baha.sushigarden.data.services.orders

import com.baha.sushigarden.data.models.DeliveryAddress
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.OrderLine
import org.json.JSONArray
import org.json.JSONObject

fun Order.toEntity(): OrderEntity {
    val linesArr = JSONArray()
    lines.forEach { line ->
        linesArr.put(JSONObject().apply {
            put("name", line.productName); put("qty", line.qty); put("total", line.lineTotal)
        })
    }
    val addrObj = JSONObject().apply {
        put("street", address.street); put("name", address.recipientName)
        put("phone", address.phone);   put("email", address.email)
    }
    return OrderEntity(id, linesArr.toString(), subtotal, deliveryFee, serviceFee, total,
        addrObj.toString(), createdAt)
}

fun OrderEntity.toOrder(): Order {
    val arr = JSONArray(linesJson)
    val lines = (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        OrderLine(o.getString("name"), o.getInt("qty"), o.getInt("total"))
    }
    val addr = JSONObject(addressJson).let {
        DeliveryAddress(it.getString("street"), it.getString("name"),
            it.getString("phone"), it.getString("email"))
    }
    return Order(id, lines, subtotal, deliveryFee, serviceFee, total, addr, createdAt)
}
```

- [ ] **Step 8: Run unit tests**

```bash
./gradlew :app:test --tests "*.OrderDaoTest"
```
Expected: 2 tests pass.

- [ ] **Step 9: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/data/services/orders/
git commit -m "feat(p3): Room database, OrderDao, OrderEntity, OrderMapper"
```

---

### Task P3.2: CartViewModel + CartScreen

**Files:**
- Create: `features/cart/CartViewModel.kt`
- Create: `features/cart/CartScreen.kt`

- [ ] **Step 1: Write `CartViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.cart

import androidx.lifecycle.ViewModel
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.services.cart.CartService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartService: CartService) : ViewModel() {
    val cartState: StateFlow<CartState> = cartService.cartState
    fun increment(productId: String) = cartService.addItem(
        cartState.value.items.first { it.product.id == productId }.product
    )
    fun decrement(productId: String) = cartService.removeItem(productId)
}
```

- [ ] **Step 2: Write `CartScreen.kt`**

```kotlin
package com.baha.sushigarden.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.data.models.CartItem
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val state by viewModel.cartState.collectAsState()

    Column(Modifier.fillMaxSize().background(SushiColors.Background).padding(Spacing.md)) {
        Text("Корзина", color = SushiColors.PrimaryText, fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("cart_title"))

        if (state.items.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Корзина пуста", color = SushiColors.SecondaryText,
                    modifier = Modifier.testTag("cart_empty"))
            }
        } else {
            LazyColumn(Modifier.weight(1f).testTag("cart_items")) {
                items(state.items) { item -> CartItemRow(item, viewModel) }
            }
            Divider(color = SushiColors.Divider)
            Row(Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Итого", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
                Text("${state.total}₽", color = SushiColors.AccentRed,
                    fontWeight = FontWeight.Bold, modifier = Modifier.testTag("cart_total"))
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Checkout.route) },
            enabled = state.items.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_checkout"),
            colors = ButtonDefaults.buttonColors(
                containerColor = SushiColors.AccentRed,
                disabledContainerColor = SushiColors.AccentRed.copy(alpha = 0.4f)
            )
        ) { Text("Оформить") }
    }
}

@Composable
private fun CartItemRow(item: CartItem, viewModel: CartViewModel) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = Spacing.sm)
            .testTag("cart_item_${item.product.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.product.name, color = SushiColors.PrimaryText)
            if (item.selectedAddOns.isNotEmpty()) {
                Text(item.selectedAddOns.joinToString { it.name },
                    color = SushiColors.SecondaryText, fontSize = 12.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.decrement(item.product.id) },
                modifier = Modifier.testTag("cart_decrement_${item.product.id}")) {
                Text("−", color = SushiColors.PrimaryText, fontSize = 20.sp)
            }
            Text("${item.quantity}", color = SushiColors.PrimaryText,
                modifier = Modifier.testTag("cart_qty_${item.product.id}"))
            IconButton(onClick = { viewModel.increment(item.product.id) },
                modifier = Modifier.testTag("cart_increment_${item.product.id}")) {
                Text("+", color = SushiColors.AccentRed, fontSize = 20.sp)
            }
        }
        Text("${item.lineTotal}₽", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/cart/
git commit -m "feat(p3): CartViewModel + CartScreen"
```

---

### Task P3.3: CheckoutViewModel + CheckoutScreen

**Files:**
- Create: `features/checkout/CheckoutViewModel.kt`
- Create: `features/checkout/CheckoutScreen.kt`

- [ ] **Step 1: Write `CheckoutViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.DeliveryAddress
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.OrderLine
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartService: CartService,
    private val orderDao: OrderDao
) : ViewModel() {
    val cartState = cartService.cartState
    val street    = MutableStateFlow("")
    val recipient = MutableStateFlow("")
    val phone     = MutableStateFlow("")
    val email     = MutableStateFlow("")

    private val _orderState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val orderState: StateFlow<UiState<String>> = _orderState.asStateFlow()

    fun confirm() = viewModelScope.launch {
        _orderState.value = UiState.Loading
        val cart = cartState.value
        val address = DeliveryAddress(street.value, recipient.value, phone.value, email.value)
        val lines = cart.items.map { OrderLine(it.product.name, it.quantity, it.lineTotal) }
        val order = Order(
            id = UUID.randomUUID().toString(),
            lines = lines, subtotal = cart.subtotal,
            deliveryFee = cart.deliveryFee, serviceFee = cart.serviceFee,
            total = cart.total, address = address
        )
        orderDao.insert(order.toEntity())
        cartService.clearCart()
        _orderState.value = UiState.Success(order.id)
    }
}
```

- [ ] **Step 2: Write `CheckoutScreen.kt`**

```kotlin
package com.baha.sushigarden.features.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.UiState
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CheckoutScreen(navController: NavController, viewModel: CheckoutViewModel = hiltViewModel()) {
    val cartState by viewModel.cartState.collectAsState()
    val orderState by viewModel.orderState.collectAsState()
    val street by viewModel.street.collectAsState()
    val recipient by viewModel.recipient.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val email by viewModel.email.collectAsState()

    LaunchedEffect(orderState) {
        if (orderState is UiState.Success) {
            navController.navigate(Screen.Tracking.route) {
                popUpTo(Screen.Cart.route) { inclusive = true }
            }
        }
    }

    Column(
        Modifier.fillMaxSize().background(SushiColors.Background)
            .verticalScroll(rememberScrollState()).padding(Spacing.md)
    ) {
        Text("Оформление заказа", color = SushiColors.PrimaryText,
            fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(Spacing.md))

        checkoutField("Адрес доставки", street, viewModel.street::value::set,
            "field_street")
        checkoutField("Кому", recipient, viewModel.recipient::value::set,
            "field_recipient")
        checkoutField("Телефон", phone, viewModel.phone::value::set,
            "field_phone")
        checkoutField("Почта", email, viewModel.email::value::set,
            "field_email")

        Spacer(Modifier.height(Spacing.md))
        Surface(color = SushiColors.CardSurface, shape = androidx.compose.foundation.shape.RoundedCornerShape(Spacing.cardCorner)) {
            Column(Modifier.padding(Spacing.md)) {
                Text("Картой онлайн", color = SushiColors.PrimaryText)
            }
        }

        Spacer(Modifier.height(Spacing.md))
        summaryRow("Сумма заказа", "${cartState.subtotal}₽")
        summaryRow("Доставка", "${cartState.deliveryFee}₽")
        summaryRow("Сервисный сбор", "${cartState.serviceFee}₽")
        Divider(color = SushiColors.Divider, modifier = Modifier.padding(vertical = Spacing.sm))
        summaryRow("Итого", "${cartState.total}₽", bold = true)

        Spacer(Modifier.height(Spacing.lg))
        Button(
            onClick = viewModel::confirm,
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_confirm"),
            colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
        ) {
            if (orderState is UiState.Loading)
                CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(20.dp))
            else Text("Подтвердить")
        }
    }
}

@Composable
private fun checkoutField(label: String, value: String, onChange: (String) -> Unit, tag: String) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm).testTag(tag),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SushiColors.PrimaryText, unfocusedTextColor = SushiColors.PrimaryText,
            focusedBorderColor = SushiColors.AccentRed, unfocusedBorderColor = SushiColors.SecondaryText
        )
    )
}

@Composable
private fun summaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = SushiColors.SecondaryText)
        Text(value, color = SushiColors.PrimaryText,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.testTag("summary_${label.lowercase().replace(" ", "_")}"))
    }
}
```

- [ ] **Step 3: Write Cart + Checkout UI tests**

```kotlin
// app/src/androidTest/java/com/baha/sushigarden/features/cart/CartScreenTest.kt
package com.baha.sushigarden.features.cart

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import com.baha.sushigarden.data.services.cart.CartService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*
import javax.inject.Inject

@HiltAndroidTest
class CartScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()
    @Inject lateinit var cartService: CartService

    @Before fun init() { hiltRule.inject() }

    @Test fun emptyCartShowsEmptyState() {
        // Navigate to cart tab
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_empty").assertIsDisplayed()
        composeRule.onNodeWithTag("btn_checkout").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("cart_empty")
    }

    @Test fun filledCart_itemVisible() {
        // Add item from catalog first
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_item_p1").assertIsDisplayed()
        composeRule.onNodeWithTag("btn_checkout").assertIsEnabled()
        composeRule.captureAndSaveScreenshot("cart_filled")
    }

    @Test fun cartItemQtyIncrement() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_increment_p1").performClick()
        composeRule.onNodeWithTag("cart_qty_p1").assertTextEquals("2")
        composeRule.captureAndSaveScreenshot("cart_qty_incremented")
    }
}
```

```kotlin
// app/src/androidTest/java/com/baha/sushigarden/features/checkout/CheckoutScreenTest.kt
package com.baha.sushigarden.features.checkout

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class CheckoutScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        // Add an item to cart so checkout is accessible
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
    }

    @Test fun checkoutFieldsDisplayed() {
        composeRule.onNodeWithTag("field_street").assertIsDisplayed()
        composeRule.onNodeWithTag("field_recipient").assertIsDisplayed()
        composeRule.onNodeWithTag("field_phone").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("checkout_empty")
    }

    @Test fun confirmCreatesOrderAndNavigatesToTracking() {
        composeRule.onNodeWithTag("field_street").performTextInput("ул. Пушкина, 10")
        composeRule.onNodeWithTag("field_recipient").performTextInput("Иван Иванов")
        composeRule.onNodeWithTag("field_phone").performTextInput("+7 900 000-00-00")
        composeRule.onNodeWithTag("field_email").performTextInput("test@test.com")
        composeRule.captureAndSaveScreenshot("checkout_filled")
        composeRule.onNodeWithTag("btn_confirm").performClick()
        composeRule.waitForIdle()
        // Should navigate to tracking
        composeRule.onNodeWithTag("tracking_map").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_after_checkout")
    }
}
```

- [ ] **Step 4: Run all P3 UI tests**

```bash
./gradlew :app:connectedAndroidTest \
  --tests "*.CartScreenTest" \
  --tests "*.CheckoutScreenTest"
```
Expected: 5 tests pass.

- [ ] **Step 5: Pull screenshots + commit**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
git add app/src/main/java/com/baha/sushigarden/features/cart/ \
        app/src/main/java/com/baha/sushigarden/features/checkout/ \
        app/src/androidTest/java/com/baha/sushigarden/features/cart/ \
        app/src/androidTest/java/com/baha/sushigarden/features/checkout/
git commit -m "feat(p3): Cart + Checkout + Room integration with UI tests"
```

---

### P3 figma-android-diff check

```
Compare docs/screenshots/android/cart_filled.png:
  mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=(Корзина frame)
  Check: line items layout, stepper +/− buttons, total row, red Оформить button

Compare docs/screenshots/android/checkout_filled.png:
  mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=(Оформление frame)
  Check: field labels, order summary rows, Подтвердить button
Fix divergences before P4.
```


## P4 — Promotions + Profile + Orders

### Task P4.1: PromotionsScreen

**Files:**
- Create: `features/promotions/PromotionsViewModel.kt`
- Create: `features/promotions/PromotionsScreen.kt`

- [ ] **Step 1: Write `PromotionsViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.promotions

import androidx.lifecycle.ViewModel
import com.baha.sushigarden.data.models.Promotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Promotion(val id: String, val title: String, val subtitle: String, val imageRes: Int)

@HiltViewModel
class PromotionsViewModel @Inject constructor() : ViewModel() {
    private val _promos = MutableStateFlow(listOf(
        Promotion("p1", "ХОТ РОЛЛС", "Горячие роллы со скидкой 20%", 0),
        Promotion("p2", "HAPPY HOUR", "С 14:00 до 16:00 скидка на всё", 0),
        Promotion("p3", "COMBO SET", "Набор из 4 ролл + напиток", 0)
    ))
    val promos = _promos.asStateFlow()
}
```

- [ ] **Step 2: Write `PromotionsScreen.kt`**

```kotlin
package com.baha.sushigarden.features.promotions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun PromotionsScreen(viewModel: PromotionsViewModel = hiltViewModel()) {
    val promos by viewModel.promos.collectAsState()
    Column(Modifier.fillMaxSize().background(SushiColors.Background).padding(Spacing.md)) {
        Text("Акции", color = SushiColors.PrimaryText, fontSize = 22.sp,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(Spacing.md))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.testTag("promos_list")
        ) {
            items(promos) { promo ->
                Surface(
                    shape = RoundedCornerShape(Spacing.cardCorner),
                    color = SushiColors.CardSurface,
                    modifier = Modifier.fillMaxWidth().testTag("promo_${promo.id}")
                ) {
                    Column(Modifier.padding(Spacing.md)) {
                        Text(promo.title, color = SushiColors.PrimaryText,
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(Spacing.xs))
                        Text(promo.subtitle, color = SushiColors.SecondaryText, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Write `PromotionsScreenTest.kt`**

```kotlin
package com.baha.sushigarden.features.promotions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class PromotionsScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        composeRule.onAllNodesWithText("Акции").onFirst().performClick()
        composeRule.waitForIdle()
    }

    @Test fun promoBannersRender() {
        composeRule.onNodeWithTag("promos_list").assertIsDisplayed()
        composeRule.onNodeWithTag("promo_p1").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("promotions")
    }

    @Test fun promoScrollable() {
        composeRule.onNodeWithTag("promos_list").performScrollToIndex(2)
        composeRule.onNodeWithTag("promo_p3").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("promotions_scrolled")
    }
}
```

- [ ] **Step 4: Run UI tests**

```bash
./gradlew :app:connectedAndroidTest --tests "*.PromotionsScreenTest"
```
Expected: 2 tests pass.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/promotions/ \
        app/src/androidTest/java/com/baha/sushigarden/features/promotions/
git commit -m "feat(p4): PromotionsScreen + UI tests"
```

---

### Task P4.2: OrdersScreen + OrderDetailScreen

**Files:**
- Create: `features/orders/OrdersViewModel.kt`
- Create: `features/orders/OrdersScreen.kt`
- Create: `features/orders/OrderDetailScreen.kt`

- [ ] **Step 1: Write `OrdersViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val orderDao: OrderDao) : ViewModel() {
    val orders: StateFlow<List<Order>> = orderDao.getAll()
        .map { entities -> entities.map { it.toOrder() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getOrder(id: String) = orders.value.find { it.id == id }
}
```

- [ ] **Step 2: Write `OrdersScreen.kt`**

```kotlin
package com.baha.sushigarden.features.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(navController: NavController, viewModel: OrdersViewModel = hiltViewModel()) {
    val orders by viewModel.orders.collectAsState()
    Column(Modifier.fillMaxSize().background(SushiColors.Background).padding(Spacing.md)) {
        Text("Заказы", color = SushiColors.PrimaryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(Spacing.md))
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет заказов", color = SushiColors.SecondaryText,
                    modifier = Modifier.testTag("orders_empty"))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.testTag("orders_list")) {
                items(orders) { order ->
                    Surface(
                        shape = RoundedCornerShape(Spacing.cardCorner),
                        color = SushiColors.CardSurface,
                        modifier = Modifier.fillMaxWidth()
                            .clickable { navController.navigate(Screen.OrderDetail.createRoute(order.id)) }
                            .testTag("order_row_${order.id}")
                    ) {
                        Row(Modifier.padding(Spacing.md), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Заказ #${order.id.take(8)}", color = SushiColors.PrimaryText,
                                    fontWeight = FontWeight.Bold)
                                Text(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                    .format(Date(order.createdAt)),
                                    color = SushiColors.SecondaryText, fontSize = 12.sp)
                            }
                            Text("${order.total}₽", color = SushiColors.AccentRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Write `OrderDetailScreen.kt`**

```kotlin
package com.baha.sushigarden.features.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun OrderDetailScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val order = viewModel.getOrder(orderId)
    Column(Modifier.fillMaxSize().background(SushiColors.Background).padding(Spacing.md)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, "Назад", tint = SushiColors.PrimaryText)
        }
        order?.let { o ->
            Text("Заказ #${o.id.take(8)}", color = SushiColors.PrimaryText,
                fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(Spacing.md))
            o.lines.forEach { line ->
                Row(Modifier.fillMaxWidth().testTag("order_line_${line.productName}"),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${line.productName} ×${line.qty}", color = SushiColors.PrimaryText)
                    Text("${line.lineTotal}₽", color = SushiColors.PrimaryText)
                }
                Spacer(Modifier.height(Spacing.xs))
            }
            Divider(color = SushiColors.Divider, modifier = Modifier.padding(vertical = Spacing.sm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Итого", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
                Text("${o.total}₽", color = SushiColors.AccentRed, fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("order_total"))
            }
        }
    }
}
```

- [ ] **Step 4: Write `OrdersScreenTest.kt`**

```kotlin
package com.baha.sushigarden.features.orders

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class OrdersScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        composeRule.onAllNodesWithText("Заказы").onFirst().performClick()
        composeRule.waitForIdle()
    }

    @Test fun emptyOrdersState() {
        composeRule.onNodeWithTag("orders_empty").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("orders_empty")
    }
}
```

```kotlin
// app/src/androidTest/java/com/baha/sushigarden/features/orders/OrderDetailScreenTest.kt
package com.baha.sushigarden.features.orders

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class OrderDetailScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        // Place an order first via checkout flow
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("field_street").performTextInput("ул. Ленина, 1")
        composeRule.onNodeWithTag("field_recipient").performTextInput("Тест")
        composeRule.onNodeWithTag("field_phone").performTextInput("+7 900 000-00-00")
        composeRule.onNodeWithTag("field_email").performTextInput("t@t.com")
        composeRule.onNodeWithTag("btn_confirm").performClick()
        composeRule.waitForIdle()
        // Navigate to orders
        composeRule.onAllNodesWithText("Заказы").onFirst().performClick()
        composeRule.waitForIdle()
    }

    @Test fun orderRowVisible() {
        composeRule.onNodeWithTag("orders_list").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("orders_filled")
    }

    @Test fun tapOrderRowShowsDetail() {
        composeRule.onNodeWithTag("orders_list").onChildAt(0).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("order_total").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("order_detail")
    }
}
```

- [ ] **Step 5: Run UI tests**

```bash
./gradlew :app:connectedAndroidTest \
  --tests "*.OrdersScreenTest" \
  --tests "*.OrderDetailScreenTest"
```
Expected: 3 tests pass.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/features/orders/ \
        app/src/androidTest/java/com/baha/sushigarden/features/orders/
git commit -m "feat(p4): OrdersScreen + OrderDetailScreen + UI tests"
```

---

### Task P4.3: ProfileScreen

**Files:**
- Create: `features/profile/ProfileViewModel.kt`
- Create: `features/profile/ProfileScreen.kt`

- [ ] **Step 1: Write `ProfileViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.AuthService
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val orderDao: OrderDao
) : ViewModel() {
    val user: StateFlow<UserProfile?> = authService.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val orders: StateFlow<List<Order>> = orderDao.getAll()
        .map { it.map { e -> e.toOrder() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val phone = MutableStateFlow("")

    fun onPhoneChange(v: String) { phone.value = v }

    fun logout() = viewModelScope.launch { authService.signOut() }
}
```

- [ ] **Step 2: Write `ProfileScreen.kt`**

```kotlin
package com.baha.sushigarden.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val phone by viewModel.phone.collectAsState()

    LaunchedEffect(user) {
        if (user != null) viewModel.phone.value = user!!.phone
    }
    LaunchedEffect(user) {
        if (user == null) navController.navigate(Screen.Auth.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    LazyColumn(
        Modifier.fillMaxSize().background(SushiColors.Background).padding(Spacing.md)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(SushiColors.CardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user?.name?.firstOrNull()?.toString() ?: "?",
                        color = SushiColors.PrimaryText, fontSize = 24.sp)
                }
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Text(user?.name ?: "", color = SushiColors.PrimaryText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("profile_name"))
                    Text(user?.email ?: "", color = SushiColors.SecondaryText,
                        modifier = Modifier.testTag("profile_email"))
                }
            }
            Spacer(Modifier.height(Spacing.md))
            OutlinedTextField(
                value = phone, onValueChange = viewModel::onPhoneChange,
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth().testTag("profile_phone"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SushiColors.PrimaryText,
                    unfocusedTextColor = SushiColors.PrimaryText,
                    focusedBorderColor = SushiColors.AccentRed,
                    unfocusedBorderColor = SushiColors.SecondaryText
                )
            )
            Spacer(Modifier.height(Spacing.md))
            Text("Мои заказы", color = SushiColors.PrimaryText,
                fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(Spacing.sm))
        }
        items(orders) { order ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = Spacing.xs)
                    .testTag("profile_order_${order.id}"),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Заказ #${order.id.take(8)}", color = SushiColors.PrimaryText)
                Text("${order.total}₽", color = SushiColors.AccentRed)
            }
        }
        item {
            Spacer(Modifier.height(Spacing.lg))
            Button(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth().testTag("btn_logout"),
                colors = ButtonDefaults.buttonColors(containerColor = SushiColors.CardSurface)
            ) { Text("Выйти", color = SushiColors.AccentRed) }
        }
    }
}
```

- [ ] **Step 3: Write `ProfileScreenTest.kt`**

```kotlin
package com.baha.sushigarden.features.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class ProfileScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        composeRule.onAllNodesWithText("Профиль").onFirst().performClick()
        composeRule.waitForIdle()
    }

    @Test fun profileInfoVisible() {
        composeRule.onNodeWithTag("profile_name").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_email").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("profile")
    }

    @Test fun phoneFieldEditable() {
        composeRule.onNodeWithTag("profile_phone").performTextClearance()
        composeRule.onNodeWithTag("profile_phone").performTextInput("+7 999 999-99-99")
        composeRule.onNodeWithTag("profile_phone").assertTextContains("+7 999 999-99-99")
        composeRule.captureAndSaveScreenshot("profile_phone_edited")
    }

    @Test fun logoutNavigatesToAuth() {
        composeRule.onNodeWithTag("btn_logout").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("auth_after_logout")
        composeRule.onNodeWithTag("btn_auth_submit").assertIsDisplayed()
    }
}
```

- [ ] **Step 4: Run all P4 UI tests**

```bash
./gradlew :app:connectedAndroidTest \
  --tests "*.PromotionsScreenTest" \
  --tests "*.OrdersScreenTest" \
  --tests "*.OrderDetailScreenTest" \
  --tests "*.ProfileScreenTest"
```
Expected: 8 tests pass.

- [ ] **Step 5: Pull screenshots + commit**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
git add app/src/main/java/com/baha/sushigarden/features/profile/ \
        app/src/androidTest/java/com/baha/sushigarden/features/profile/
git commit -m "feat(p4): ProfileScreen + UI tests with screenshots"
```

---

### P4 figma-android-diff check

```
Compare screenshots to Figma frames:
  promotions.png    → Акции frame (nodeId from Figma Акции screen)
  profile.png       → Профиль frame
  orders_empty.png  → Заказы frame (empty state)
  orders_filled.png → Заказы frame (filled state)
  order_detail.png  → OrderDetail frame

Check: card surface #292830, avatar circle, phone field styling,
logout button color, order row layout. Fix before P5.
```


## P5 — Tracking map

### Task P5.1: CourierSimulator

**Files:**
- Create: `data/services/delivery/CourierSimulator.kt`

- [ ] **Step 1: Write failing unit test**

```kotlin
// app/src/test/java/com/baha/sushigarden/features/delivery/CourierSimulatorTest.kt
package com.baha.sushigarden.features.delivery

import com.baha.sushigarden.data.services.delivery.CourierSimulator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CourierSimulatorTest {
    private val dispatcher = StandardTestDispatcher()
    private val sim = CourierSimulator(dispatcher)

    @Test fun initialEta_isPositive() = runTest(dispatcher) {
        assertTrue(sim.etaMinutes.first() > 0)
    }
    @Test fun progress_startAtZero() = runTest(dispatcher) {
        assertEquals(0f, sim.progress.first(), 0.01f)
    }
    @Test fun progress_advancesOverTime() = runTest(dispatcher) {
        sim.start()
        advanceTimeBy(5_000)
        assertTrue(sim.progress.first() > 0f)
    }
}
```

Run: `./gradlew :app:test --tests "*.CourierSimulatorTest"` → FAIL

- [ ] **Step 2: Write `CourierSimulator.kt`**

```kotlin
package com.baha.sushigarden.data.services.delivery

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourierSimulator(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    // Hardcoded route: restaurant → delivery address in Moscow
    private val route = listOf(
        LatLng(55.7558, 37.6173),
        LatLng(55.7580, 37.6200),
        LatLng(55.7600, 37.6230),
        LatLng(55.7620, 37.6260),
        LatLng(55.7640, 37.6290),
        LatLng(55.7660, 37.6320)
    )

    private val _position  = MutableStateFlow(route.first())
    private val _progress  = MutableStateFlow(0f)
    private val _etaMinutes = MutableStateFlow(30)

    val position    = _position.asStateFlow()
    val progress    = _progress.asStateFlow()
    val etaMinutes  = _etaMinutes.asStateFlow()

    private var job: Job? = null

    fun start() {
        job = CoroutineScope(dispatcher).launch {
            val totalSteps = route.size - 1
            for (i in 0 until totalSteps) {
                delay(3_000)
                _position.value = route[i + 1]
                _progress.value = (i + 1).toFloat() / totalSteps
                _etaMinutes.value = (30 * (1f - _progress.value)).toInt().coerceAtLeast(1)
            }
        }
    }

    fun stop() { job?.cancel(); job = null }
}
```

- [ ] **Step 3: Run unit tests**

```bash
./gradlew :app:test --tests "*.CourierSimulatorTest"
```
Expected: 3 tests pass.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/baha/sushigarden/data/services/delivery/
git commit -m "feat(p5): CourierSimulator with coroutine-based route animation"
```

---

### Task P5.2: TrackingViewModel + TrackingScreen

**Files:**
- Create: `features/tracking/TrackingViewModel.kt`
- Create: `features/tracking/TrackingScreen.kt`

- [ ] **Step 1: Write `TrackingViewModel.kt`**

```kotlin
package com.baha.sushigarden.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.models.Courier
import com.baha.sushigarden.data.services.delivery.CourierSimulator
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val courierSimulator: CourierSimulator
) : ViewModel() {
    val courierPosition: StateFlow<LatLng> = courierSimulator.position
        .stateIn(viewModelScope, SharingStarted.Eagerly, LatLng(55.7558, 37.6173))

    val etaMinutes: StateFlow<Int> = courierSimulator.etaMinutes
        .stateIn(viewModelScope, SharingStarted.Eagerly, 30)

    val courier = Courier()

    init { courierSimulator.start() }
    override fun onCleared() { courierSimulator.stop() }
}
```

- [ ] **Step 2: Add `CourierSimulator` to `AppModule.kt`**

```kotlin
@Provides @Singleton
fun provideCourierSimulator(): CourierSimulator = CourierSimulator()
```

- [ ] **Step 3: Write `TrackingScreen.kt`**

```kotlin
package com.baha.sushigarden.features.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun TrackingScreen(navController: NavController, viewModel: TrackingViewModel = hiltViewModel()) {
    val courierPos by viewModel.courierPosition.collectAsState()
    val eta by viewModel.etaMinutes.collectAsState()
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(courierPos, 14f)
    }

    LaunchedEffect(courierPos) {
        cameraState.position = CameraPosition.fromLatLngZoom(courierPos, 14f)
    }

    Column(Modifier.fillMaxSize().background(SushiColors.Background)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, "Назад", tint = SushiColors.PrimaryText)
        }

        GoogleMap(
            modifier = Modifier.weight(1f).testTag("tracking_map"),
            cameraPositionState = cameraState
        ) {
            Marker(state = MarkerState(position = courierPos), title = viewModel.courier.name)
        }

        Surface(
            color = SushiColors.CardSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(Spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(viewModel.courier.name, color = SushiColors.PrimaryText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("courier_name"))
                        Text(viewModel.courier.title, color = SushiColors.SecondaryText)
                    }
                    Text("~$eta мин", color = SushiColors.AccentRed, fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("courier_eta"))
                }
            }
        }
    }
}
```

- [ ] **Step 4: Write `TrackingScreenTest.kt`**

```kotlin
package com.baha.sushigarden.features.tracking

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*

@HiltAndroidTest
class TrackingScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before fun init() {
        hiltRule.inject()
        // Navigate to tracking via checkout flow
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Корзина").filterToOne(hasTestTag("cart_title").not()).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("field_street").performTextInput("ул. Ленина, 1")
        composeRule.onNodeWithTag("field_recipient").performTextInput("Тест")
        composeRule.onNodeWithTag("field_phone").performTextInput("+7 900 000-00-00")
        composeRule.onNodeWithTag("field_email").performTextInput("t@t.com")
        composeRule.onNodeWithTag("btn_confirm").performClick()
        composeRule.waitForIdle()
    }

    @Test fun mapVisible() {
        composeRule.onNodeWithTag("tracking_map").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking")
    }

    @Test fun courierCardVisible() {
        composeRule.onNodeWithTag("courier_name").assertTextEquals("Максим Винокур")
        composeRule.captureAndSaveScreenshot("tracking_courier_card")
    }

    @Test fun etaCountdownVisible() {
        composeRule.onNodeWithTag("courier_eta").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_eta")
    }

    @Test fun backNavigatesToOrders() {
        composeRule.onAllNodesWithText("Заказы").onFirst().performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("orders_after_tracking")
        composeRule.onNodeWithTag("orders_list").assertIsDisplayed()
    }
}
```

- [ ] **Step 5: Run P5 UI tests**

```bash
./gradlew :app:connectedAndroidTest --tests "*.TrackingScreenTest"
```
Expected: 4 tests pass.

- [ ] **Step 6: Pull screenshots + commit**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
git add app/src/main/java/com/baha/sushigarden/features/tracking/ \
        app/src/androidTest/java/com/baha/sushigarden/features/tracking/
git commit -m "feat(p5): TrackingScreen + CourierSimulator + Google Maps + UI tests"
```

---

### P5 figma-android-diff check

```
Compare docs/screenshots/android/tracking.png:
  mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=(Отслеживание frame)

Check: map takes upper portion of screen, courier card at bottom with dark
card surface #292830, courier name "Максим Винокур" visible, ETA text in
accent red #EC1A35, card has rounded top corners.
Fix divergences before P6.
```


## P6 — UI-test sweep + design-shotgun

### Task P6.1: Full test run

- [ ] **Step 1: Run all unit tests**

```bash
./gradlew :app:test
```
Expected: `BUILD SUCCESSFUL` — all unit tests pass.

- [ ] **Step 2: Run all UI tests**

```bash
./gradlew :app:connectedAndroidTest
```
Expected: `BUILD SUCCESSFUL` — all instrumented tests pass.

- [ ] **Step 3: Pull all screenshots**

```bash
adb pull /sdcard/Android/data/com.baha.sushigarden/files/screenshots/ docs/screenshots/android/
ls docs/screenshots/android/
```

Expected files present:
```
auth_register_filled.png
auth_login_empty.png
auth_login_filled.png
auth_register_no_consent.png
auth_register_invalid_email.png
auth_register_short_password.png
auth_login_wrong.png
catalog_default.png
catalog_categories.png
catalog_rolls_selected.png
product_detail_p1.png
product_detail_qty2.png
product_detail_addon_selected.png
catalog_after_add.png
cart_empty.png
cart_filled.png
cart_qty_incremented.png
checkout_empty.png
checkout_filled.png
tracking_after_checkout.png
promotions.png
promotions_scrolled.png
orders_empty.png
orders_filled.png
order_detail.png
profile.png
profile_phone_edited.png
auth_after_logout.png
tracking.png
tracking_courier_card.png
tracking_eta.png
orders_after_tracking.png
```

---

### Task P6.2: figma-android-diff — all 10 screens

Run `figma-android-diff` skill for each screen. For each comparison:
1. Capture fresh screenshot via `adb exec-out screencap -p > docs/screenshots/android/<screen>.png`
2. Fetch Figma frame via `mcp__figma__view_node fileKey=wOK1MMzuJZF3pIOZhGHpY9 nodeId=<node>`
3. Compare and note any severity-ranked defects

| Screen | Screenshot file | Figma frame hint |
|--------|----------------|-----------------|
| Регистрация | auth_register_filled.png | Auth/Register frame |
| Войти | auth_login_filled.png | Auth/Login frame |
| Каталог | catalog_default.png | Каталог/Главная frame |
| Детали продукта | product_detail_p1.png | Детали продукта frame |
| Акции | promotions.png | Акции frame |
| Заказы (пусто) | orders_empty.png | Заказы frame (empty) |
| Заказы (список) | orders_filled.png | Заказы frame (filled) |
| Корзина | cart_filled.png | Корзина frame |
| Оформление | checkout_filled.png | Оформление заказа frame |
| Отслеживание | tracking.png | Отслеживание frame |
| Профиль | profile.png | Профиль frame |

- [ ] **Step 1: Compare all 11 screenshots to Figma** (use `figma-android-diff` skill)

- [ ] **Step 2: Fix all High and Medium severity defects**

For each defect found, make the minimal change and re-run the affected UI test to verify screenshot improves.

- [ ] **Step 3: Re-run full test suite to confirm no regressions**

```bash
./gradlew :app:test && ./gradlew :app:connectedAndroidTest
```
Expected: all tests pass.

- [ ] **Step 4: Final commit**

```bash
git add docs/screenshots/android/
git add app/src/
git commit -m "feat(p6): full UI-test sweep + design-shotgun fixes — all 11 screens verified"
```

---

## Summary

| Phase | What ships |
|-------|-----------|
| P0 | Gradle scaffold, design system (Figma tokens + Sen font), data models, screenshot helper |
| P1 | App shell, auth gate, Register + Login screens, Firebase + Fake auth, Hilt modules |
| P2 | LocalMenuRepository (5 cats, 10 items), CatalogScreen, ProductDetailScreen, CartService |
| P3 | Room + OrderDao, CartScreen, CheckoutScreen, order creation flow |
| P4 | PromotionsScreen, OrdersScreen, OrderDetailScreen, ProfileScreen |
| P5 | Google Maps Compose, CourierSimulator, TrackingScreen |
| P6 | Full UI-test sweep, screenshot capture for all screens, figma-android-diff fixes |

**Every phase is independently releasable.** Each phase adds working, tested screens. Screenshot capture (`captureAndSaveScreenshot`) runs in every UI test method. `figma-android-diff` comparison closes each phase.

