package br.com.alura.orgs.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityPerfilUsuarioBinding
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Usuario
import br.com.alura.orgs.ui.dialog.FormularioImagemDialog
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class PerfilUsuarioActivity : UsuarioBaseActivity() {

    private val usuarioDAO by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    private val perfilUsuarioBinding by lazy {
        ActivityPerfilUsuarioBinding.inflate(layoutInflater)
    }
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(perfilUsuarioBinding.root)

        perfilUsuarioBinding.activityPerfilUsuarioButtonLogout.setOnClickListener {
            desloga()
        }

        perfilUsuarioBinding.activityPerfilUsuarioButtonExitApp.setOnClickListener {
            finishAffinity();
            System.exit(0);
        }

        lifecycleScope.launch {
            usuario.collect { usuario ->
                usuario?.let { usuario ->
                    with(perfilUsuarioBinding) {
                        this.activityPerfilUsuarioName.text = usuario.nome
                        this.activityPerfilUsuarioId.text = "ID: ${usuario.id}"
                        usuario.icone?.let { this.activityPerfilUsuarioIcone.load(usuario.icone) }
                            ?: this.activityPerfilUsuarioIcone.load(R.drawable.ic_action_user_profile)
                        url = usuario.icone
                    }
                    configureIconClickListener(usuario)
                }
            }
        }

    }

    private suspend fun configureIconClickListener(usuario: Usuario) {
        perfilUsuarioBinding.activityPerfilUsuarioIcone.setOnClickListener {
            FormularioImagemDialog(this@PerfilUsuarioActivity).mostra(url) { imagem ->
                url = imagem
                perfilUsuarioBinding.activityPerfilUsuarioIcone.tentaCarregarImagem(url).apply {
                    url?.let {
                        val usuarioEdit = Usuario(usuario.id, usuario.nome, usuario.senha, url)
                        lifecycleScope.launch {
                            usuarioDAO.salva(usuarioEdit)
                        }
                    }
                }
            }
        }
    }
}