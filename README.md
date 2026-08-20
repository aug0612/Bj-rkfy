# 🎵 Spotify Material You Expressive 3 Client

Une application cliente Spotify moderne conçue pour Android avec **Jetpack Compose** et **Material 3 Expressive Design**.

## ✨ Fonctionnalités
- 🎨 **Material 3 Expressive UI** : Dynamic Colors (Material You), typographie expressive, composants tonaux elevation.
- 🎧 **Mini-Lecteur Flottant & Plein Écran** : Mini-barre de contrôle dynamique et vue détaillée du titre.
- 🔐 **OAuth 2.0 PKCE** : Architecture sécurisée pour l'authentification Spotify sans secret client embarqué.
- ⚡ **Ktor & Coroutines** : Client HTTP réactif et fluide.

---

## 🚀 Comment lancer le projet sur ton PC

### 1. Cloner le dépôt
```bash
git clone https://github.com/TON_PSEUDO/TON_REPO.git
cd SpotifyMaterialExpressive
```

### 2. Configurer le Client ID Spotify
1. Rends-toi sur le [Spotify Developer Dashboard](https://developer.spotify.com/dashboard).
2. Crée une application et ajoute `spotifyclient://callback` dans **Redirect URIs**.
3. Copie ton **Client ID** et colle-le dans `app/src/main/java/com/example/spotifymaterial/network/AuthManager.kt`.

### 3. Compiler & Lancer
Tu peux ouvrir ce dossier directement dans **Android Studio** (Recommandé : Android Studio Koala ou plus récent), ou compiler via le terminal :

```bash
# Sous Linux / macOS
./gradlew assembleDebug

# Sous Windows (PowerShell / CMD)
.\gradlew.bat assembleDebug
```

L'APK généré se trouvera dans `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📤 Transférer ce projet vers ton dépôt GitHub

Exécute ces commandes dans le dossier du projet :

```bash
git init
git add .
git commit -m "feat: initialisation Spotify Client Material You Expressive 3"
git branch -M main
git remote add origin https://github.com/TON_PSEUDO/TON_REPO.git
git push -u origin main
```
