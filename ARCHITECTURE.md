# clara-provider-app Android - Architecture Documentation

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Data Flow](#data-flow)
3. [Module Breakdown](#module-breakdown)
4. [API Integration](#api-integration)
5. [State Management](#state-management)
6. [Screen Hierarchy](#screen-hierarchy)

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI Layer                      │
├─────────────────────────────────────────────────────────────────┤
│ ConversationListScreen │ ConversationDetailScreen │ PatientProfile
└─────────────┬─────────────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────────┐
│     State Management (ViewModel + StateFlow + Coroutines)        │
├─────────────────────────────────────────────────────────────────┤
│      ProviderConversationStore (ViewModel)                       │
│  - reviewRequests: StateFlow<List<...>>                          │
│  - conversationDetailsCache: StateFlow<Map<...>>                 │
│  - Auto-refresh via Timer (60s)                                 │
└─────────────┬─────────────────────────────────────────────────────┘
              │
     ┌────────┴────────┐
     ▼                 ▼
┌─────────────┐  ┌────────────────────┐
│  Services   │  │  Notifications     │
├─────────────┤  ├────────────────────┤
│ Supabase    │  │ Firebase Cloud     │
│ REST API    │  │ Messaging          │
│ OkHttp      │  │ Push Notifications │
└────────────┬┘  └────────┬───────────┘
             │             │
             └─────┬───────┘
                   ▼
        ┌──────────────────────────┐
        │ Supabase PostgreSQL API  │
        │ - provider_review_req    │
        │ - follow_up_messages     │
        │ - patients               │
        │ - conversations          │
        └──────────────────────────┘
```

### Architectural Patterns

**MVVM (Model-View-ViewModel)**
- **Models**: Data structures (ProviderReviewRequestDetail, Message, ChildProfile)
- **Views**: Jetpack Compose screens that observe state changes
- **ViewModel**: ProviderConversationStore manages state and business logic

**Reactive Programming**
- Uses Kotlin Coroutines + StateFlow for reactive state updates
- @Composable functions automatically recompose on state changes
- Eliminates callback hell and manual refresh logic

**Dependency Injection**
- Services passed through screen constructors
- State injected via ViewModel
- Enables testing and code reusability

## Data Flow

### 1. App Initialization Flow

```
MainActivity.onCreate()
├─> Activity window setup
├─> Create ClaraProviderAppTheme
├─> Create ProviderConversationStore (ViewModel)
├─> Compose MainApp() root
└─> MainApp calls ConversationListScreen()
    └─> onAppear: Store.loadReviewRequests()
```

### 2. Loading Review Requests

```
User Opens App
└─> ConversationListScreen.onAppear()
    └─> Store.loadReviewRequests()
        ├─> Set isLoading = true
        ├─> Call ProviderSupabaseService.fetchReviewRequests()
        │   ├─> OkHttp GET /provider_review_requests
        │   ├─> Parse JSON response
        │   └─> Return List<ProviderReviewRequestDetail>
        ├─> Update reviewRequests StateFlow
        ├─> Update badgeCount (count pending)
        ├─> Set isLoading = false
        └─> UI automatically recomposes with new data
```

### 3. Conversation Detail Flow

```
User Taps Review Item
└─> Store.loadConversationDetail(conversationId)
    ├─> Check conversationDetailsCache
    │   └─> If cached, return immediately
    ├─> If not cached:
    │   ├─> Call ProviderSupabaseService.fetchConversationDetail()
    │   ├─> Retry up to 3 times on failure
    │   └─> Store in cache
    └─> UI observes conversationDetailsCache StateFlow
        └─> ConversationDetailScreen recomposes
```

### 4. Submit Provider Response

```
User Submits Response
└─> Store.submitProviderResponse(...)
    ├─> Set isLoading = true
    ├─> Call ProviderSupabaseService.submitProviderResponse()
    │   ├─> HTTP PATCH /provider_review_requests
    │   └─> Include response content & type
    ├─> Update conversationDetailsCache
    ├─> Update reviewRequests in list
    ├─> Update badgeCount
    ├─> Set isLoading = false
    └─> UI shows success state
```

## Module Breakdown

### Models (`models/`)

**ProviderReviewRequestDetail.kt**
- `ProviderReviewRequestDetail`: Core review request model
  - id, conversationId, conversationTitle
  - Child info (name, age, DOB)
  - triageOutcome, status, timestamps
  - conversationMessages: List<Message>
  - providerResponse: ProviderResponse?

- `Message`: Individual message in conversation
  - content, isFromUser, timestamp
  - Optional: triageOutcome, providerName

- `ProviderResponse`: Provider's clinical response
  - responseType (agree, disagree, escalation)
  - content, urgencyLevel, timestamps

- `ChildProfile`: Patient information
  - Demographics, medications, allergies
  - pastMedicalHistory, clinicalNotes

- Enums: `ReviewStatus`, `TriageOutcome` for type safety

### Services (`services/`)

**SupabaseServiceBase.kt**
- Base HTTP client with authentication
- OkHttpClient with header injection
- GET, POST, PATCH, DELETE request methods
- Common error handling

**ProviderSupabaseService.kt**
- Provider-specific API operations
- `fetchReviewRequests()` - list with optional filter
- `fetchConversationDetail()` - with retry logic
- `submitProviderResponse()` - save clinical feedback
- `updateReviewStatus()` - status transitions
- `flagReview()` - escalation
- `searchReviews()` - query by title/patient

**ClaraMessagingService.kt**
- Firebase Cloud Messaging service
- `onMessageReceived()` - handle push notifications
- `onNewToken()` - register new device tokens
- Notification channel creation (Android 8+)
- Data payload handling

### Store (`store/`)

**ProviderConversationStore.kt** (ViewModel)
- Centralized state management
- StateFlow collections:
  - reviewRequests, selectedStatus
  - conversationDetailsCache
  - isLoading, error, badgeCount
  - searchQuery

- Key functions:
  - `loadReviewRequests()` - fetch with optional filter
  - `loadConversationDetail()` - fetch with caching
  - `submitProviderResponse()` - save response
  - `updateReviewStatus()` - change status
  - `flagReview()` - mark for escalation
  - `searchConversations()` - filter by query
  - `setStatusFilter()` - change status filter

- Auto-refresh: 60-second timer (mirrors iOS)
- Error handling with error StateFlow

### UI (`ui/`)

**Theme** (`theme/Theme.kt`, `theme/Type.kt`)
- Material Design 3 color scheme
- Light/dark mode support
- Typography system (display, headline, title, body, label)

**Screens** (`screens/`)
- `MainApp`: Navigation root composable
- `ConversationListScreen`: List of review requests (placeholder)
- Additional screens: ConversationDetailScreen, PatientProfileScreen (WIP)

**Components** (`components/`)
- Reusable Jetpack Compose components (WIP)
- Message list, conversation item, patient card, etc.

## API Integration

### Supabase REST API Endpoints

**Review Requests**
```
GET /rest/v1/provider_review_requests
- Filters: status=eq.pending, order=created_at.desc
- Returns: List<ProviderReviewRequestDetail>

GET /rest/v1/provider_review_requests?conversation_id=eq.{id}
- Returns: Single review request with full details

PATCH /rest/v1/provider_review_requests?id=eq.{id}
- Body: { status: string, responded_at?: timestamp, ... }
- Updates review status and timestamps
```

**Conversations**
```
GET /rest/v1/conversations?id=eq.{id}
- Returns: Full conversation with all messages

GET /rest/v1/follow_up_messages?conversation_id=eq.{id}
- Returns: Follow-up messages from patient
```

**Patients**
```
GET /rest/v1/patients?order=name.asc
- Returns: List of all patients
```

### Request/Response Format

**Request Headers**
```
Authorization: Bearer {anonKey}
Content-Type: application/json
Prefer: return=representation
```

**Response Format**
```json
{
  "id": "uuid",
  "conversationId": "uuid",
  "conversationTitle": "string",
  "childName": "string",
  "childAge": 5,
  "childDOB": "2019-01-01",
  "triageOutcome": "er_911|er_drive|urgent_visit|routine_visit|home_care",
  "status": "pending|responded|flagged|escalated",
  "conversationMessages": [
    {
      "id": "uuid",
      "content": "string",
      "isFromUser": boolean,
      "timestamp": "ISO8601",
      "triageOutcome": "string?",
      "providerName": "string?"
    }
  ],
  "providerResponse": {
    "responseType": "agree|agree_with_thoughts|disagree_with_thoughts|escalation",
    "content": "string",
    "urgencyLevel": "low|medium|high?"
  },
  "respondedAt": "ISO8601?",
  "createdAt": "ISO8601",
  "updatedAt": "ISO8601"
}
```

## State Management

### ProviderConversationStore StateFlow Properties

```kotlin
// Public exposed flows
val reviewRequests: StateFlow<List<ProviderReviewRequestDetail>>
val selectedStatus: StateFlow<String?>
val conversationDetailsCache: StateFlow<Map<String, ProviderReviewRequestDetail>>
val isLoading: StateFlow<Boolean>
val error: StateFlow<String?>
val badgeCount: StateFlow<Int>
val searchQuery: StateFlow<String>
```

### State Update Patterns

**Manual Trigger**
```kotlin
// User-initiated actions
store.loadReviewRequests()
store.submitProviderResponse(...)
store.setStatusFilter("pending")
store.searchConversations("pneumonia")
```

**Automatic Trigger**
```kotlin
// Background refresh every 60 seconds
startAutoRefresh() // Coroutine loop
```

**Cache Updates**
```kotlin
// Conversation details cached after fetch
conversationDetailsCache[conversationId] = detail
```

## Screen Hierarchy

### Navigation Structure (Planned)

```
MainApp (Root)
├── ConversationListScreen (Primary)
│   ├── AppBar with search
│   ├── Status filter tabs
│   └── Review list
│       └── Tap item → ConversationDetailScreen
│
├── ConversationDetailScreen (Detail)
│   ├── Patient card
│   ├── Conversation history
│   └── Response composer
│
└── PatientProfileScreen (Profile)
    ├── Demographics
    ├── Medications
    ├── Allergies
    └── Past medical history
```

## Comparison with iOS Architecture

| Aspect | iOS (SwiftUI) | Android (Compose) |
|--------|---------------|-------------------|
| State Management | @StateObject + @Published | ViewModel + StateFlow |
| Reactive Updates | Combine | Kotlin Coroutines |
| HTTP Client | URLSession | OkHttp |
| JSON Parsing | Codable | Gson/kotlinx.serialization |
| Push Notifications | UserNotifications | Firebase Cloud Messaging |
| Navigation | NavigationStack | NavController (Compose Nav) |
| DI Pattern | @EnvironmentObject | Constructor injection |
| Threading | Task/async-await | launch/suspendCancellableCoroutine |

Both follow MVVM with identical business logic, making sync straightforward.

## Performance Considerations

1. **Caching**: Conversation details cached to avoid redundant API calls
2. **Auto-refresh**: 60-second interval prevents excessive API usage
3. **Lazy Loading**: Conversation details loaded on-demand, not upfront
4. **Memory**: StateFlow subscriptions cleaned up on screen exit
5. **Coroutines**: All network calls on IO dispatcher, UI updates on Main

## Error Handling

1. **API Errors**: Caught and stored in error StateFlow
2. **Network Errors**: Retry logic (up to 3 times) for conversation detail
3. **JSON Parse Errors**: Logged with fallback to empty state
4. **User Feedback**: Error messages displayed in UI, dismissible

## Security Considerations

1. **API Keys**: Stored in build config (configure via secrets)
2. **HTTPS Only**: All Supabase calls over HTTPS
3. **Authentication**: Bearer token in Authorization header
4. **Data Validation**: JSON schema validation on parse
5. **Cleartext Traffic**: Disabled in data_extraction_rules.xml
