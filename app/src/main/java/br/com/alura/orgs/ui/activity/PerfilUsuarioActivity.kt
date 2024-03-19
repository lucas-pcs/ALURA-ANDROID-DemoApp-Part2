package br.com.alura.orgs.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.databinding.ActivityPerfilUsuarioBinding
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class PerfilUsuarioActivity : UsuarioBaseActivity() {

    private val perfilUsuarioBinding by lazy {
        ActivityPerfilUsuarioBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(perfilUsuarioBinding.root)

        lifecycleScope.launch {
            usuario.collect {
                it?.let {
                    with(perfilUsuarioBinding){
                        this.activityPerfilUsuarioName.text = it.nome
                        this.activityPerfilUsuarioId.text = "ID: ${it.id}"
                    }
                }
            }
        }

        perfilUsuarioBinding.activityPerfilUsuarioButtonLogout.setOnClickListener {
            desloga()
        }

        perfilUsuarioBinding.activityPerfilUsuarioButtonExitApp.setOnClickListener {
            finishAffinity();
            System.exit(0);
        }
    }
}