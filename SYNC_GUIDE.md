# iOS → Android Sync Guide

This document explains the workflow for keeping the Android app in sync with iOS changes.

## Overview

The Android app is a faithful Kotlin port of the iOS app, maintaining feature parity while respecting platform differences. The main source of truth is the iOS repository.

**iOS Repo:** https://github.com/dochobbs/claraproviderios.git
**Android Repo:** (this repository)

## Sync Workflow

### 1. Monitor iOS Changes

```bash
# Check iOS repo for new commits
cd /tmp/claraproviderios
git fetch origin
git log --oneline origin/main -20

# Or check the iOS CHANGELOG
cat FEATURES.md  # Look for recent additions
```

### 2. Analyze Changes

Determine scope:
- **API Changes**: Supabase schema updates, endpoint changes
- **Feature Changes**: New screens, new review types, new patient fields
- **Bug Fixes**: Crash fixes, logic corrections, performance improvements
- **UI Changes**: Visual updates (mainly Android-specific)

### 3. Port to Android

Map iOS → Android concepts:

| iOS | Android | Notes |
|-----|---------|-------|
| SwiftUI View | Jetpack Compose @Composable | Visual equivalent |
| @Published | StateFlow | Reactive state |
| @StateObject | ViewModel | State container |
| Combine | Coroutines + StateFlow | Async programming |
| URLSession | OkHttp/Retrofit | HTTP client |
| Codable | Gson | JSON serialization |
| UserNotifications | Firebase Cloud Messaging | Push notifications |
| NavigationStack | NavController | Navigation |
| Model/Service/Store | Model/Service/Store | Same architecture |

### 4. Implementation Pattern

**For Data Models:**
```kotlin
// iOS: Codable struct
struct Message: Codable {
    let id: UUID
    let content: String
}

// Android: Data class with Gson
data class Message(
    val id: String,
    val content: String
)
```

**For Services:**
```kotlin
// iOS: Foundation + Combine
func fetchReviews() async throws -> [ProviderReviewRequestDetail]

// Android: Coroutines + Result
suspend fun fetchReviews(): Result<List<ProviderReviewRequestDetail>>
```

**For State Management:**
```kotlin
// iOS: @StateObject with @Published
@StateObject var store = ProviderConversationStore()

// Android: ViewModel with StateFlow
val store: ProviderConversationStore = viewModel()
val reviews = store.reviewRequests.collectAsState()
```

**For UI:**
```kotlin
// iOS: SwiftUI with NavigationStack
NavigationStack {
    ConversationListView()
}

// Android: Compose with NavController
NavHost(navController, startDestination = "list") {
    composable("list") { ConversationListScreen() }
}
```

### 5. Testing Changes

After porting:

```bash
# Build the app
./gradlew build

# Run unit tests
./gradlew test

# Run instrumentation tests (on device/emulator)
./gradlew connectedAndroidTest

# Manual testing
# - Launch app on emulator
# - Verify feature works identically to iOS
# - Test error states
# - Check performance
```

### 6. Commit & Document

```bash
# Commit with clear message
git add -A
git commit -m "SYNC: Port [feature name] from iOS

- Mirror iOS changes in [file names]
- Maintain feature parity with iOS version
- Follow Kotlin/Compose conventions
- All tests passing"

# Document in sync log
echo "## $(date): Synced [feature]
- iOS commit: [hash]
- Android files: [list]
" >> SYNC_LOG.md
```

## Common Sync Patterns

### Pattern 1: API Endpoint Change

**iOS Change**: Supabase endpoint returns new field

```swift
// iOS example
let response = try await supabase.from("reviews")
    .select("*, newField")
    .execute()
```

**Android Port**:
```kotlin
// Android equivalent
val response = supabaseService.makeGetRequest(
    "reviews?select=*,newField"
)
```

**Steps**:
1. Update Model to include newField
2. Update ProviderSupabaseService query
3. Update UI to use newField
4. Test parsing

### Pattern 2: New Feature Screen

**iOS**: Adds PatientProfileView

**Android Steps**:
1. Create data model if needed
2. Create ViewModel/Store logic
3. Create Composable screen
4. Add navigation route
5. Wire up in MainApp

