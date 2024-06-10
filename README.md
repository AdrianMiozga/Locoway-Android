# PKP

> [!NOTE]
> Application made for educational purposes only.

## Technology Stack

- [Kotlin](https://kotlinlang.org/)
  - [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
  - [Flows](https://kotlinlang.org/docs/flow.html)
  - [`java.time`](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) - API for dates and times
- [Firebase](https://firebase.google.com/)
  - [Authentication](https://firebase.google.com/products/auth) - multi-platform sign-in
  - [Cloud Firestore](https://firebase.google.com/products/firestore) - NoSQL database
- [Android Jetpack](https://developer.android.com/jetpack)
  - [Compose](https://developer.android.com/jetpack/compose) - modern UI toolkit
  - [Navigation](https://developer.android.com/guide/navigation) - navigation in application
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - state holder
- [Material Design 3](https://m3.material.io/)
- [Gradle](https://gradle.org/)
  - [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
  - [Version Catalog](https://docs.gradle.org/current/userguide/platforms.html)
  - [API Desugaring](https://developer.android.com/studio/write/java8-support-table) - Java 8+ API for older app API levels

### Architecture

- Single-activity application
- Single-module application
- Unidirectional Data Flow

### Libraries

- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - dependency injection
- [Retrofit](https://github.com/square/retrofit) - HTTP client

### Testing

- [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview) - testing performance

### Static Analysis Tools

- [Android Linter](https://developer.android.com/studio/write/lint)
- [ktfmt](https://facebook.github.io/ktfmt/) - formatting Kotlin code
