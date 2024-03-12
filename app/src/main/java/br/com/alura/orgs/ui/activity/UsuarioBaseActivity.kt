package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.database.preferences.dataStore
import br.com.alura.orgs.database.preferences.usuarioLogadoPreference
import br.com.alura.orgs.extensions.toast
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

abstract class UsuarioBaseActivity : AppCompatActivity() {

    private val usuarioDAO by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }
    
    private var _usuario: MutableStateFlow<Usuario?> = MutableStateFlow(null)
    protected var usuario: StateFlow<Usuario?> = _usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            verificaUsuarioLogado()
        }
    }

    protected suspend fun verificaUsuarioLogado() {
            dataStore.data.collect { preferences ->
                preferences[usuarioLogadoPreference]?.let { usuarioID ->
                    buscaUsuario(usuarioID)
                } ?: vaiParaLogin()
            }
    }

    private suspend fun buscaUsuario(usuarioID: String) {
        _usuario.value = usuarioDAO.buscaPorId(usuarioID).firstOrNull()
    }

    private fun vaiParaLogin() {
        vaiPara(LoginActivity::class.java) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        finish()
    }

    protected fun desloga() {
        lifecycleScope.launch {
            dataStore.edit { preferences ->
                preferences.remove(usuarioLogadoPreference)
            }
            vaiParaLogin()
        }
    }

}