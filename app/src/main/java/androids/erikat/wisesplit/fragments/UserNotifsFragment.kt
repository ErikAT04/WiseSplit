package androids.erikat.wisesplit.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.NotifDAO
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.adapters.NotificationsAdapter
import androids.erikat.wisesplit.databinding.FragmentNotificationsBinding
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.launch
//Fragmento de Notificaciones del Usuario
class UserNotifsFragment(var funcionActionBar:(Toolbar, String)->Unit):Fragment() {
    //Binding
    lateinit var binding:FragmentNotificationsBinding
    //Adapter
    lateinit var adapter:NotificationsAdapter
    //Lista de notificaciones dle usuario
    var listaNotificaciones = mutableListOf<Notification>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Se inflan la vista y el vbinding
        var v = inflater.inflate(R.layout.fragment_notifications, container, false)
        binding = FragmentNotificationsBinding.bind(v)
        //Se prepara el RecyclerView
        binding.notifsRView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        binding.notifsRView.addItemDecoration(DividerItemDecoration(v.context, LinearLayoutManager.VERTICAL))
        //Se prepara el adapter
        adapter = NotificationsAdapter(funcionAceptar = {
            lifecycleScope.launch {
                GroupDAO().insertarUsuario(it.userDest, it.group)
                NotifDAO().delete(it)
                cargarLista()
            }
        },
            funcionRechazar = {
                lifecycleScope.launch {
                    NotifDAO().delete(it)
                    cargarLista()
                }
            }, listaNotificaciones)
        binding.notifsRView.adapter = adapter
        //Se carga la lista
        cargarLista()

        //Se carga el actionbar
        funcionActionBar(binding.userNotifsToolbar, "Notificaciones de ${APIUtils.mainUser!!.username}")
        return v
    }

    //Función de carga de notificaciones
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarLista() {
        lifecycleScope.launch {
            //Se guarda las notificaciones del usuario
            listaNotificaciones = NotifDAO().getNotifsFromUser(APIUtils.mainUser!!).toMutableList()
            adapter.cambiarLista(listaNotificaciones) //Se actualiza el adapter
        }
    }
}