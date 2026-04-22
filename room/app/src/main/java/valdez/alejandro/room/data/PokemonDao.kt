package valdez.alejandro.room.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon_table ORDER BY number ASC")
    fun getAll(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(pokemon : PokemonEntity)

    @Delete
    suspend fun delete(pokemon: PokemonEntity)

    @Update
    suspend fun update(pokemon: PokemonEntity)

    // Consulta "Maestra": Filtra por nombre (LIKE), tipo y nivel mínimo
    @Query("""
        SELECT * FROM pokemon_table 
        WHERE (name LIKE '%' || :search || '%' OR type LIKE '%' || :search || '%')
        AND (:type IS NULL OR type = :type)
        AND level >= :minLevel
        ORDER BY number ASC
    """)
    fun getFilteredPokemons(search: String, type: String?, minLevel: Int): Flow<List<PokemonEntity>>
}