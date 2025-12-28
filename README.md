# Metal Force

A Metal Slug-style run and gun action game built with libGDX and Kotlin for Android.

## Features

### Gameplay
- **3 Stages** with increasing difficulty
- **Boss Fights** at the end of each stage
- **Touch Controls** optimized for mobile
- **Pixel Art Graphics** (procedurally generated)

### Weapons System
| Weapon | Description |
|--------|-------------|
| Pistol | Default weapon, unlimited ammo |
| Heavy MG | Fast fire rate, 100 rounds |
| Shotgun | 5-pellet spread, 30 shells |
| Rocket | Explosive, splash damage, 10 rockets |
| Flamethrower | Continuous fire, 150 fuel |

### Power-ups & Pickups
- Health Packs - Restore 50 HP
- Weapon Crates - Random weapon pickup
- Ammo Boxes - Restore ammo
- Bombs - Screen-clearing attack

### Enemy Types
| Enemy | HP | Behavior |
|-------|-----|----------|
| Soldier | 30 | Basic rifle shooter |
| Heavy | 80 | Slow, armored, rapid fire |
| Grenadier | 40 | Throws arc grenades |
| Shield | 60 | Block frontal attacks |
| Runner | 20 | Fast melee attacker |
| Boss | 500 | Multi-phase tank |

### Game Features
- Main Menu with How to Play guide
- Pause functionality
- Stage intro screens
- Score system with enemy values
- Health bar & ammo display
- Invincibility frames on damage
- Enemy drop system (15% chance)

## Controls

| Button | Action |
|--------|--------|
| < > | Move Left/Right |
| JUMP | Jump |
| FIRE | Shoot weapon |
| BOMB | Clear screen attack |
| \|\| | Pause game |

## Building

### Prerequisites
- JDK 17+
- Android SDK

### Build APK
```bash
./gradlew android:assembleDebug
```

Output: `android/build/outputs/apk/debug/`

### GitHub Actions
Push to `main` branch triggers automatic build.

## Project Structure

```
metal-slug-game/
├── core/                    # Game logic (shared)
│   └── src/main/kotlin/
│       └── com/yudhalabs/metalforce/
│           ├── MetalForceGame.kt
│           ├── entities/
│           │   ├── Player.kt
│           │   ├── Enemy.kt
│           │   ├── Boss.kt
│           │   ├── Bullet.kt
│           │   ├── Weapon.kt
│           │   └── Pickup.kt
│           ├── screens/
│           │   ├── MainMenuScreen.kt
│           │   └── GameScreen.kt
│           └── utils/
│               └── Assets.kt
├── android/                 # Android launcher
└── .github/workflows/       # CI/CD
```

## Tech Stack
- **libGDX** - Cross-platform game framework
- **Kotlin** - Programming language
- **Gradle** - Build system

## Credits
- Built by YudhaLabs
- Inspired by Metal Slug series

## License

MIT License
