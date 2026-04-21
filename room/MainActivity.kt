package valdez.alejandro.room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import valdez.alejandro.room.data.DataStoreManager
import valdez.alejandro.room.data.PokemonDatabase
import valdez.alejandro.room.data.PokemonRepository
import valdez.alejandro.room.navigation.AppNavigation
import valdez.alejandro.room.screens.MainScreen
import valdez.alejandro.room.ui.theme.RoomTheme
import valdez.alejandro.room.viewModel.AuthViewModel
import valdez.alejandro.room.viewModel.PokemonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel(DataStoreManager(this))
        val database by lazy { PokemonDatabase.getDatabase(this) }
        val repository by lazy { PokemonRepository(database.pokemonDao()) }
        val pokemonViewModel: PokemonViewModel by viewModels { PokemonViewModelFactory(repository) }

        setContent {
            RoomTheme {
                AppNavigation(authViewModel, pokemonViewModel)
            }
        }
    }
}

class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PokemonViewModel(repository) as T
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoomTheme {
        Greeting("Android")
    }
}