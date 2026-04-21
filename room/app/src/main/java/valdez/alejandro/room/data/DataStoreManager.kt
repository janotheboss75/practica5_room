package valdez.alejandro.room.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Esta clase es la administradora de los datos de la sesión.
// Necesita el "Context" para que Android le dé permiso de leer y escribir archivos.
class DataStoreManager(private val context: Context) {

    // 1. CREACIÓN DEL ARCHIVO
    // Aquí inicializamos el DataStore. Es como crear el archivo físico en el celular
    // y le ponemos de nombre "session_prefs" (preferencias de sesión).
    private val Context.dataStore by preferencesDataStore(name = "session_prefs")

    // 2. DEFINICIÓN DE LAS LLAVES
    // Las llaves son como las "etiquetas" que usamos para guardar y encontrar nuestros datos.
    companion object {
        // Llave tipo Boolean (verdadero o falso) para saber si hay sesión activa.
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        // Llave tipo String (texto) para guardar el nombre del usuario.
        val USERNAME = stringPreferencesKey("username")
    }

    // 3. LECTURA DE DATOS EN TIEMPO REAL (FLOWS)
    // Este Flow es un "observador". Si el estado de la sesión cambia, avisa automáticamente.
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map {
            // Lee la libreta usando la llave IS_LOGGED_IN.
            // El "?: false" significa: "Si no hay nada guardado aún, asume que es falso (no logueado)".
            it[IS_LOGGED_IN] ?: false
        }

    // Este Flow observa y te entrega el nombre de usuario.
    val usernameFlow: Flow<String> = context.dataStore.data
        .map {
            // Lee la libreta usando la llave USERNAME.
            // El "?: """ significa: "Si no hay nada guardado, devuelve un texto en blanco".
            it[USERNAME] ?: ""
        }

    // 4. GUARDAR DATOS (INICIAR SESIÓN)
    // La palabra "suspend" significa que esto debe ejecutarse en segundo plano (Corrutina)
    // para que la pantalla del celular no se trabe mientras se guarda el archivo.
    suspend fun saveSession(username: String) {
        context.dataStore.edit {
            // .edit abre la libreta para escribir.
            // Guardamos que sí está logueado (true) y el nombre que le pasamos a la función.
            it[IS_LOGGED_IN] = true
            it[USERNAME] = username
        }
    }

    // 5. BORRAR DATOS (CERRAR SESIÓN)
    // También usa "suspend" por la misma razón: está modificando un archivo.
    suspend fun logout() {
        context.dataStore.edit {
            // .clear() limpia la libreta por completo.
            // Borra el estado de la sesión y el nombre de usuario de un solo golpe.
            it.clear()
        }
    }
}