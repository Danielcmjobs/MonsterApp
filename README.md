# 🎮 MonsterApp - Pokémon Explorer

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="120" alt="MonsterApp Logo"/>
</p>

<p align="center">
  <b>Una aplicación Android para explorar, capturar y combatir Pokémon en un mapa real</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/API-PokeAPI-red?style=for-the-badge"/>
</p>

---

## 📋 Descripción

**MonsterApp** es una aplicación Android desarrollada en Kotlin que permite a los usuarios explorar un mapa interactivo, capturar Pokémon que aparecen en ubicaciones cercanas, ver sus estadísticas detalladas y combatir contra Pokémon salvajes para subir de nivel.

---

## ✨ Características Principales

### 🗺️ Mapa Interactivo (OSMDroid)
- Mapa real de OpenStreetMap
- Los Pokémon aparecen alrededor de tu ubicación real
- Marcadores personalizados para cada Pokémon
- Captura con un solo toque

### 📱 Pokédex Personal
- Lista de todos los Pokémon capturados
- Búsqueda por nombre
- Estadísticas globales (nivel promedio, más fuerte, etc.)
- Gestos para liberar Pokémon

### 📊 Detalles del Pokémon
- Imagen oficial de alta calidad
- Tipos con colores característicos
- Estadísticas completas (HP, ATK, DEF, etc.)
- Barras de progreso animadas

### ⚔️ Sistema de Combate
- Combates por turnos
- Cálculo de daño basado en estadísticas reales
- Efectividad de tipos (18 tipos implementados)
- Sistema de críticos (10% de probabilidad)
- **Recompensa**: Ganar niveles al vencer rivales
- **Rival aleatorio**: Cada combate es contra un Pokémon diferente (1-150)

### 🤕 Sistema de Heridos y Curas
- **Pokémon heridos**: Si pierdes un combate, tu Pokémon queda herido
- **No pueden combatir**: Un Pokémon herido no puede luchar hasta ser curado
- **Pociones en el mapa**: Aparecen como marcadores verdes (1/3 de la cantidad de Pokémon)
- **Inventario de curas**: Se acumulan y se muestran en la Pokédex
- **Botón curar**: En los detalles del Pokémon herido, usa una poción para curarlo
- **Indicador visual**: Los Pokémon heridos muestran 🤕 en la Pokédex

### 😈 Diálogos Especiales
- **Liberar Pokémon herido**: Si intentas liberar un Pokémon herido, aparece un mensaje culpabilizante:
  - *"¿Estás seguro de que quieres liberar a [Pokémon] estando herido? Tú sí que eres un monstruo..."*
  - Botones: "Lo sé, la vida es dura." / "Tienes razón... lo siento"

### 📍 Geolocalización
- Detecta tu ubicación real mediante GPS
- Los Pokémon aparecen cerca de ti
- Fallback a Madrid si no hay permisos

---

## 🏗️ Arquitectura

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con una separación clara de capas:

```
MonsterApp/
├── data/
│   ├── local/
│   │   ├── dao/           # Data Access Objects (Room)
│   │   ├── entities/      # Entidades de la BD
│   │   └── AppDatabase.kt # Base de datos Room
│   ├── model/             # Modelos de datos (API)
│   ├── remote/            # Retrofit + API
│   └── repository/        # Repositorio único
├── domain/
│   └── battle/            # Lógica de combate
├── ui/
│   ├── fragments/         # Pantallas (Fragments)
│   ├── adapters/          # RecyclerView Adapters
│   └── viewmodel/         # ViewModels
├── utils/                 # Utilidades
└── MonsterExplorerApp.kt  # Application class
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Uso |
|------------|-----|
| **Kotlin** | Lenguaje principal |
| **MVVM** | Patrón de arquitectura |
| **Room** | Base de datos local |
| **Retrofit** | Cliente HTTP para API |
| **Moshi** | Serialización JSON |
| **OSMDroid** | Mapas OpenStreetMap |
| **Glide** | Carga de imágenes |
| **Navigation Component** | Navegación entre pantallas |
| **ViewBinding** | Enlace de vistas |
| **LiveData** | Datos observables |
| **Coroutines** | Programación asíncrona |
| **Flow** | Streams reactivos |

---

## 📦 Dependencias Principales

```kotlin
// Room (Base de datos)
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Retrofit (API)
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

