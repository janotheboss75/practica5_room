package valdez.alejandro.room.data

import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val pokemonDao: PokemonDao){
    val allPokemons = pokemonDao.getAll()

    suspend fun add(pokemon: PokemonEntity){
        pokemonDao.add(pokemon)
    }

    suspend fun delete(pokemon: PokemonEntity){
        pokemonDao.delete(pokemon)
    }

    suspend fun update(pokemon: PokemonEntity){
        pokemonDao.update(pokemon)
    }

    fun getFilteredPokemons(search: String, type: String?, minLevel: Int): Flow<List<PokemonEntity>> {
        return pokemonDao.getFilteredPokemons(search, type, minLevel)
    }
}