Example structure:
```
iOS PatientProfileView.swift
  ↓
Android PatientProfileScreen.kt
  ├─ Uses ProviderConversationStore (existing)
  ├─ Displays ChildProfile model (port model)
  └─ Follows same UI patterns
```

### Pattern 3: Bug Fix

**iOS**: Fixes retry logic in API client

**Android Steps**:
1. Locate equivalent code in SupabaseServiceBase or ProviderSupabaseService
2. Apply same fix pattern
3. Test the specific error scenario
4. Verify behavior matches iOS

### Pattern 4: UI Polish

**iOS**: Updates color scheme, typography

**Android Steps**:
1. Update Theme.kt colors/typography
2. Apply Material Design 3 conventions
3. Test on both light and dark modes
4. Verify accessibility

## Sync Priority Levels

**High Priority** (sync immediately):
- API changes
- Data model changes
- Security fixes
- Critical bug fixes

**Medium Priority** (sync next release):
- New features
- UI/UX improvements
- Performance optimizations

**Low Priority** (optional):
- Code style/formatting
- Comment updates
- Documentation

## File Mapping Reference

Quick lookup for equivalent files:

| iOS | Android |
|-----|---------|
| Models/ProviderReviewRequestDetail.swift | models/ProviderReviewRequestDetail.kt |
| Services/ProviderSupabaseService.swift | services/ProviderSupabaseService.kt |
| Services/SupabaseServiceBase.swift | services/SupabaseServiceBase.kt |
| Store/ProviderConversationStore.swift | store/ProviderConversationStore.kt |
| Views/ConversationListView.swift | ui/screens/ConversationListScreen.kt |
| Views/ConversationDetailView.swift | ui/screens/ConversationDetailScreen.kt |
| Views/PatientProfileView.swift | ui/screens/PatientProfileScreen.kt |
| Clara_ProviderApp.swift | MainActivity.kt + MainApp.kt |

## Checking for Drift

To verify Android hasn't drifted from iOS:

```bash
# 1. Compare model definitions
diff ios-models android-models  # conceptually

# 2. Check API calls are identical
# - Same endpoints
# - Same request body format
# - Same response parsing

# 3. Verify feature completeness
# - iOS has feature X?
# - Android has feature X?
# - Do they behave identically?

# 4. Run both apps side-by-side
# - Same data displayed?
# - Same user interactions?
# - Same error messages?
```

## Long-Term Strategy

**Monthly Sync:**
- Review iOS commits from past month
- Port any major changes
- Test thoroughly
- Update version number

**Quarterly Review:**
- Check for architectural divergence
- Update documentation
- Plan tech debt
- Assess feature parity

**Major iOS Release:**
- Full feature comparison
- Priority port plan
- Coordinated release
- Version alignment

## Tools & Resources

### iOS Repository
```bash
# Clone for reference
git clone https://github.com/dochobbs/claraproviderios.git
cd claraproviderios

# View architecture
cat ARCHITECTURE.md

# Check recent changes
git log --oneline -50
```

### Android Build
```bash
# Build changes
./gradlew build

# Run tests
./gradlew test

# Check for issues
./gradlew lint
```

## Common Pitfalls

### ❌ Don't: Copy iOS code directly
```kotlin
// BAD: Literal Swift translation
let reviews = try await store.loadReviewRequests()
```

### ✅ Do: Use Kotlin idioms
```kotlin
// GOOD: Kotlin coroutines + Result
store.loadReviewRequests() // suspends, updates StateFlow
```

### ❌ Don't: Ignore platform differences
```kotlin
// BAD: Push notifications like iOS
UserDefaults.standard.set(token, forKey: "device_token")
```

### ✅ Do: Use Android APIs
```kotlin
// GOOD: Firebase Cloud Messaging
FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
    // Register token
}
```

### ❌ Don't: Forget Android specifics
- Navigation patterns
- Lifecycle awareness
- Back button behavior
- System permissions

### ✅ Do: Embrace Android
- Use NavController
- Observe ViewModel lifecycle
- Handle back stack
- Request permissions properly

## Questions?

If unsure about how to port something:
1. Check ARCHITECTURE.md in both repos
2. Review similar implemented features
3. Check Android/Kotlin conventions
4. Test thoroughly before committing
