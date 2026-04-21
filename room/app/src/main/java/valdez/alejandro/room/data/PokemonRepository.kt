package valdez.alejandro.room.data

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
}