package br.com.alura.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaTodosProdutosBinding
import br.com.alura.orgs.extensions.toHash
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.model.Usuario
import br.com.alura.orgs.ui.recyclerview.adapter.UsuariosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ListaTodosProdutosActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaTodosProdutosBinding.inflate(layoutInflater)
    }
    private val usuarioDAO by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }
    private val produtoDAO by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val usuariosAdapter = UsuariosAdapter(this@ListaTodosProdutosActivity)

        binding.activityListaTodosProdutosRvUsuarios.adapter = usuariosAdapter


        lifecycleScope.launch {
            usuarioDAO.buscaTodosUsuarios().filterNotNull().collect { listaDeUsuarios ->
                launch {
                    agrupaProdutosSemUsuario(listaDeUsuarios)
                }
                usuariosAdapter.atualiza(listaDeUsuarios)
                lifecycleScope.launch {
                    listaDeUsuarios.forEach { usuario ->
                        produtoDAO.buscaProdutosPorUsuario(usuario.id)
                    }

                }
            }
        }
    }

    private suspend fun agrupaProdutosSemUsuario(listaDeUsuarios: List<Usuario>) {
        val produtosSemUsuarioNome = "Sem Usuário"
        val produtosSemUsuarioId = "sem_usuario"
        val produtosSemUsuarioSenha = "1234".toHash()
        val produtosSemUsuario =
            Usuario(produtosSemUsuarioId, produtosSemUsuarioNome, produtosSemUsuarioSenha)

        if (!listaDeUsuarios.contains(produtosSemUsuario)) {
            usuarioDAO.salva(produtosSemUsuario)
        }

        produtoDAO.buscaTodos().collect { listaDeTodosProdutos ->
            listaDeTodosProdutos.forEach { produto ->
                Log.d("TAG", "onCreate: ${produto.usuarioId}")
                if (produto.usuarioId.isNullOrEmpty()) {
                    Log.d("TAG", "onCreate: IS NULL")
                    val novoProduto = produto
                    novoProduto.usuarioId = produtosSemUsuarioId
                    produtoDAO.salva(novoProduto)
                }
            }
        }
    }
}