// Moshi (JSON)
implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

// OSMDroid (Mapas)
implementation("org.osmdroid:osmdroid-android:6.1.18")

// Glide (Imágenes)
implementation("com.github.bumptech.glide:glide:4.16.0")

// Navigation
implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
```

---

## 📱 Pantallas de la Aplicación

### 1. 🎬 Pantalla de Carga
- Logo de la aplicación
- Animación de carga
- Navegación automática al mapa

### 2. 🗺️ Mapa
- Mapa interactivo OSMDroid
- Marcadores de Pokémon
- **Marcadores de pociones** (verdes, 1/3 de frecuencia)
- FAB para ir a la Pokédex
- FAB para centrar en ubicación
- Contador de Pokémon cercanos
- Geolocalización real (GPS)

### 3. 📋 Pokédex (Favoritos)
- Lista de Pokémon capturados
- Barra de búsqueda
- Card de estadísticas
- Contador total
- **Contador de pociones** 💊
- **Indicador de Pokémon heridos** 🤕
- Long press para liberar
- **Diálogo especial** al liberar Pokémon herido

### 4. 📊 Detalles
- Imagen HD del Pokémon
- Nombre y número
- Tipos con colores
- 6 estadísticas con barras
- **Estado del Pokémon** (herido/sano)
- Botón de combate (solo si está sano)
- **Botón de curar** (solo si está herido)
- Botón de volver

### 5. ⚔️ Combate
- Vista de ambos Pokémon
- **Rival aleatorio** (Pokémon diferente cada vez)
- Barras de HP animadas
- Mensajes de batalla
- Botón de ataque
- Botón de rendirse
- Resultado y recompensa (+niveles)
- **Sistema de heridos** (si pierdes, tu Pokémon queda herido)

---

## 🎮 Sistema de Combate

### Fórmula de Daño
```
DañoBase = ((2 * Nivel / 5 + 2) * Ataque * 100 / Defensa) / 50 + 2
DañoFinal = DañoBase × Efectividad × Crítico
```

### Efectividad de Tipos
| Tipo | Super Efectivo vs | Poco Efectivo vs |
|------|-------------------|------------------|
| 🔥 Fuego | Planta, Bicho, Acero | Agua, Roca, Dragón |
| 💧 Agua | Fuego, Tierra, Roca | Planta, Dragón |
| ⚡ Eléctrico | Agua, Volador | Planta, Eléctrico |
| 🌿 Planta | Agua, Tierra, Roca | Fuego, Veneno, Volador |
| ... | ... | ... |

### Recompensas
| Diferencia de nivel | Niveles ganados |
|---------------------|-----------------|
| Rival +10 o más | +3 niveles |
| Rival +5 a +9 | +2 niveles |
| Rival igual o menor | +1 nivel |

---

## 🗄️ Base de Datos (Room)

### Tablas

#### `pokemon_capturados`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK) | ID autoincremental |
| pokemonId | Int | ID de la PokeAPI |
| nombre | String | Nombre del Pokémon |
| nivel | Int | Nivel actual |
| fechaCaptura | Long | Timestamp de captura |
| latitud | Double | Latitud de captura |
| longitud | Double | Longitud de captura |
| **estaHerido** | **Boolean** | **Si el Pokémon está herido (perdió un combate)** |

#### `battle_history`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK) | ID autoincremental |
| playerPokemonId | Int | ID del Pokémon del jugador |
| playerPokemonName | String | Nombre del jugador |
| playerPokemonLevel | Int | Nivel del jugador |
| opponentPokemonId | Int | ID del oponente |
| opponentPokemonName | String | Nombre del oponente |
| opponentPokemonLevel | Int | Nivel del oponente |
| result | String | "WIN" o "LOSE" |
| damageDealt | Int | Daño infligido |
| damageReceived | Int | Daño recibido |
| battleDate | Long | Timestamp del combate |
| durationSeconds | Int | Duración en segundos |

#### `inventario` (NUEVO)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK) | Siempre 1 (único registro) |
| **cantidadCuras** | **Int** | **Número de pociones disponibles** |

---

## 🌐 API Utilizada

### PokeAPI (https://pokeapi.co/)

#### Endpoints utilizados:
- `GET /pokemon?limit={n}` - Lista de Pokémon
- `GET /pokemon/{id}` - Detalles de un Pokémon

#### Modelo de respuesta:
```json
{
  "id": 25,
  "name": "pikachu",
  "types": [{"type": {"name": "electric"}}],
  "stats": [
    {"base_stat": 35, "stat": {"name": "hp"}},
    {"base_stat": 55, "stat": {"name": "attack"}},
    ...
  ],
  "sprites": {
    "other": {
      "official-artwork": {
        "front_default": "https://..."
      }
    }
  }
}
```

---

## 📍 Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

| Permiso | Uso |
|---------|-----|
| INTERNET | Conexión a PokeAPI y tiles del mapa |
| ACCESS_NETWORK_STATE | Verificar conectividad |
| ACCESS_FINE_LOCATION | Ubicación GPS precisa |
| ACCESS_COARSE_LOCATION | Ubicación aproximada (red) |

---

## 🚀 Instalación

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17+
- Android SDK 24+ (minSdk)
- Android SDK 34 (targetSdk)

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/MonsterApp.git
   ```

