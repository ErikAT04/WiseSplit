package androids.erikat.wisesplit.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.DTO.GroupDTO
import androids.erikat.wisesplit.GroupActivity
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.adapters.GroupsAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

//Fragmento Principal de la actividad del usuario
class GroupListFragment(
    var funcionActionBar: (Toolbar, String) -> Unit,
    var funcionCargaActivity: () -> Unit
) : Fragment() {
    //RecyclerView
    lateinit var recyclerView: RecyclerView

    //Lista de grupos del usuario
    var groupList: List<Group> = mutableListOf()

    //Adaptador
    lateinit var adapter: GroupsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Infla la vista y guarda el recyclerview
        val v: View = inflater.inflate(R.layout.fragment_list_group, container, false)
        recyclerView = v.findViewById(R.id.groupListRView)

        //Prepara el recyclerview
        recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                v.context,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter = GroupsAdapter(groupList) { group -> //Función de selección de grupo
            APIUtils.selectedGroup = group //Carga el grupo actual como el principal seleccionado
            funcionCargaActivity() //Carga la nueva actividad
            GroupActivity.funcionSalir = {
                this@GroupListFragment.loadList()
            }
        }
        recyclerView.adapter = adapter //Introduce el adapter
        loadList() //Carga la lista

        //Carga la función de carga del ActionBar de la actividad con el toolbar
        var toolbar: Toolbar = v.findViewById(R.id.groupListToolbar)
        funcionActionBar(toolbar, "Grupos de ${APIUtils.mainUser?.username}")

        //Genera el listener del FAB de creación de grupo
        var fab = v.findViewById<FloatingActionButton>(R.id.addGrupoFAB)
        fab.setOnClickListener {
            var builder = AlertDialog.Builder(v.context)

            var tfield = EditText(v.context)
            tfield.hint = "Escribe un nombre para el grupo"

            builder.setView(tfield)

            builder.setPositiveButton("Enviar") { dialog, _ -> //Función de Creación de Grupo
                lifecycleScope.launch {
                    if (tfield.text.isNotEmpty()) {  //Si el texto no está vacío
                        //Crea el grupo
                        var grupo = Group(
                            null,
                            tfield.text.toString(),
                            "https://static.vecteezy.com/system/resources/previews/023/547/344/non_2x/group-icon-free-vector.jpg"
                        )
                        //Inserta el grupo
                        GroupDAO().insert(grupo)
                        //Recoge el grupo insertado (no controla problemas de concurrencia)
                        grupo = GroupDAO().getList().last()
                        //Inserta el usuario en el grupo
                        GroupDAO().insertarUsuario(APIUtils.mainUser!!, grupo)
                        //Promociona al usuario a administrador
                        GroupDAO().promocionarUsuario(grupo.id!!, APIUtils.mainUser!!.email)
                        //Recarga la lista
                        loadList()
                        Toast.makeText(v.context, "Grupo creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            v.context,
                            "El campo no puede estar vacío",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            builder.create().show()

        }
        return v
    }
    //Función de carga de Lista de Grupos
    private fun loadList() {
        lifecycleScope.launch {
            //Guarda los grupos del usuario
            groupList = GroupDAO().getListFromUser(APIUtils.mainUser!!)
            adapter.cambiarLista(groupList) //Actualiza el adapter
        }
    }
}