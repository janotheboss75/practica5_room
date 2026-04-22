package valdez.alejandro.room.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import valdez.alejandro.room.data.PokemonEntity
import valdez.alejandro.room.data.PokemonRepository

class PokemonViewModel (private val repository: PokemonRepository) : ViewModel(){
    private val availablePokemons = listOf(
        PokemonEntity(name = "Pikachu", number = "025", type = "Electric"),
        PokemonEntity(name = "Bulbasaur", number = "001", type = "Grass"),
        PokemonEntity(name = "Charmander", number = "004", type = "Fire"),
        PokemonEntity(name = "Squirtle", number = "007", type = "Water"),
        PokemonEntity(name = "Caterpie", number = "010", type = "Bug"),
        PokemonEntity(name = "Weedle", number = "013", type = "Bug"),
        PokemonEntity(name = "Pidgey", number = "016", type = "Normal"),
        PokemonEntity(name = "Rattata", number = "019", type = "Normal"),
        PokemonEntity(name = "Spearow", number = "021", type = "Normal"),
        PokemonEntity(name = "Ekans", number = "023", type = "Poison"),
        PokemonEntity(name = "Arbok", number = "024", type = "Poison"),
        PokemonEntity(name = "Clefairy", number = "035", type = "Fairy")
    )

    var wildPokemon by mutableStateOf<PokemonEntity?>(null)
        private set

    var capturedPokemons by mutableStateOf(listOf<PokemonEntity>())
        private set

    var pokemonSeEscapo by mutableStateOf(false)
        private set
    // ... (availablePokemons y funciones de captura se mantienen igual) ...

    fun searchPokemon(){
        wildPokemon = availablePokemons.random()
    }

    fun releaseCapturedPokemons(){
        capturedPokemons = emptyList()
    }

    fun capturePokemon() {
        wildPokemon?.let {
            val success = (1..100).random()
            if (success > 50) {
                capturedPokemons = capturedPokemons + it
                pokemonSeEscapo = false
                wildPokemon = null
            } else {
                pokemonSeEscapo = true
                wildPokemon = null
            }
        }
    }

    fun addPokemon(name: String, number: String, type: String, level: Int = 1){
        viewModelScope.launch {
            repository.add(
                PokemonEntity(
                    name = name,
                    number = number,
                    type = type,
                    level = level
                )
            )
        }
    }

    // --- ESTADOS PARA FILTROS ---
    val searchText = MutableStateFlow("")
    val filterType = MutableStateFlow<String?>(null)
    val minLevel = MutableStateFlow(1f)

    // --- LÓGICA DE BÚSQUEDA EN TIEMPO REAL (Puntos 3, 4 y 5) ---
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredPokemonsState: StateFlow<List<PokemonEntity>> = combine(
        searchText,
        filterType,
        minLevel
    ) { search, type, level ->
        Triple(search, type, level.toInt())
    }.flatMapLatest { (search, type, level) ->
        // Le pide los datos a la BD usando la Query con LIKE y WHERE
        repository.getFilteredPokemons(search, type, level)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- FUNCIONES DE ACTUALIZACIÓN ---
    fun onSearchTextChange(text: String) { searchText.value = text }
    fun setFilterType(type: String?) { filterType.value = type }
    fun setMinLevel(level: Float) { minLevel.value = level }

    // ... (Funciones add, delete y levelUp se mantienen igual) ...

    // Agrega esta para que no falte:
    var failedToTrain by mutableStateOf(false)
        private set
    fun resetTrainMessage() { failedToTrain = false }

    fun deletePokemon(pokemon: PokemonEntity) {
        viewModelScope.launch { repository.delete(pokemon) }
    }

    fun levelUpPokemon(pokemon: PokemonEntity) {
        failedToTrain = false
        if (pokemon.level < 100) {
            if ((1..100).random() > 40) {
                viewModelScope.launch { repository.update(pokemon.copy(level = pokemon.level + 1)) }
            } else { failedToTrain = true }
        }
    }
}