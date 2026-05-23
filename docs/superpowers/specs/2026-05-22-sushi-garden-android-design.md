# Sushi Garden Android — Design Spec

**Date:** 2026-05-22
**Status:** Approved
**Figma source:** https://www.figma.com/design/wOK1MMzuJZF3pIOZhGHpY9/Error-Nil.-Apps?node-id=1-6 (file key `wOK1MMzuJZF3pIOZhGHpY9`, canvas "App1")
**iOS reference:** `~/Desktop/llm-ai-projects/sushi-garden-ios`

## 1. Overview

Sushi Garden Android is a pixel-perfect port of the iOS SwiftUI app to Android using Jetpack Compose. It targets the same Figma design spec, the same Firebase Authentication project, and the same local mock data. The visual identity — dark background, red accents, Sen typography — is reproduced exactly on Android. UI tests cover every screen and every flow.

### Stated requirements
- Jetpack Compose + MVVM + Kotlin coroutines.
- Pixel-perfect reproduction of the Figma design (same as iOS).
- Firebase Auth connecting to the same Firebase project as iOS.
- Local mock data for menu, cart, orders, promotions.
- Google Maps for order tracking screen.
- UI tests for every screen and every flow.
- Screenshot comparison via `/design-shotgun` after each phase.

### Approved scoping decisions
- **Design approach:** Pixel-perfect Figma port. Material3 theme is overridden with Figma tokens throughout; no Material component defaults leak into the UI.
- **Firebase:** Same project as iOS. Android app registered in the same Firebase console. `google-services.json` injected at build time, git-ignored.
- **Backend scope:** Firebase Auth only. Menu, cart, orders, promotions are local.
- **Map:** Google Maps Compose SDK. Simulated courier animation along a hardcoded route (no live backend).
- **UI-test isolation:** `BuildConfig.IS_UI_TEST` switches Hilt bindings to `FakeAuthService` + in-memory Room, fully offline and deterministic.
- **Language:** Russian, matching the design exactly.
- **Fonts:** Sen Regular/Bold bundled in `assets/fonts/`. Mugesta (user-supplied `.ttf`) for `+`/`−` steppers; falls back to a standard font until supplied.
- **Persistence:** Placed orders persisted with Room (`OrderDao`). Active cart is in-memory (`InMemoryCartService`) and clears on process death.

### User-supplied dependencies
- `google-services.json` (real Firebase credentials, same project as iOS).
- Mugesta font file (`.ttf`).

## 2. Project setup

- **Location:** `~/Desktop/llm-ai-projects/sushi-garden-android`
- **App name:** `SushiGarden` · **Package:** `com.baha.sushigarden`
- **Min SDK:** 26 · **Target SDK:** 36 · **Kotlin:** 2.0
- **Build system:** Gradle with Kotlin DSL (`build.gradle.kts`)
- **Emulator:** `Pixel_8_API_36` (1080×2400) — already available locally
- **Targets:** `app` (main), unit tests, instrumented UI tests

### Key dependencies
| Library | Purpose |
|---------|---------|
| Jetpack Compose BOM (latest stable) | UI framework |
| Navigation Compose | NavHost + bottom nav |
| Hilt (hilt-android + hilt-navigation-compose) | DI |
| Firebase Auth KTX | Authentication |
| Room KTX | Order persistence |
| Google Maps Compose | Tracking map |
| Coil Compose | Image loading |
| Kotlin Coroutines | Async |
| JUnit4 + MockK + kotlinx-coroutines-test | Unit tests |
| Compose UI Test + Espresso | UI tests |

## 3. Architecture

### iOS → Android mapping

| iOS (SwiftUI) | Android (Compose) |
|---------------|------------------|
| `SwiftUI View` | `@Composable fun` |
| `@StateObject ObservableObject` | `ViewModel` + `StateFlow` |
| `@Published var` | `MutableStateFlow` / `collectAsState()` |
| `Dependencies` (DI root) | Hilt modules |
| `RootTabBarController` (5 tabs) | `NavHost` + `BottomNavigation` |
| `FirebaseAuthService` | `FirebaseAuthService` |
| `SwiftData` (`OrderStore`) | Room (`OrderDao`) |
| `MapKit` + `CourierSimulator` | Google Maps Compose + `CourierSimulator` |
| `XCTest` + `XCUITest` | JUnit4 + Compose UI Test |

### Project structure

