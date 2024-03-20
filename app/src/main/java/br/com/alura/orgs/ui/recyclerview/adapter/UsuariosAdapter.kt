package br.com.alura.orgs.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.UsuarioItemBinding
import br.com.alura.orgs.model.Usuario
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class UsuariosAdapter(
    private val context: Context,
    usuarios: List<Usuario?> = emptyList<Usuario?>()
)

    : RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

    private val binding by lazy {

    }

    private val produtoDAO by lazy {
        AppDatabase.instancia(context).produtoDao()
    }

    private var usuarios = usuarios.toMutableList()

    inner class ViewHolder(private val binding: UsuarioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun vincula(usuario: Usuario) {
            binding.usuarioItemNome.text = usuario.nome
            binding.usuarioItemId.text = usuario.id
            binding.usuarioItemIcone.load(usuario.icone)

            CoroutineScope(Dispatchers.Main).launch {
                produtoDAO.buscaProdutosPorUsuario(usuario.id).collect{listaDeProdutosDoUsuario ->
                    listaDeProdutosDoUsuario?.let {
                        binding.usuarioItemRvProdutos.adapter = ListaProdutosAdapter(context,listaDeProdutosDoUsuario)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = UsuarioItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuariosAdapter.ViewHolder, position: Int) {
        usuarios[position]?.let {
            holder.vincula(it)
        }

    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    fun atualiza(usuarios: List<Usuario>) {
        this.usuarios.clear()
        this.usuarios.addAll(usuarios)
        notifyDataSetChanged()
    }


}