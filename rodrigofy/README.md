# 💜 rodrigofy <sub><sup>(a.k.a. GUTSfy)</sup></sub>

**you seem pretty sad for a girl in love with a stock music app. let's fix that.**

An unofficial, fan-made Spotify client for Android — built with **Kotlin**,
**Jetpack Compose**, and **Material 3 Expressive** — wrapped head to toe in
Olivia Rodrigo's *SOUR* and *GUTS* aesthetic. Deep purples, punk-pop energy,
tonal cards, and a floating player bar that follows you around like a
recurring thought about your ex.

> ⚠️ **Disclaimer**: rodrigofy is an independent, open-source fan project.
> It is **not affiliated with, endorsed by, or sponsored by** Spotify AB,
> Olivia Rodrigo, Geffen Records, or Interscope Records. No copyrighted
> audio, lyrics, or artwork ships with this repository — track and album
> **names** are used only as thematic metadata for the demo catalogue.

---

## 🩹 What's inside (a.k.a. the track listing)

Every feature gets a song to hide behind. Sue us.

| Feature | Track-coded name | What it actually does |
|---|---|---|
| Reactive playback core | **GUTS Engine** | A Kotlin `StateFlow`-driven `PlayerViewModel` that ticks playback position, shuffle, and repeat state in real time — no lag, no logical explanation needed. |
| Secure login | **vampire Security** | OAuth 2.0 Authorization Code flow **+ PKCE** — no client secret ever lives on-device. It doesn't need to be invited in to steal your session. |
| Encrypted token storage | **traitor-proof storage** | Access & refresh tokens live in `EncryptedSharedPreferences` (AES-256-GCM, Android Keystore-backed). Nothing sensitive touches plaintext. |
| Theming system | **SOUR Aesthetics** | A full Material 3 color scheme in pastel-to-deep purple, `Dynamic Color` support on Android 12+, and an expressive, extra-bold type scale. |
| Home feed | **good 4 u Home Screen** | Tonal playlist cards + a scrollable track list, built entirely with `LazyColumn`/`LazyRow` and Material 3 `Card`/`ListItem`. |
| Full-screen player | **PlayerScreen** | Big cover art, a `LargeFloatingActionButton` play/pause control, scrubber, shuffle & repeat — GUTS tour-poster energy. |
| Floating mini player | **ExpressivePlayerBar** | A rounded, tonal capsule that hovers above the Home screen and expands into the full player on tap. |
| Offline fallback | **"logical" mode** | Every API call gracefully degrades to a bundled demo catalogue (`DemoData.kt`) when there's no token, no network, or no premium account — the app is never empty-handed. |

---

## 🖤 Tech stack

- **Kotlin** + **Jetpack Compose** (declarative UI, no XML layouts)
- **Material 3** — tonal color roles, expressive typography & shapes, `Dynamic Color`
- **Navigation Compose** — single-Activity, three-destination graph (Login → Home → Player)
- **Ktor Client** (Android engine) — talks to the Spotify Web API, with `kotlinx.serialization` for JSON
- **AndroidX Security Crypto** — `EncryptedSharedPreferences` for token storage
- **Coroutines & `StateFlow`** — reactive playback + auth state
- **Gradle Version Catalog** (`libs.versions.toml`) — centralized dependency management

## 📁 Project structure

```
rodrigofy/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml          # spotifyclient://callback intent-filter
│       ├── java/com/rodrigofy/app/
│       │   ├── MainActivity.kt          # entry point, OAuth redirect handling
│       │   ├── RodrigofyApplication.kt
│       │   ├── auth/
│       │   │   ├── AuthManager.kt       # OAuth 2.0 + PKCE flow
│       │   │   └── PkceUtil.kt          # code_verifier / code_challenge helpers
│       │   ├── data/
│       │   │   ├── SpotifyModels.kt     # @Serializable data classes
│       │   │   ├── SpotifyApiService.kt # Ktor client + demo fallback
│       │   │   └── DemoData.kt          # offline SOUR / GUTS catalogue
│       │   ├── navigation/
│       │   │   └── RodrigofyNavGraph.kt
│       │   ├── ui/
│       │   │   ├── theme/               # Color.kt, Type.kt, Theme.kt
│       │   │   ├── components/
│       │   │   │   └── ExpressivePlayerBar.kt
│       │   │   └── screens/
│       │   │       ├── LoginScreen.kt
│       │   │       ├── HomeScreen.kt
│       │   │       └── PlayerScreen.kt
│       │   └── viewmodel/
│       │       └── PlayerViewModel.kt
│       └── res/                         # themes, colors, adaptive launcher icon
├── gradle/libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── app/build.gradle.kts
```

---

## 🚀 Getting started

### 1. Register a Spotify app

1. Go to the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard) and create an app.
2. Add this exact **Redirect URI**: `spotifyclient://callback`
3. Copy your **Client ID**.

### 2. Plug in your Client ID

Open `app/build.gradle.kts` and replace the placeholder:

```kotlin
buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"YOUR_SPOTIFY_CLIENT_ID\"")
```

with your real Client ID. (For a production app, prefer reading it from
`local.properties` / a Gradle property so it never gets committed.)

### 3. Build & run

Open the project in **Android Studio** (Ladybug or newer) and hit Run — or
from the command line:

```bash
./gradlew assembleDebug
```

The APK lands in `app/build/outputs/apk/debug/`.

> No Spotify account handy, or not Premium? The app works out of the box in
> **demo mode** — `HomeScreen` and `PlayerScreen` are fully browsable using
> the bundled SOUR/GUTS catalogue, no login required.

---

## 🧵 Known limitations

- Actual audio playback (via the Spotify App Remote SDK or Web Playback SDK)
  is **not wired up** — `PlayerViewModel` simulates a playback clock so the
  UI is fully functional and demoable without a Premium account or the
  (separately licensed) Spotify App Remote SDK.
- Album art uses placeholder images, not real Spotify artwork.
- This is a **portfolio / learning project**, not a Spotify App Remote
  Extended Access production app.

---

## 📦 Deploying from your phone (Termux)

If you're pushing this repo straight from an Android phone via **Termux**,
Android's storage permissions will trip Git's ownership check. Here's the
exact sequence:

### Fix the Git "dubious ownership" restriction

```bash
git config --global --add safe.directory '*'
```

### Step-by-step push to GitHub

```bash
# 1. Install git if you haven't already
pkg install git -y

# 2. Set your identity (once per Termux install)
git config --global user.name "your-username"
git config --global user.email "you@example.com"

# 3. Move into the project folder
cd rodrigofy

# 4. Initialize the repo
git init

# 5. Stage everything
git add .

# 6. Commit — main character energy
git commit -m "Initial commit — SOUR era 💜"

# 7. Rename the default branch to main
git branch -M main

# 8. Point it at your GitHub repo (create the empty repo on GitHub first)
git remote add origin https://github.com/<your-username>/rodrigofy.git

# 9. Push
git push -u origin main
```

If GitHub asks for a password, use a **Personal Access Token** (Settings →
Developer settings → Personal access tokens) instead of your account
password — GitHub no longer accepts plain passwords over HTTPS.

---

## 📄 License

MIT — see [LICENSE](./LICENSE). This project is unaffiliated fan work; see
the disclaimer at the top of this README and in the license file.

---

<p align="center"><i>brutal honesty: thanks for reading this far. now go build something you're not embarrassed of.</i></p>
