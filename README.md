# clara-provider-app Android

A modern Kotlin + Jetpack Compose Android application for healthcare providers to review and respond to patient triage requests and consultation outcomes in real-time.

## Overview

**clara-provider-app** enables healthcare providers to:
- Review incoming patient triage and consultation requests
- Access comprehensive patient medical histories
- Provide clinical feedback and medical responses
- Track review request statuses (pending, responded, flagged, escalated)
- Receive real-time push notifications for new requests
- Manage conversations with patients through the Clara platform

## Key Features

✅ **Review Management**
- Real-time list of patient review requests
- Status-based filtering (Pending, All, Flagged)
- Full-text search by conversation title or patient name
- Pull-to-refresh functionality

✅ **Detailed Review Interface**
- Complete conversation history with triage outcomes
- Patient information cards with medical background
- Support for multiple provider response types (Agree, Agree with Thoughts, Disagree with Thoughts, Escalation)
- Message composition and submission

✅ **Patient Information**
- Comprehensive patient profiles
- Medical history including:
  - Current medications
  - Known allergies
  - Past medical conditions
  - Clinical notes

✅ **Smart Notifications**
- Remote push notifications for new requests (Firebase Cloud Messaging)
- Real-time badge count updates
- Background notification handling

✅ **Real-time Sync**
- Automatic 60-second auto-refresh of review list
- Conversation caching for optimal performance
- Seamless status updates across views

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with Kotlin Coroutines for state management:

```
clara-provider-app-android/
├── app/
│   ├── src/main/java/com/clara/provider/
│   │   ├── MainActivity.kt              # App entry point
│   │   ├── store/
│   │   │   └── ProviderConversationStore  # Central state (ViewModel)
│   │   ├── services/
│   │   │   ├── SupabaseServiceBase       # HTTP client base
│   │   │   ├── ProviderSupabaseService   # API client
│   │   │   └── ClaraMessagingService     # Push notifications (FCM)
│   │   ├── models/
│   │   │   └── ProviderReviewRequestDetail  # Core data models
│   │   └── ui/
│   │       ├── theme/                   # Material Design 3 theming
│   │       ├── screens/
│   │       │   ├── MainApp              # Navigation root
│   │       │   └── ConversationListScreen
│   │       └── components/              # Reusable composables
│   ├── build.gradle.kts                 # App build configuration
│   └── AndroidManifest.xml
├── build.gradle.kts                    # Project build configuration
├── settings.gradle.kts                 # Gradle settings
└── README.md
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for comprehensive technical details.

## Prerequisites

- **Android Studio** Hedgehog or later
- **Android SDK** 24+ (minimum API level)
- **Target SDK** 34 (Android 14)
- **Kotlin** 1.9.20+
- **Java** 8+

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/dochobbs/claraproviderandroid.git
cd clara-provider-app-android
```

### 2. Configure Supabase Credentials

Edit `app/src/main/java/com/clara/provider/services/SupabaseServiceBase.kt`:
- Update `supabaseUrl` with your Supabase project URL
- Update `supabaseKey` with your service role key
- Update `supabaseAnonKey` with your anonymous key

### 3. Configure Firebase Cloud Messaging

1. Create a Firebase project at https://console.firebase.google.com
2. Download `google-services.json` from Firebase console
3. Replace `app/google-services.json` with your downloaded file

### 4. Open in Android Studio

```bash
# Open the project
open -a "Android Studio" .

# Or use Android Studio UI to open the directory
```

### 5. Build and Run

1. Select a target device/emulator
2. Click **Run** (Shift + F10)
3. App launches with placeholder UI

## Development

### Project Structure

- **Models**: Data structures for Supabase integration
- **Services**: Backend communication and push notification handling
- **Store**: Centralized state management with ViewModel + StateFlow
- **UI**: Jetpack Compose screens and components

### Key Technologies

- **Language**: Kotlin
- **Framework**: Jetpack Compose
- **State Management**: ViewModel + StateFlow (reactive)
- **Backend**: Supabase (PostgreSQL + REST API)
- **Networking**: OkHttp + Retrofit
- **Push Notifications**: Firebase Cloud Messaging
- **Architecture**: MVVM with Coroutines

### Development Guidelines

- Uses MVVM architecture with clear separation of concerns
- Comprehensive error handling with detailed logging
- Graceful fallbacks for API changes
- Memory-efficient caching for conversation details
- Responsive UI with reactive state updates

## API Integration

The app integrates with **Supabase REST API** for:

**Review Request Operations:**
- Fetch review requests with optional status filtering
- Get pending/escalated/flagged reviews
- Fetch detailed conversation with retry logic

**Messaging:**
- Send provider responses to patients
- Fetch follow-up messages
- Create patient notifications

**Status Management:**
- Update review statuses
- Add provider responses with urgency levels
- Track response timestamps

See [ARCHITECTURE.md](ARCHITECTURE.md) for complete API documentation.

## Build Variants

```bash
# Debug build (development)
./gradlew assembleDebug

# Release build (production)
./gradlew assembleRelease

# With code signing (requires keystore)
./gradlew assembleRelease -PkeyStore=path/to/keystore
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumentation tests (Android device/emulator required)
./gradlew connectedAndroidTest
```

## Known Issues and TODOs

- **User Authentication**: Currently uses "default_user" placeholder - replace with actual user ID system
- **Child Profiles**: May require separate API endpoint if stored differently
- **Full UI Implementation**: Screens are placeholders, ready for feature development
- **Dashboard Stats**: Framework ready but statistics calculation pending

## Push Notifications Setup

To enable push notifications:

1. Create Firebase project and enable Cloud Messaging
2. Download `google-services.json` and add to `app/`
3. Device tokens are automatically registered on app launch
4. Badge counts automatically update with pending review count

See [SETUP.md](SETUP.md) for detailed configuration instructions.

## Git Workflow - Syncing with iOS

This Android app is maintained in sync with the iOS version:

**Main iOS Repository:** https://github.com/dochobbs/claraproviderios.git

**Sync Strategy:**
1. Monitor iOS repo for changes
2. Port relevant changes to Android following Kotlin/Compose conventions
3. Maintain feature parity between platforms
4. Document cross-platform differences

```bash
# Check iOS changes
git log origin/main..main -- ../../../GIT/vhs

# Port changes to Android
# (Use Android-specific patterns while maintaining API compatibility)
```

## Contributing

When contributing to this project:

1. Follow the existing MVVM architecture
2. Add comprehensive error handling
3. Include Compose previews for new UI components
4. Test on both light and dark modes
5. Update documentation for new features
6. Ensure code formatting with standard Kotlin style

## License

[Add your license here]

## Support

For issues, questions, or contributions, please create a GitHub issue or contact the development team.

---

**Status**: Initial Setup Complete
**Last Updated**: November 2024
**Version**: 1.0.0 (Initial)

For more details, see:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Complete system design
- [SETUP.md](SETUP.md) - Detailed development setup
- [SYNC_GUIDE.md](SYNC_GUIDE.md) - iOS to Android sync workflow
