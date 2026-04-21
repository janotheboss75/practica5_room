package valdez.alejandro.room.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // IMPORTANTE: Este arregla el error de las listas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import valdez.alejandro.room.data.PokemonEntity
import valdez.alejandro.room.viewModel.PokemonViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolsaScreen(pokemonViewModel: PokemonViewModel){
    // 1. Usamos la lista filtrada que creamos en el ViewModel
    val pokemons by pokemonViewModel.filteredPokemonsState.collectAsStateWithLifecycle()
    val selectedType by pokemonViewModel.filterType.collectAsStateWithLifecycle()

    // Lista de tipos (puedes ajustarla según los tipos que tengas)
    val tipos = listOf("Todos", "Electric", "Grass", "Fire", "Water", "Bug", "Normal", "Poison", "Fairy")

    // Estados para controlar el diálogo de eliminación
    var showDialog by remember { mutableStateOf(false) }
    var pokemonToDelete by remember { mutableStateOf<PokemonEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            text = "Bolsa de Pokemon",
            style = MaterialTheme.typography.headlineLarge
        )

        // 2. Fila de Filtros (Los "Chips")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            items(tipos) { tipo ->
                val isSelected = if (tipo == "Todos") selectedType == null else selectedType == tipo

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (tipo == "Todos") {
                            pokemonViewModel.setFilterType(null)
                        } else {
                            pokemonViewModel.setFilterType(tipo)
                        }
                    },
                    label = { Text(tipo) }
                )
            }
        }

        // Obtenemos el nivel mínimo actual del ViewModel
        val currentMinLevel by pokemonViewModel.minLevel.collectAsStateWithLifecycle()

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                text = "Nivel mínimo: ${currentMinLevel.toInt()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = currentMinLevel,
                onValueChange = { pokemonViewModel.setMinLevel(it) },
                valueRange = 1f..100f, // Rango de nivel 1 a 100
                steps = 100 // Para que se mueva de 1 en 1
            )
        }

        // 3. Lista de Pokémon
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(5.dp, 12.dp)
        ) {
            items(pokemons) { pokemon ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ){
                    ListItem(
                        headlineContent = { Text("${pokemon.name} (Lvl: ${pokemon.level})") },
                        supportingContent = { Text(pokemon.type) },
                        trailingContent = {
                            Row {
                                // Botón para subir nivel
                                IconButton(onClick = {
                                    pokemonViewModel.levelUpPokemon(pokemon)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Subir Nivel",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Botón para eliminar
                                IconButton(onClick = {
                                    pokemonToDelete = pokemon
                                    showDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar Pokémon",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // --- SECCIÓN DE DIÁLOGOS ---

    // Confirmación para eliminar
    if (showDialog && pokemonToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                pokemonToDelete = null
            },
            title = { Text("Eliminar Pokémon") },
            text = { Text("¿Estás seguro de que quieres liberar a ${pokemonToDelete?.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    pokemonToDelete?.let { pokemonViewModel.deletePokemon(it) }
                    showDialog = false
                    pokemonToDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    pokemonToDelete = null
                }) { Text("Cancelar") }
            }
        )
    }

    // Aviso de entrenamiento fallido
    if (pokemonViewModel.failedToTrain) {
        AlertDialog(
            onDismissRequest = { pokemonViewModel.resetTrainMessage() },
            title = { Text("¡Entrenamiento fallido!") },
            text = { Text("Tu Pokémon se rehusó a entrenar en este momento. ¡Sigue intentando!") },
            confirmButton = {
                TextButton(onClick = { pokemonViewModel.resetTrainMessage() }) {
                    Text("Ni modo")
                }
            }
        )
    }
}