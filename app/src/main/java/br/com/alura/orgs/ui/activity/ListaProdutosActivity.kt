package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosActivityBinding
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Usuario
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


class ListaProdutosActivity : UsuarioBaseActivity() {

    private val listaProdutosAdapter = ListaProdutosAdapter(context = this)
    private val binding by lazy {
        ActivityListaProdutosActivityBinding.inflate(layoutInflater)
    }
    private val produtoDAO by lazy {
        val db = AppDatabase.instancia(this)
        db.produtoDao()
    }

    private val usuarioDAO by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()

        lifecycleScope.launch {
            usuario
                .filterNotNull()
                .collect { usuario ->
                    buscaProdutosUsuario(usuario.id)
                }
        }

    }

    private suspend fun buscaProdutosUsuario(usuarioId: String) {
        produtoDAO.buscaProdutosPorUsuario(usuarioId).collect { produtos ->
            listaProdutosAdapter.atualiza(produtos)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_produtos_sair_do_app -> {
                desloga()
            }

            R.id.menu_lista_produtos_perfil_do_usuario -> {
                vaiPara(PerfilUsuarioActivity::class.java)
            }

            R.id.menu_lista_produtos_todos_produtos -> {
                vaiPara(ListaTodosProdutosActivity::class.java)
            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configuraFab() {
        val fab = binding.activityListaProdutosFab
        fab.setOnClickListener {
            vaiParaFormularioProduto()
        }
    }

    private fun vaiParaFormularioProduto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        startActivity(intent)
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaProdutosRecyclerView
        recyclerView.adapter = listaProdutosAdapter
        listaProdutosAdapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
            }
            startActivity(intent)
        }
    }

}