package androids.erikat.wisesplit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.databinding.NotificacionReciclableBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
//Adaptador de Notificaciones
class NotificationsAdapter(var funcionAceptar:(Notification)->Unit, var funcionRechazar:(Notification)->Unit, var listaNotifs:List<Notification>): RecyclerView.Adapter<NotificationsAdapter.NotifsHolder>() {
    //Función de Creación de ViewHolder: Carga una vista y devuelve un holder con esa vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifsHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.notificacion_reciclable, parent, false)

        return NotifsHolder(view)
    }
    //Función de cuenta de objetos, devuelve el tamaño de la lista
    override fun getItemCount(): Int {
        return listaNotifs.size
    }
    //Función de carga de datos en Holder: Carga los datos del holder con su función
    override fun onBindViewHolder(holder: NotifsHolder, position: Int) {
        holder.bind(funcionAceptar, funcionRechazar, listaNotifs[position])
    }
    //Holder de las notificaciones
    class NotifsHolder(var v:View):ViewHolder(v){
        //ViewBinding
        lateinit var binding:NotificacionReciclableBinding
        //Función de carga de datos: Recibe dos funciones (una para aceptar la invitación y otra para rechazarla) y la notificación a cargar
        fun bind(funcionAceptar: (Notification) -> Unit, funcionRechazar: (Notification) -> Unit, notif: Notification){
            binding = NotificacionReciclableBinding.bind(v)
            binding.mensajeInvitacionTview.text = "Te han invitado al grupo '${notif.group.group_name}'"
            binding.imagenGrupoInvitarImageView.cargarImagen(notif.group.image)
            binding.aceptarInvitacionImageView.setOnClickListener {
                funcionAceptar(notif)
            }
            binding.cancelarInvitacionImageView.setOnClickListener {
                funcionRechazar(notif)
            }
        }
    }
    //Función de cambio de lista: Actualiza el adapter con la nueva lista de datos
    fun cambiarLista(lista:List<Notification>){
        listaNotifs = lista
        notifyDataSetChanged()
    }
}