2. Abre el proyecto en Android Studio

3. Sincroniza Gradle:
   ```bash
   ./gradlew build
   ```

4. Ejecuta en emulador o dispositivo:
   ```bash
   ./gradlew installDebug
   ```

### Generar APK
```bash
./gradlew assembleDebug
```
La APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📁 Estructura de Archivos

```
app/src/main/
├── java/com/example/monsterapp/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/
│   │   │   │   ├── PokemonDao.kt
│   │   │   │   └── BattleHistoryDao.kt
│   │   │   ├── entities/
│   │   │   │   ├── PokemonEntity.kt
│   │   │   │   └── BattleHistoryEntity.kt
│   │   │   └── AppDatabase.kt
│   │   ├── model/
│   │   │   ├── PokemonDetail.kt
│   │   │   ├── PokemonListResponse.kt
│   │   │   └── PokemonResult.kt
│   │   ├── remote/
│   │   │   ├── PokeApiService.kt
│   │   │   └── RetrofitClient.kt
│   │   └── repository/
│   │       └── PokemonRepository.kt
│   ├── domain/
│   │   └── battle/
│   │       ├── BattleModels.kt
│   │       └── BattleCalculator.kt
│   ├── ui/
│   │   ├── adapters/
│   │   │   └── FavoritesAdapter.kt
│   │   ├── fragments/
│   │   │   ├── LoadingFragment.kt
│   │   │   ├── MapFragment.kt
│   │   │   ├── DetailsFragment.kt
│   │   │   ├── FavoritesFragment.kt
│   │   │   └── BattleFragment.kt
│   │   └── viewmodel/
│   │       ├── MapViewModel.kt
│   │       ├── DetailsViewModel.kt
│   │       ├── FavoritosViewModel.kt
│   │       ├── BattleViewModel.kt
│   │       └── PokemonViewModelFactory.kt
│   ├── utils/
│   │   ├── LocationHelper.kt
│   │   ├── PokemonTypeColors.kt
│   │   ├── SearchUtils.kt
│   │   └── StatsUtils.kt
│   ├── MainActivity.kt
│   └── MonsterExplorerApp.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── fragment_loading.xml
│   │   ├── fragment_map.xml
│   │   ├── fragment_details.xml
│   │   ├── fragment_favorites.xml
│   │   ├── fragment_battle.xml
│   │   ├── item_pokemon_captured.xml
│   │   └── item_stat.xml
│   ├── navigation/
│   │   └── nav_graph.xml
│   ├── values/
│   │   ├── colors.xml
│   │   ├── strings.xml
│   │   └── themes.xml
│   └── drawable/
│       └── ... (iconos y fondos)
└── AndroidManifest.xml
```

---

## 🎯 Flujo de la Aplicación

