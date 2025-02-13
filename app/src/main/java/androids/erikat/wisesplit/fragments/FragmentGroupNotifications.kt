package androids.erikat.wisesplit.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.NotifDAO
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.adapters.NotificationsAdapter
import androids.erikat.wisesplit.adapters.NotificationsGroupAdapter
import androids.erikat.wisesplit.databinding.FragmentGroupMainBinding
import androids.erikat.wisesplit.databinding.FragmentGroupNotificationsBinding
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

//Fragmento de Notificaciones dentro del grupo
class FragmentGroupNotifications(var funcionCambioToolbar:(Toolbar, String)->Unit) : Fragment() {
    //Binding
    lateinit var binding:FragmentGroupNotificationsBinding
    //Lista de notificaciones del grupo
    var listaNotificaciones:MutableList<Notification> = mutableListOf()
    //Adapter
    lateinit var adapter:NotificationsGroupAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Se infla la vista y el binding
        var view = inflater.inflate(R.layout.fragment_group_notifications, container, false)
        binding = FragmentGroupNotificationsBinding.bind(view)
        //Se hace la función del constructor
        funcionCambioToolbar(binding.notificacionesGrupoToolbar, "${APIUtils.selectedGroup!!.group_name}: Notificaciones")
        //Se carga el recycler view
        binding.notificacionesEnviadasRView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        binding.notificacionesEnviadasRView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        adapter = NotificationsGroupAdapter(listaNotificaciones){ //Función de inserción de usuario
            lifecycleScope.launch {
                NotifDAO().delete(it) //Se borra la invitación
                cargarLista() //Se recarga la lista
            }
        }
        binding.notificacionesEnviadasRView.adapter = adapter
        //Se carga la lista de notificaciones
        cargarLista()

        return view
    }
    //Función de Carga de Lista de Notificaciones
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarLista() {
        lifecycleScope.launch {
            //Coge la lista de notificaciones del grupo actual
            listaNotificaciones = NotifDAO().getNotifsFromGroup(APIUtils.selectedGroup!!).toMutableList()
            Log.d("Notificaciones", listaNotificaciones.size.toString())
            adapter.cambiarLista(listaNotificaciones) //Actualiza el adapter
        }
    }

}