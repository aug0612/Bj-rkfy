# Deploying rodrigofy from Termux (Android)

This guide covers pushing this project to GitHub directly from an Android
phone using [Termux](https://termux.dev/), no laptop required.

## 1. Install Termux + Git

Install Termux from F-Droid (recommended) or GitHub releases — the Play
Store build is outdated and unmaintained.

```bash
pkg update -y
pkg install git -y
```

## 2. Fix Git's "dubious ownership" / safe.directory restriction

Android's shared storage permission model makes Git treat the project
folder as untrusted by default, which blocks every command with a
"detected dubious ownership" error. Fix it once, globally:

```bash
git config --global --add safe.directory '*'
```

(If you'd rather scope it to just this repo instead of `'*'`, use the
absolute path: `git config --global --add safe.directory /path/to/rodrigofy`.)

## 3. Configure your Git identity

```bash
git config --global user.name "your-username"
git config --global user.email "you@example.com"
```

## 4. Get the project onto your phone

If you unzipped `rodrigofy.zip` into your Termux home directory, `cd` into
it:

```bash
cd ~/rodrigofy
```

## 5. Initialize and push

```bash
git init
git add .
git commit -m "Initial commit — SOUR era 💜"
git branch -M main
git remote add origin https://github.com/<your-username>/rodrigofy.git
git push -u origin main
```

## 6. Authentication

GitHub disabled password auth over HTTPS. When prompted for a password,
paste a **Personal Access Token** instead:

1. On GitHub: Settings → Developer settings → Personal access tokens →
   Tokens (classic) → Generate new token.
2. Grant it the `repo` scope.
3. Use the token as your password when Git prompts you.

Alternatively, set up SSH keys in Termux (`ssh-keygen -t ed25519`, then add
the public key to GitHub → Settings → SSH and GPG keys) and use an SSH
remote instead:

```bash
git remote set-url origin git@github.com:<your-username>/rodrigofy.git
```

## 7. Building the APK from Termux (optional)

Full Android SDK builds inside Termux are possible but heavyweight
(via `termux-android-sdk` community setups). For most people it's easier
to push to GitHub from Termux and then open the repo in **Android Studio**
on a desktop to build and sign the APK.
