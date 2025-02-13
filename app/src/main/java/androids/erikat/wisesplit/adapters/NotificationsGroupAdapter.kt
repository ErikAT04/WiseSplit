package androids.erikat.wisesplit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.NotificacionGrupoReciclableBinding
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
//Adapter de las notificaciones de un grupo
class NotificationsGroupAdapter(var listaNotificaciones:MutableList<Notification>, var funcionBorrado: (Notification) -> Unit): RecyclerView.Adapter<NotificationsGroupAdapter.NotificationHolder>() {
    //Holder de las notificaciones de un grupo
    class NotificationHolder(var v:View):ViewHolder(v) {
        //ViewBinding
        lateinit var binding:NotificacionGrupoReciclableBinding
        //Función de carga de datos: Carga los datos de la notificación
        fun bind(n:Notification, funcionBorrado:()->Unit){
            binding = NotificacionGrupoReciclableBinding.bind(v)
            binding.notificacionEnviadaTv.text = "Notificación enviada a ${n.userDest.username} (${n.userDest.email}) el dia ${n.date.toString().substring(0, 10)}"
            //Si se pulsa en el botón de borrar (imagen con una x), se borra la invitación.
            binding.borrarNotificacionBtt.isVisible = APIUtils.isThisUserAdmin
            binding.borrarNotificacionBtt.isEnabled = APIUtils.isThisUserAdmin
            binding.borrarNotificacionBtt.setOnClickListener {
                funcionBorrado()
            }
        }
    }
    //Función de Creación del ViewHolder: Crea una vista y devuelve un holder con esta
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.notificacion_grupo_reciclable, parent, false)
        return NotificationHolder(view)
    }
    //Función de cuenta de objetos: Devuelve el tamaño de la lista
    override fun getItemCount(): Int {
        return listaNotificaciones.size
    }
    //Función de carga de datos en viewHolder: Pasa al holder una notificación en función de la posición y una función que acciona otra escrita en los parámetros de la clase
    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.bind(listaNotificaciones[position], funcionBorrado = {
            funcionBorrado(listaNotificaciones[position])
        })
    }
    //Función de cambio de lista: Se recarga el adapter con una nueva lista
    fun cambiarLista(lista:MutableList<Notification>){
        listaNotificaciones = lista
        notifyDataSetChanged()
    }
}