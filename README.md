# MonsterApp 🎮

MonsterApp is an Android application built with Kotlin for exploring Pokémon using real-time data from the [PokeAPI](https://pokeapi.co/). Save your favorite Pokémon locally with Room, visualize their details and stats, explore the map as a Trainer, and experience a modern Android architecture.

## Features

- **Pokédex** – Browse all Pokémon with infinite scrolling via PokeAPI
- **Pokémon Details** – View artwork, types, stats (HP, Attack, Defense, Sp. Atk, Sp. Def, Speed), height, weight, abilities
- **Favorites** – Save and manage favorite Pokémon locally using Room database (add with ♥ FAB, remove with long press)
- **Trainer Map** – Interactive map (OpenStreetMap via OSMDroid) showing your current location as a trainer

## Architecture & Technologies

| Layer | Technology |
|-------|-----------|
| Architecture | MVVM (Model-View-ViewModel) |
| Networking | Retrofit 2 + Moshi (JSON parsing) |
| Local Storage | Room Database |
| Navigation | Android Navigation Component (with Safe Args) |
| Image Loading | Coil |
| Async | Kotlin Coroutines + LiveData |
| Maps | OSMDroid (OpenStreetMap — no API key required) |
| UI | Material Design 3, ViewBinding |

## Project Structure

```
app/src/main/java/com/example/monsterapp/
├── data/
│   ├── api/          # Retrofit service & NetworkModule
│   ├── db/           # Room database, DAO, entities
│   ├── model/        # API response models (Moshi)
│   └── repository/   # PokemonRepository (single source of truth)
├── ui/
│   ├── list/         # Pokédex list (Fragment + ViewModel + Adapter)
│   ├── detail/       # Pokémon detail (Fragment + ViewModel)
│   ├── favorites/    # Favorites (Fragment + ViewModel + Adapter)
│   └── map/          # Trainer map (Fragment)
├── MainActivity.kt   # Bottom Navigation + NavController
└── MonsterApplication.kt
```

## Getting Started

1. Clone the repository
2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle and run on a device/emulator with Android 7.0+ (API 24+)

No API keys required — PokeAPI is free and OSMDroid uses OpenStreetMap tiles.

## Screenshots

| Pokédex | Details | Favorites | Map |
|---------|---------|-----------|-----|
| Grid of Pokémon cards | Stats, types, abilities | Saved Pokémon | Trainer location |
