package androids.erikat.wisesplit.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.databinding.CardGroupBinding
import androidx.recyclerview.widget.RecyclerView

//Adaptador de los grupos
class GroupsAdapter(private var datos:List<Group>, private var funcion:(Group)->Unit):RecyclerView.Adapter<GroupsAdapter.GroupHolder>(){
    //Holder de los grupos
    class GroupHolder(private var v:View):RecyclerView.ViewHolder(v){
        //Binding
        lateinit var binding:CardGroupBinding
        //Función de carga de datos: Carga el binding, la imagen y añade el nombre del grupo al texto
        fun render(dato:Group, funcion:(Group)->Unit){
            binding = CardGroupBinding.bind(v)
            binding.groupImage.cargarImagen(dato.image)
            binding.groupName.text = dato.group_name
            //Cuando se pulse sobre la carta se realiza la función pasada por parámetro
            v.setOnClickListener{
                funcion(dato)
            }
        }
    }
    //Función de creación del viewholder: Carga la vista y devuelve un ViewHolder con esa vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_group, parent, false)
        return GroupHolder(view)
    }
    //Función de la cuenta de objetos: Devuelve el tamaño de la lista
    override fun getItemCount(): Int {
        return datos.size
    }
    //Función de carga de datos en el holder: Llama a la función de éste
    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.render(datos[position], funcion)
    }
    //Función de cambio de lista: Se pasa una nueva lista y se recarga el adapter
    fun cambiarLista(lista:List<Group>){
        datos = lista
        notifyDataSetChanged()
    }
}