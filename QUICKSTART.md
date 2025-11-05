# Quick Start - Clara Provider Android

Get the Android app running in 10 minutes.

## TL;DR - Fastest Path

```bash
# 1. Clone
git clone https://github.com/[your-username]/claraproviderandroid.git
cd clara-provider-app-android

# 2. Configure Supabase
# Edit: app/src/main/java/com/clara/provider/services/SupabaseServiceBase.kt
# Update: supabaseUrl, supabaseKey, supabaseAnonKey

# 3. Open in Android Studio
open -a "Android Studio" .

# 4. Wait for Gradle sync
# (Watch the "Gradle: Build" panel at bottom)

# 5. Select device and run
# - Device dropdown → select emulator or connected device
# - Click green Run button (▶)
```

**Expected Result:** App opens with "Conversation List - Coming Soon" placeholder UI

## Prerequisites Check

```bash
# Verify Android Studio installed
which android  # or open -a "Android Studio"

# Verify Gradle
./gradlew --version

# Verify Git
git --version
```

## One-Time Setup

### Step 1: Get Supabase Credentials (5 min)

1. Go to https://supabase.com → your Clara project
2. **Settings** → **API**
3. Copy these three values:
   - Project URL (looks like `https://xxxxx.supabase.co`)
   - `anon` key (starts with `eyJhb...`)
   - `service_role` key (also starts with `eyJhb...`)

### Step 2: Update SupabaseServiceBase.kt (2 min)

File: `app/src/main/java/com/clara/provider/services/SupabaseServiceBase.kt`

Change these lines:
```kotlin
// Line ~11
private val supabaseUrl = "https://YOUR-PROJECT.supabase.co"  // Paste URL here

// Line ~12
private val supabaseKey = "eyJhb..."  // Paste service_role key here

// Line ~13
private val supabaseAnonKey = "eyJhb..."  // Paste anon key here
```

### Step 3: Get Firebase Config (3 min)

1. Go to https://console.firebase.google.com → Create new project or use existing
2. **Project Settings** → Download `google-services.json`
3. Copy file to `app/google-services.json` (replace the placeholder)

### Step 4: Run App (remaining time)

1. Open Android Studio
2. Wait for Gradle sync (green checkmark appears)
3. Click the green **Run** button (▶) or press Shift+F10
4. Select device and wait for app to launch

## Verify It Works

**Expected on first launch:**
- ✅ App doesn't crash
- ✅ Placeholder UI shows "Conversation List - Coming Soon"
- ✅ No red errors in Logcat

**If you see errors:**
- Check Logcat (View → Tool Windows → Logcat)
- Most likely: Supabase credentials not updated
- Fix: Update SupabaseServiceBase.kt and rebuild

## Project Structure (for reference)

```
clara-provider-app-android/
├── README.md           ← Overview
├── SETUP.md            ← Detailed setup
├── ARCHITECTURE.md     ← Technical design
├── SYNC_GUIDE.md       ← iOS sync workflow
├── app/
│   ├── build.gradle.kts        ← Dependencies
│   ├── google-services.json    ← Firebase config
│   └── src/main/java/com/clara/provider/
│       ├── MainActivity.kt      ← Entry point
│       ├── models/              ← Data models
│       ├── services/            ← API & notifications
│       ├── store/               ← State management
│       └── ui/                  ← UI screens & theme
└── build.gradle.kts
```

## Common Issues & Quick Fixes

### ❌ "Failed to resolve Supabase"
**Issue**: Gradle can't download dependencies
**Fix**: Check internet, or run `./gradlew clean && ./gradlew build`

### ❌ "API key invalid" (in Logcat)
**Issue**: Supabase credentials wrong
**Fix**: Double-check you copied the full key from Supabase console

### ❌ Emulator won't start
**Issue**: No Android Virtual Device (AVD)
**Fix**:
```bash
# In Android Studio: Tools > Device Manager > Create Device
# Or: emulator -avd Pixel_6_API_34
```

### ❌ Gradle sync stuck
**Issue**: Network issue or cache corruption
**Fix**:
```bash
./gradlew clean
rm -rf .gradle
./gradlew build
```

## Next Steps

After app is running:

1. **Read** [README.md](README.md) for feature overview
2. **Read** [ARCHITECTURE.md](ARCHITECTURE.md) to understand code structure
3. **Explore** the source code:
   - Models in `models/ProviderReviewRequestDetail.kt`
   - Services in `services/ProviderSupabaseService.kt`
   - State in `store/ProviderConversationStore.kt`
4. **Start** implementing UI screens

## Development Tips

```bash
# Build without running
./gradlew build

# Run tests
./gradlew test

# View logs in real-time
./gradlew logcat | grep -i "clara"

# Clean before troubleshooting
./gradlew clean

# Install on device without running
./gradlew installDebug
```

## Helpful Resources

- **Android Docs**: https://developer.android.com
- **Jetpack Compose**: https://developer.android.com/jetpack/compose/documentation
- **Supabase**: https://supabase.com/docs
- **Firebase**: https://firebase.google.com/docs
- **Kotlin**: https://kotlinlang.org/docs

## Still Stuck?

1. Check [SETUP.md](SETUP.md) for detailed instructions
2. See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) (when available)
3. Check Logcat for specific error messages
4. Create a GitHub issue with:
   - Error message from Logcat
   - Steps you took
   - OS & Android Studio version

---

That's it! 🎉 You should now have the Clara Provider Android app running locally.

**Next**: Read [ARCHITECTURE.md](ARCHITECTURE.md) to understand how it all fits together.
