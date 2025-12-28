# Metal Force

A Metal Slug-inspired side-scrolling shooter game for Android, built with LibGDX and Kotlin.

## Features

- Side-scrolling run and gun gameplay
- Pixel art graphics (procedurally generated)
- Touch controls optimized for mobile
- Enemy AI with shooting mechanics
- Score system
- Infinite enemy spawning

## Controls

- **Left/Right buttons**: Move player
- **JUMP button**: Jump
- **FIRE button**: Shoot

## Building

### Prerequisites
- JDK 17
- Android SDK

### Build APK locally
```bash
./gradlew android:assembleDebug
```

### GitHub Actions
Push to `main` branch to trigger automatic build and release.

## Project Structure

```
├── core/                    # Game logic (shared)
│   └── src/main/kotlin/
│       └── com/yudhalabs/metalforce/
│           ├── MetalForceGame.kt
│           ├── entities/
│           │   ├── Player.kt
│           │   ├── Enemy.kt
│           │   └── Bullet.kt
│           ├── screens/
│           │   └── GameScreen.kt
│           └── utils/
│               └── Assets.kt
├── android/                 # Android launcher
└── .github/workflows/       # CI/CD
```

## License

MIT License
