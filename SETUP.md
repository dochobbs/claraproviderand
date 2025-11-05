# Setup Guide - Clara Provider Android

Complete setup instructions for development environment.

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **Android SDK**: API 24-34 installed
- **Java Development Kit**: JDK 11+
- **Git**: For version control
- **Kotlin**: 1.9.20+ (included with Android Studio)

## Step 1: Clone Repository

```bash
git clone https://github.com/[your-username]/claraproviderandroid.git
cd clara-provider-app-android
```

## Step 2: Install Android SDK

1. Open Android Studio
2. **Tools** → **SDK Manager**
3. Install the following:
   - SDK Platform 34 (Android 14)
   - SDK Tools:
     - Android Emulator
     - Android SDK Build-Tools
     - Android SDK Platform-Tools
     - Google Play Services

## Step 3: Configure Supabase

### Get Supabase Credentials

1. Log in to https://supabase.com
2. Open your Clara Provider project
3. Go to **Settings** → **API**
4. Copy:
   - Project URL
   - `anon` key (for public access)
   - `service_role` key (for backend operations)

### Update SupabaseServiceBase.kt

Edit `app/src/main/java/com/clara/provider/services/SupabaseServiceBase.kt`:

```kotlin
private val supabaseUrl = "https://YOUR-PROJECT.supabase.co"
private val supabaseKey = "YOUR-SERVICE-ROLE-KEY"
private val supabaseAnonKey = "YOUR-ANON-KEY"
```

**Security Note**: For production, use environment variables or secure config:
```kotlin
// Better approach
private val supabaseUrl = BuildConfig.SUPABASE_URL
private val supabaseAnonKey = BuildConfig.SUPABASE_ANON_KEY
```

Then in `build.gradle.kts`:
```gradle
buildTypes {
    debug {
        buildConfigField("String", "SUPABASE_URL", "\"https://...\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"pk...\"")
    }
}
```

## Step 4: Configure Firebase Cloud Messaging

### Create Firebase Project

1. Go to https://console.firebase.google.com
2. Click **Add Project**
3. Name it "Clara Provider Android"
4. Complete the setup

### Download google-services.json

1. In Firebase Console, go to **Project Settings**
2. Click **Add App** → **Android**
3. Enter package name: `com.clara.provider`
4. Download `google-services.json`
5. Place it in `app/` directory (replace existing placeholder)

### Enable Cloud Messaging

1. In Firebase Console, go to **Cloud Messaging**
2. Note your **Sender ID** and **Server API Key**
3. Add these to Supabase:
   - In Supabase, go to **Auth** → **Providers**
   - Enable **Phone** if not already enabled
   - Configure with Firebase credentials for production

## Step 5: Open in Android Studio

1. **File** → **Open**
2. Select the `clara-provider-app-android` directory
3. Click **Open**
4. Wait for Gradle sync (first time takes 2-3 minutes)

### Troubleshooting Gradle Sync

If you see errors:

**Missing SDK Platform:**
```
Tools > SDK Manager > SDK Platforms
```
Install API 34

**Kotlin Plugin Issues:**
```
File > Settings > Languages & Frameworks > Kotlin > Kotlin Compiler
```
Update to version 1.9.20 or later

**Dependency Issues:**
```bash
./gradlew clean
./gradlew build
```

## Step 6: Create Virtual Device (Emulator)

1. **Tools** → **Device Manager**
2. Click **Create Device**
3. Select "Pixel 6" (or your preference)
4. Choose API 34 (Android 14)
5. Finish and wait for download

Or use a real Android device with USB debugging enabled.

## Step 7: Build and Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run on emulator/device
./gradlew installDebug
```

Or in Android Studio:
1. Select your device in the device dropdown
2. Click the **Run** button (▶)
3. App launches on device

## Step 8: Verify Setup

After app launches:

1. **Check Logs**
   ```
   View > Tool Windows > Logcat
   ```
   Look for "Clara Provider" logs, no errors

2. **Test Network**
   - App should load with placeholder UI
   - No crashes on startup

3. **Check Permissions**
   - Allow notifications when prompted
   - No permission denials in Logcat

## Configuration Files

### Local Development Config

Create `local.properties` (not in git):

```properties
sdk.dir=/Users/username/Library/Android/sdk
```

### Supabase Config Options

For different environments, create multiple build variants:

```gradle
flavorDimensions "environment"
productFlavors {
    dev {
        dimension "environment"
        buildConfigField("String", "SUPABASE_URL", "\"https://dev.supabase.co\"")
    }
    prod {
        dimension "environment"
        buildConfigField("String", "SUPABASE_URL", "\"https://prod.supabase.co\"")
    }
}
```

Then build with:
```bash
./gradlew assembleProdDebug
```

## Development Workflow

### Daily Development

```bash
# Update code
# ... make changes ...