```
app/src/main/java/com/baha/sushigarden/
  ui/
    designsystem/        Color.kt · Typography.kt · Spacing.kt · Theme.kt
    components/          shared composables (buttons, form fields, cards)
  features/
    auth/                AuthScreen.kt · AuthViewModel.kt
    catalog/             CatalogScreen.kt · CatalogViewModel.kt
    productdetail/       ProductDetailScreen.kt · ProductDetailViewModel.kt
    promotions/          PromotionsScreen.kt · PromotionsViewModel.kt
    cart/                CartScreen.kt · CartViewModel.kt
    checkout/            CheckoutScreen.kt · CheckoutViewModel.kt
    tracking/            TrackingScreen.kt · TrackingViewModel.kt
    orders/              OrdersScreen.kt · OrderDetailScreen.kt · OrdersViewModel.kt
    profile/             ProfileScreen.kt · ProfileViewModel.kt
  data/
    models/              Product · CartItem · AddOn · Order · DeliveryAddress · Courier · UserProfile
    services/
      auth/              AuthService.kt · FirebaseAuthService.kt · FakeAuthService.kt
      catalog/           MenuRepository.kt · LocalMenuRepository.kt
      cart/              CartService.kt · InMemoryCartService.kt
      orders/            OrderDao.kt · SushiGardenDatabase.kt
      delivery/          CourierSimulator.kt
  di/
    AppModule.kt         Hilt bindings (real)
    TestModule.kt        Hilt bindings (IS_UI_TEST = true)
  navigation/
    NavGraph.kt          NavHost + Screen sealed class
    BottomNavBar.kt
  SushiGardenApp.kt      Application class (Hilt + Firebase init)
  MainActivity.kt
```

### MVVM per feature

```
Screen (@Composable)
  └── collectAsState() ← ViewModel (StateFlow<UiState>)
                               └── Service interfaces (injected by Hilt)
                                     ├── AuthService
                                     ├── MenuRepository
                                     ├── CartService
                                     ├── OrderDao
                                     └── CourierSimulator
```

### Authentication flow

```
App launch
  │
  ├─ authService.currentUser != null ──▶ MainGraph (bottom nav)
  │
  └─ null ──▶ AuthGraph
                  │
                  ├─ Register: name + email + password + consent checkbox
                  └─ Login:    email + password
                          │
                    Firebase Auth (prod) / FakeAuthService (IS_UI_TEST)
                          │
                    navigate to MainGraph
```

## 4. Design tokens (from Figma)

| Token | Value |
|-------|-------|
| Background | `#0F0F11` |
| Card surface | `#292830` |
| Tab bar | `#161616` |
| Accent red | `#EC1A35` |
| Primary text | `#FFFFFF` |
| Secondary text | `#6C6C74` |
| Icon inactive | `#4C4C4C` |
| Card corner radius | `12.4dp` |
| Font — primary | Sen Regular / Sen Bold (bundled) |
| Font — stepper | Mugesta (user-supplied) |

## 5. Screens

| # | Screen (Russian) | Tab / Entry | Key UI elements |
|---|------------------|-------------|-----------------|
| 1 | Регистрация | Auth gate | Name, email, password, consent checkbox, red button |
| 2 | Войти | Auth gate toggle | Email, password, show-password toggle |
| 3 | Каталог / Главная | Tab 1 | Banner carousel, category pills (Суши/Роллы/Горячие/Салаты/WOK), 2-col product grid, address header |
| 4 | Детали продукта | Tap product card | Large image, add-ons, qty stepper (Mugesta +/−), add to cart CTA |
| 5 | Акции | Tab 2 | Promo banners, ХОТ РОЛЛС |
| 6 | Заказы | Tab 3 | Empty state + order history list |
| 7 | Корзина | Tab 4 | Line items, qty steppers, add-ons, total, Оформить |
| 8 | Оформление заказа | Корзина → Оформить | Address, Кому/Телефон/Почта, Картой онлайн, totals, Подтвердить |
| 9 | Отслеживание | Checkout confirm | Google Maps + courier marker, Максим Винокур card, ETA countdown |
| 10 | Профиль | Tab 5 | Avatar, name, editable phone, order history rows, logout |

## 6. Services & data