```
┌─────────────────┐
│ LoadingFragment │ (Splash screen)
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────┐
│                  MapFragment                     │
│            (Mapa + GPS + Pokémon)               │
│                                                  │
│  🔴 Pokémon (capturar)    💊 Pociones (recoger) │
└────────┬───────────────────────┬────────────────┘
         │                       │
         ▼                       ▼
┌─────────────────┐     ┌──────────────────┐
│ Capturar Pokémon│     │ Recoger Poción   │
│ (Toast + Room)  │     │ (+1 inventario)  │
└────────┬────────┘     └──────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────┐
│              FavoritesFragment                   │
│                 (Pokédex)                        │
│                                                  │
│  📋 Lista Pokémon    💊 X pociones   🤕 Y heridos│
│  Long press = Liberar (con diálogo especial)    │
└────────┬────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────┐
│              DetailsFragment                     │
│           (Detalles del Pokémon)                │
│                                                  │
│  ┌─────────────────┐  ┌─────────────────┐       │
│  │  Pokémon SANO   │  │ Pokémon HERIDO  │       │
│  │                 │  │                 │       │
│  │ [⚔️ COMBATIR]   │  │ [💊 Curar (X)]  │       │
│  └────────┬────────┘  └────────┬────────┘       │
└───────────┼────────────────────┼────────────────┘
            │                    │
            ▼                    ▼
┌───────────────────┐   ┌───────────────────┐
│  BattleFragment   │   │   Usar Poción     │
│    (Combate)      │   │   (-1 poción)     │
│                   │   │   Pokémon SANO    │
│  🎮 vs Rival      │   └───────────────────┘
│     aleatorio     │
└─────────┬─────────┘
          │
    ┌─────┴─────┐
    ▼           ▼
┌───────┐   ┌────────┐
│ GANAR │   │ PERDER │
│       │   │        │
│+Niveles│   │🤕HERIDO│
└───────┘   └────────┘
```

### 😈 Diálogo Especial al Liberar Pokémon Herido

```
┌─────────────────────────────────────┐
│          😱 ¿En serio?              │
│                                     │
│  ¿Estás seguro de que quieres      │
│  liberar a [Pokémon] estando       │
│  herido? Tú sí que eres un         │
│  monstruo...                        │
│                                     │
│  [Tienes razón... lo siento]       │
│  [Lo sé, la vida es dura.]         │
└─────────────────────────────────────┘
```

---

## 📝 Changelog

### Versión 3.0 (Actual)
- ✅ **Sistema de Pokémon Heridos**: Los Pokémon quedan heridos al perder combates
- ✅ **Sistema de Pociones**: Recoge pociones en el mapa para curar Pokémon
- ✅ **Inventario de Curas**: Contador visible en la Pokédex
- ✅ **Botón Curar**: En detalles del Pokémon herido
- ✅ **Rival Aleatorio**: Cada combate es contra un Pokémon diferente (1-150)
- ✅ **Diálogos Especiales**: Mensaje culpabilizante al liberar Pokémon herido
- ✅ **Indicadores Visuales**: 🤕 en Pokédex para Pokémon heridos

### Versión 2.0
- ✅ Sistema de combate por turnos
- ✅ Efectividad de tipos
- ✅ Sistema de críticos
- ✅ Subir niveles al ganar
- ✅ Historial de combates
- ✅ Geolocalización real (GPS)

### Versión 1.0
- ✅ Mapa interactivo con OSMDroid
- ✅ Captura de Pokémon
- ✅ Pokédex con lista de capturados
- ✅ Detalles del Pokémon
- ✅ Integración con PokeAPI

---

## 👨‍💻 Autor

**Daniel**

---

## 📄 Licencia

Este proyecto fue desarrollado con fines educativos.

Los datos de Pokémon son proporcionados por [PokeAPI](https://pokeapi.co/) bajo su licencia.

---

## 🙏 Agradecimientos

- [PokeAPI](https://pokeapi.co/) - API de datos de Pokémon
- [OpenStreetMap](https://www.openstreetmap.org/) - Tiles del mapa
- [OSMDroid](https://github.com/osmdroid/osmdroid) - Librería de mapas
- [Glide](https://github.com/bumptech/glide) - Carga de imágenes

---

<p align="center">
  <b>¡Atrapa a todos! 🎮</b>
</p>