# Build and run
./gradlew installDebug

# View logs
./gradlew logcat

# Run tests
./gradlew test
```

### Code Formatting

Android Studio has built-in formatting:
1. Select code
2. **Code** → **Reformat Code** (Cmd+Option+L on Mac)

Or use command line:
```bash
./gradlew spotlessApply  # if spotless plugin is configured
```

### Debugging

1. Set breakpoint (click line number in editor)
2. Run in debug mode (shift + F9)
3. Step through code
4. Inspect variables in Variables panel

## Troubleshooting

### App Crashes on Startup

**Check Logcat:**
```
View > Tool Windows > Logcat
```

Look for:
- `ApiException` - Supabase config issue
- `NetworkException` - Check Supabase URL
- `FileNotFound` - Check google-services.json placement

**Fix Supabase config:**
```kotlin
// In SupabaseServiceBase.kt, verify:
supabaseUrl = "https://YOUR-ID.supabase.co"  // No trailing slash
supabaseAnonKey = "eyJhbG..."  // Full key from Supabase
```

### Emulator Won't Start

```bash
# List available images
emulator -list-avds

# Start specific emulator
emulator -avd Pixel_6_API_34

# Or in Android Studio: Tools > Device Manager > Run
```

### Build Fails with Kotlin Errors

Update Kotlin:
1. **File** → **Settings** (Preferences on Mac)
2. **Languages & Frameworks** → **Kotlin**
3. Update compiler to 1.9.20+
4. Invalidate cache: **File** → **Invalidate Caches**

### Gradle Sync Takes Forever

```bash
# Clean and rebuild
./gradlew clean --stop
rm -rf .gradle
./gradlew build
```

### Firebase Not Initializing

1. Verify `google-services.json` is in `app/` folder
2. Check `build.gradle.kts` has:
   ```gradle
   id("com.google.gms.google-services")
   ```
3. Run `./gradlew clean` and rebuild

## Testing on Real Device

### Enable USB Debugging

1. **Settings** → **About Phone**
2. Tap **Build Number** 7 times (enables Developer Options)
3. Go to **Developer Options**
4. Enable **USB Debugging**
5. Connect device via USB
6. Accept the "Allow USB debugging" prompt

### Deploy to Device

```bash
# Install app
./gradlew installDebug

# Or just run
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Environment Variables

For CI/CD or local security, use environment variables:

```bash
# Set in terminal or ~/.zshrc
export SUPABASE_URL="https://YOUR-PROJECT.supabase.co"
export SUPABASE_ANON_KEY="pk_..."

# In build.gradle.kts
buildTypes {
    debug {
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL")}\"")
    }
}
```

## Next Steps

1. ✅ Setup complete
2. **Read** [ARCHITECTURE.md](ARCHITECTURE.md) for code structure
3. **Read** [SYNC_GUIDE.md](SYNC_GUIDE.md) for maintaining feature parity
4. **Explore** the codebase:
   - `models/` - Data structures
   - `services/` - API integration
   - `store/` - State management
   - `ui/` - UI components
5. **Build** the app and verify it runs
6. **Start** implementing screens

## Getting Help

### Resources

- [Android Developer Docs](https://developer.android.com)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Supabase Kotlin Client](https://github.com/supabase-community/supabase-kt)
- [Firebase Messaging](https://firebase.google.com/docs/cloud-messaging)

### Common Issues

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) (coming soon) for detailed solutions.

### Team Communication

- GitHub Issues for bugs/features
- Pull Requests for code review
- Keep [SYNC_GUIDE.md](SYNC_GUIDE.md) updated when syncing from iOS

---

**Setup Complete!** You're ready to start development. 🚀

For questions or issues, check the troubleshooting section or create a GitHub issue.