- **Menu:** Local mock, same 5 categories and items as iOS (Хикари 620₽·255г, Лос-Анджелес 707₽·285г, Айдахо маки 810₽·285г, Осака маки 740₽·275г). Add-ons: Васаби, Имбирь, Соевый соус (60₽ each). Figma imageRefs used for product images via Figma REST API.
- **Cart:** In-memory `InMemoryCartService` (StateFlow). Clears on process death.
- **Orders:** Room `OrderDao` + `SushiGardenDatabase`. Placed orders persist across restarts.
- **Auth:** Firebase Auth KTX. `FirebaseAuthService` wraps Firebase with coroutines. `FakeAuthService` injected in UI test builds.
- **Delivery:** `CourierSimulator` — coroutine-based animation along a hardcoded polyline, ETA countdown via StateFlow.

## 7. Testing strategy (TDD — failing test first)

**Requirement: UI tests must cover every screen and every flow.**

### Unit tests (JUnit4 + MockK + coroutines-test)

- Every ViewModel: state transitions, loading, error handling
- `InMemoryCartService`: add, remove, qty increment/decrement, add-ons, totals, clear
- `CourierSimulator`: ETA math, progress updates
- `LocalMenuRepository`: all categories, all items
- `OrderDao`: create, read, list — against in-memory Room
- Form validators: email format, password rules, non-empty name, consent required

### UI tests (Compose UI Test + Espresso)

Launched with `BuildConfig.IS_UI_TEST = true` → `FakeAuthService` + pre-seeded data + in-memory Room. Fully offline.

Every screen gets a dedicated test class. Every distinct flow within each screen is a separate test:

| Screen | Flows covered |
|--------|--------------|
| Auth | Register success · Register disabled without consent · Register with invalid email · Register with short password · Toggle to login · Login success · Login with wrong credentials |
| Catalog | Category tab switch · Scroll product grid · Tap product → detail · Banner carousel interaction · Address header visible |
| Product Detail | Add-on selection · Qty stepper increment/decrement · Add to cart → cart badge updates · Back navigation |
| Promotions | Banners render · Promo scroll |
| Cart (empty) | Empty state visible · Оформить disabled |
| Cart (filled) | Item qty change · Remove item · Add-on toggle · Total updates · Оформить enabled |
| Checkout | All fields fillable · Подтвердить creates order + navigates to Tracking |
| Tracking | Map visible · Courier card (Максим Винокур) · ETA countdown visible · Back to orders |
| Orders (empty) | Empty state visible |
| Orders (filled) | Order row visible · Tap → order detail · Detail shows correct lines and totals |
| Profile | Name/email visible · Phone field editable · Order history rows · Logout → auth gate |

All interactive elements carry stable `testTag` IDs.

## 8. Screenshot comparison workflow

After each build phase, for every screen built in that phase:
1. Run app on `Pixel_8_API_36` emulator
2. Capture: `adb exec-out screencap -p > docs/screenshots/android/<screen>.png`
3. Run `/design-shotgun`: `$D compare --images "docs/screenshots/android/<screen>.png,<ios-screenshot>.png" --output <board>.html`
4. Review side-by-side board — fix any divergence from Figma before advancing to the next phase

iOS reference screenshots are in `~/Desktop/llm-ai-projects/sushi-garden-ios/docs/screenshots/`.

## 9. Build phases (each phase TDD'd)

- **P0 — Scaffold:** Gradle project, Hilt, design system (Color/Type/Spacing from Figma tokens), Sen font bundled, Figma image assets extracted into `res/drawable`
- **P1 — Shell + Auth:** `SushiGardenApp`, `MainActivity`, `NavGraph`, auth gate, Register + Login screens, Firebase + Fake auth, Hilt wiring
- **P2 — Catalog:** `LocalMenuRepository`, Catalog screen (banner carousel, category pills, product grid), `ProductDetailScreen`, `InMemoryCartService`
- **P3 — Cart + Checkout:** Cart screen (line items, steppers, add-ons, totals), Checkout screen, Room `OrderDao`, order creation flow
- **P4 — Promotions + Profile + Orders:** Promotions screen, Profile screen (editable phone), Orders list, OrderDetail screen
- **P5 — Tracking map:** Google Maps Compose SDK, `CourierSimulator`, TrackingScreen
- **P6 — UI-test sweep + design-shotgun:** Complete Compose UI test coverage for every screen and every flow, screenshot comparison board vs iOS for all 10 screens

## 10. Out of scope (v1)

- Real payment processing (UI only)
- Live courier tracking / real delivery backend
- Push notifications
- Firestore / remote menu or remote order storage
- Localization beyond Russian
