package com.example.monsterapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monsterapp.data.local.entities.BattleHistoryEntity
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.data.repository.PokemonRepository
import com.example.monsterapp.domain.battle.BattleAction
import com.example.monsterapp.domain.battle.BattleCalculator
import com.example.monsterapp.domain.battle.BattleState
import com.example.monsterapp.domain.battle.Combatant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [MEJORA] ViewModel para gestionar el sistema de combate
 * Maneja toda la lógica de batalla, turnos y resultado final
 */
class BattleViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val TAG = "BattleViewModel"

    private val _battleState = MutableLiveData<BattleState>()
    val battleState: LiveData<BattleState> = _battleState

    private val _battleMessage = MutableLiveData<String>()
    val battleMessage: LiveData<String> = _battleMessage

    private val _battleOver = MutableLiveData<Boolean>()
    val battleOver: LiveData<Boolean> = _battleOver

    private val _winner = MutableLiveData<String?>()
    val winner: LiveData<String?> = _winner

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData para niveles ganados (recompensa)
    private val _nivelesGanados = MutableLiveData<Int>()
    val nivelesGanados: LiveData<Int> = _nivelesGanados

    private var currentState: BattleState? = null

    // [MEJORA] Nombre real del oponente (se actualiza al cargar de la API)
    private val _opponentRealName = MutableLiveData<String>()
    val opponentRealName: LiveData<String> = _opponentRealName

    /**
     * [MEJORA] Carga los detalles desde la API e inicia el combate
     */
    fun cargarDetallesYCombatir(
        playerEntity: PokemonEntity,
        opponentEntity: PokemonEntity,
        playerPokemonId: Int,
        opponentPokemonId: Int
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "📡 Cargando detalles para combate: Player=$playerPokemonId, Opponent=$opponentPokemonId")

                // Cargar detalles desde la API
                val playerDetail = repository.getPokemonDetail(playerPokemonId)
                val opponentDetail = repository.getPokemonDetail(opponentPokemonId)

                Log.d(TAG, "✅ Detalles cargados: ${playerDetail.name} vs ${opponentDetail.name}")

                // [MEJORA] Actualizar el nombre real del oponente
                val nombreRealOponente = opponentDetail.name.replaceFirstChar { it.uppercase() }
                _opponentRealName.postValue(nombreRealOponente)

                // [MEJORA] Crear una nueva entidad con el nombre real del oponente
                val opponentEntityConNombre = opponentEntity.copy(nombre = nombreRealOponente)

                // Iniciar combate con los detalles cargados
                iniciarCombate(playerEntity, playerDetail, opponentEntityConNombre, opponentDetail)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar detalles del Pokémon: ${e.message}", e)
                _error.postValue("Error al cargar el combate: ${e.message}")
                _battleMessage.postValue("Error al cargar el combate")
            }
        }
    }

    /**
     * Inicia un combate entre el Pokémon del jugador y uno rival
     */
    fun iniciarCombate(
        playerPokemon: PokemonEntity,
        playerPokemonDetail: PokemonDetail,
        opponentPokemon: PokemonEntity,
        opponentPokemonDetail: PokemonDetail
    ) {
        try {
            Log.d(TAG, "⚔️ Iniciando combate: ${playerPokemon.nombre} vs ${opponentPokemon.nombre}")

            // Crear combatientes
            val player = crearCombatant(playerPokemon, playerPokemonDetail)
            val opponent = crearCombatant(opponentPokemon, opponentPokemonDetail)

            Log.d(TAG, "📊 Player HP: ${player.maxHP}, ATK: ${player.attack}, DEF: ${player.defense}")
            Log.d(TAG, "📊 Opponent HP: ${opponent.maxHP}, ATK: ${opponent.attack}, DEF: ${opponent.defense}")

            // Crear estado inicial
            currentState = BattleState(
                playerPokemon = player,
                opponentPokemon = opponent,
                turno = 1,
                estado = "IN_PROGRESS",
                acciones = emptyList(),
                mensajeActual = "¡${playerPokemon.nombre} vs ${opponentPokemon.nombre}!"
            )

            // Usar postValue para actualizar desde cualquier hilo
            _battleState.postValue(currentState!!)
            _battleOver.postValue(false)

            Log.d(TAG, "✅ Combate iniciado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al iniciar combate: ${e.message}", e)
            _error.postValue("Error al iniciar combate: ${e.message}")
        }
    }

    /**
     * El jugador ataca
     */
    fun jugadorAtaca() {
        val state = currentState ?: return

        if (state.haTerminado()) {
            Log.w(TAG, "El combate ya terminó")
            return
        }

        Log.d(TAG, "🎯 Turno ${state.turno}: Jugador ataca")

        viewModelScope.launch {
            try {
                // Ataque del jugador
                val danioJugador = calcularDanioAtaque(state.playerPokemon, state.opponentPokemon)
                val nuevoOpponent = state.opponentPokemon.copy(currentHP = (state.opponentPokemon.currentHP - danioJugador).coerceAtLeast(0))

                var nuevoState = state.copy(
                    opponentPokemon = nuevoOpponent,
                    mensajeActual = "¡${state.playerPokemon.pokemon.nombre} atacó! (-$danioJugador HP)"
                )

                _battleState.postValue(nuevoState)
                currentState = nuevoState
                delay(1000)

                // Verificar si ganó
                if (nuevoOpponent.isDefeated()) {
                    finalizarCombate("PLAYER", nuevoState)
                    return@launch
                }

                // Ataque del oponente
                val danioOponente = calcularDanioAtaque(nuevoState.opponentPokemon, nuevoState.playerPokemon)
                val nuevoJugador = nuevoState.playerPokemon.copy(currentHP = (nuevoState.playerPokemon.currentHP - danioOponente).coerceAtLeast(0))

                nuevoState = nuevoState.copy(
                    playerPokemon = nuevoJugador,
                    turno = nuevoState.turno + 1,
                    mensajeActual = "¡${nuevoState.opponentPokemon.pokemon.nombre} atacó! (-$danioOponente HP)"
                )

                _battleState.postValue(nuevoState)
                currentState = nuevoState
                delay(1000)

                // Verificar si perdió
                if (nuevoJugador.isDefeated()) {
                    finalizarCombate("OPPONENT", nuevoState)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en jugadorAtaca: ${e.message}", e)
                _error.postValue("Error en combate: ${e.message}")
            }
        }
    }

    /**
     * Calcula el daño de un ataque
     */
    private fun calcularDanioAtaque(atacante: Combatant, defensor: Combatant): Int {
        val tipoAtacante = BattleCalculator.obtenerTipoPrimario(atacante)
        val tipoDefensor = BattleCalculator.obtenerTipoPrimario(defensor)
        val efectividad = BattleCalculator.calcularEfectividad(tipoAtacante, tipoDefensor)

        val danioBase = BattleCalculator.calcularDano(
            nivelAtacante = atacante.pokemon.nivel,
            ataqueAtacante = atacante.attack,
            defensorOponente = defensor.defense,
            poderAtaque = 100
        )

        var danioFinal = (danioBase * efectividad).toInt()

        // Crítico
        if (BattleCalculator.esAtaqueCritico()) {
            danioFinal = (danioFinal * 1.5).toInt()
            Log.d(TAG, "💥 ¡CRÍTICO!")
        }

        Log.d(TAG, "Daño calculado: $danioFinal (efectividad: $efectividad)")
        return danioFinal
    }

    /**
     * Finaliza el combate y guarda el resultado
     */
    private fun finalizarCombate(ganador: String, estadoFinal: BattleState) {
        Log.d(TAG, "✅ Combate terminado. Ganador: $ganador")

        val resultado = if (ganador == "PLAYER") "WIN" else "LOSE"
        val danioTotal = estadoFinal.playerPokemon.maxHP - estadoFinal.playerPokemon.currentHP
        val danioRecibido = estadoFinal.opponentPokemon.maxHP - estadoFinal.opponentPokemon.currentHP

        // Guardar en historial
        val battleRecord = BattleHistoryEntity(
            playerPokemonId = estadoFinal.playerPokemon.pokemon.pokemonId,
            playerPokemonName = estadoFinal.playerPokemon.pokemon.nombre,
            playerPokemonLevel = estadoFinal.playerPokemon.pokemon.nivel,
            opponentPokemonId = estadoFinal.opponentPokemon.pokemon.pokemonId,
            opponentPokemonName = estadoFinal.opponentPokemon.pokemon.nombre,
            opponentPokemonLevel = estadoFinal.opponentPokemon.pokemon.nivel,
            result = resultado,
            damageDealt = danioRecibido,
            damageReceived = danioTotal,
            battleDate = System.currentTimeMillis(),
            durationSeconds = estadoFinal.turno * 15  // Aprox 15s por turno
        )

        viewModelScope.launch {
            repository.guardarHistorialCombate(battleRecord)

            // [MEJORA] Si ganó, subir nivel del Pokémon
            if (ganador == "PLAYER") {
                // Calcular niveles ganados (1-3, más si el rival era más fuerte)
                val diferenciaNivel = estadoFinal.opponentPokemon.pokemon.nivel - estadoFinal.playerPokemon.pokemon.nivel
                val nivelesBase = when {
                    diferenciaNivel >= 10 -> 3  // Rival mucho más fuerte
                    diferenciaNivel >= 5 -> 2  // Rival más fuerte
                    else -> 1                  // Rival igual o más débil
                }

                val nuevoNivel = repository.subirNivel(
                    estadoFinal.playerPokemon.pokemon.pokemonId,
                    nivelesBase
                )

                if (nuevoNivel > 0) {
                    _nivelesGanados.postValue(nivelesBase)
                    Log.d(TAG, "🎉 ¡${estadoFinal.playerPokemon.pokemon.nombre} subió $nivelesBase nivel(es)! Ahora es Lv.$nuevoNivel")
                }
            } else {
                // [MEJORA] Si perdió, marcar el Pokémon como herido
                repository.marcarComoHerido(estadoFinal.playerPokemon.pokemon.pokemonId)
                Log.d(TAG, "🤕 ${estadoFinal.playerPokemon.pokemon.nombre} quedó herido")
            }
        }

        _winner.postValue(ganador)
        _battleOver.postValue(true)

        val mensaje = if (ganador == "PLAYER") {
            "¡Ganaste! Tu Pokémon ganó experiencia."
        } else {
            "¡Perdiste! Tu Pokémon quedó herido."
        }
        _battleMessage.postValue(mensaje)
    }

    /**
     * Crea un objeto Combatant a partir de entidades Room y detalle de API
     */
    private fun crearCombatant(pokemon: PokemonEntity, detail: PokemonDetail): Combatant {
        // Extraer estadísticas del detalle
        var hp = 100
        var atk = 100
        var def = 100
        var spAtk = 100
        var spDef = 100
        var spe = 100

        detail.stats.forEach { stat ->
            when (stat.stat.name) {
                "hp" -> hp = stat.baseStat
                "attack" -> atk = stat.baseStat
                "defense" -> def = stat.baseStat
                "sp-atk" -> spAtk = stat.baseStat
                "sp-def" -> spDef = stat.baseStat
                "speed" -> spe = stat.baseStat
            }
        }

        // Ajustar por nivel
        val hpAjustado = (hp * pokemon.nivel / 50) + pokemon.nivel + 5
        val atkAjustado = (atk * pokemon.nivel / 50) + 5
        val defAjustado = (def * pokemon.nivel / 50) + 5
        val spAtkAjustado = (spAtk * pokemon.nivel / 50) + 5
        val spDefAjustado = (spDef * pokemon.nivel / 50) + 5
        val speAjustado = (spe * pokemon.nivel / 50) + 5

        return Combatant(
            pokemon = pokemon,
            pokemonDetail = detail,
            currentHP = hpAjustado,
            maxHP = hpAjustado,
            attack = atkAjustado,
            defense = defAjustado,
            spAtk = spAtkAjustado,
            spDef = spDefAjustado,
            speed = speAjustado
        )
    }
}

