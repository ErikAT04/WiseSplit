package androids.erikat.wisesplit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Dialogs.InviteUserDialog
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.adapters.UsersGroupAdapter
import androids.erikat.wisesplit.databinding.FragmentGroupSettingsBinding
import androids.erikat.wisesplit.databinding.FragmentGroupUsersBinding
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

//Fragmento que guarda todos los usuarios del grupo
class FragmentGroupUsers(var funcionCambioToolbar:(Toolbar, String)->Unit) : Fragment() {
    //Binding
    lateinit var binding:FragmentGroupUsersBinding
    //Adapter
    lateinit var adapter:UsersGroupAdapter
    //Lista de usuarios del grupo
    var listaUsuarios:MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Se infla la vista y el binding
        var view = inflater.inflate(R.layout.fragment_group_users, container, false)
        binding = FragmentGroupUsersBinding.bind(view)
        //Se prepara el recyclerview de los usuarios
        binding.usuariosGrupoRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        binding.usuariosGrupoRecyclerView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))

        adapter = UsersGroupAdapter(listaUsuarios, false, funcionPromocionar = { //Función de promoción de usuarios
            lifecycleScope.launch {
                var resultado = GroupDAO().promocionarUsuario(APIUtils.selectedGroup!!.id!!, it.email) //Hace administrador a un usuario
                Toast.makeText(view?.context, resultado, Toast.LENGTH_SHORT).show()
                cargarLista() //Recarga
            }
        }, funcionEliminar = { //Función de eliminar usuario de grupo
            lifecycleScope.launch {
                var resultado = GroupDAO().expulsarUsuario(APIUtils.selectedGroup!!.id!!, it.email) //Lo elimina del grupo
                Toast.makeText(view?.context, resultado, Toast.LENGTH_SHORT).show() //Muestra el resultado
                cargarLista() //Recarga
            }
        })
        binding.usuariosGrupoRecyclerView.adapter = adapter //Introduce el adapter

        cargarLista()

        funcionCambioToolbar(binding.usuariosGrupoToolbar, "Usuarios de ${APIUtils.selectedGroup!!.group_name}")

        //El FAB solo puede ser utilizado por administradores
        binding.addUsuarioFAB.isEnabled = APIUtils.isThisUserAdmin
        binding.addUsuarioFAB.isVisible = APIUtils.isThisUserAdmin
        binding.addUsuarioFAB.setOnClickListener {
            InviteUserDialog().show(requireActivity().supportFragmentManager, "Invitar Usuario") //Inicia el Dialog de Invitación
        }
        return view
    }
    //Función de carga de lista
    private fun cargarLista() {
        lifecycleScope.launch {
            //Carga la nueva lista de usuarios
            listaUsuarios = UserDAO().getUsersFromGroup(APIUtils.selectedGroup!!).toMutableList()
            //Actualiza el adapter con la nueva lista
            adapter.cambiarLista(listaUsuarios)
        }
    }
}