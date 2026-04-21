package valdez.alejandro.room.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import valdez.alejandro.room.viewModel.AuthViewModel

@Composable
fun MainScreen(viewModel: AuthViewModel,
               onLogout: () -> Unit,
               onBolsaClick: () -> Unit,
               onCapturarClick: () -> Unit) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val username by viewModel.username.collectAsState()

    if (isLoggedIn) {
        HomeScreen(
            username = username,
            onLogout = {
                viewModel.logout() // Borra la sesión en tu DataStore
                onLogout()         // Le avisa a la navegación que cambie de pantalla
            },
            onBolsaClick = onBolsaClick,
            onCapturarClick = onCapturarClick
        )
    } else {
        LoginScreen(viewModel)
    }

}