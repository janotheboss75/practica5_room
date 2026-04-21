package valdez.alejandro.room.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valdez.alejandro.room.data.PokemonEntity
import valdez.alejandro.room.data.PokemonRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api

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

    val pokemonsState: StateFlow<List<PokemonEntity>> = repository.allPokemons
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun deletePokemon(pokemon: PokemonEntity) {
        viewModelScope.launch {
            repository.delete(pokemon)
        }
    }

    // Variable para saber si el entrenamiento falló
    var failedToTrain by mutableStateOf(false)
        private set

    fun resetTrainMessage() {
        failedToTrain = false
    }

    fun levelUpPokemon(pokemon: PokemonEntity) {
        // Reseteamos el estado al iniciar un nuevo intento
        failedToTrain = false

        if (pokemon.level < 100) {
            val success = (1..100).random()

            if (success > 40) { // 60% de éxito
                val leveledUpPokemon = pokemon.copy(level = pokemon.level + 1)
                viewModelScope.launch {
                    repository.update(leveledUpPokemon)
                }
            } else {
                // Seteamos a true si la probabilidad falla
                failedToTrain = true
            }
        }
    }

    // Estado para guardar el tipo seleccionado (null significa "Todos")
    val filterType = MutableStateFlow<String?>(null)

    // Le pusimos "filteredPokemonsState" para que no marque error de duplicado
    val filteredPokemonsState: StateFlow<List<PokemonEntity>> = combine(
        repository.allPokemons,
        filterType
    ) { pokemons, type ->
        if (type == null) {
            pokemons // Si es null, mostramos todos
        } else {
            pokemons.filter { it.type == type } // Filtramos por tipo
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Función para que la interfaz cambie el filtro
    fun setFilterType(type: String?) {
        filterType.value = type
    }
}