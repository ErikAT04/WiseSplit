package androids.erikat.wisesplit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.databinding.UsuarioGrupoReciclableBinding
import androids.erikat.wisesplit.fragments.FragmentGroupMain
import androids.erikat.wisesplit.fragments.FragmentGroupUsers
import androids.erikat.wisesplit.fragments.GroupListFragment
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
//Adaptador de Usuarios de un grupo
class UsersGroupAdapter(
    var listaUsuarios: MutableList<User>, //Lista de usuarios
    var seleccionando: Boolean, //Este adaptador se utiliza en dos vistas reciclables con distintas funciones, por lo que esta booleana controla qué opciones cargar en qué vista.
    var funcionPromocionar: (User) -> Unit, //Función para hacer administrador a una persona
    var funcionEliminar: (User) -> Unit //Función para eliminar a una persona
) : Adapter<UsersGroupAdapter.UsersGroupHolder>() {
    //Holder de usuarios
    class UsersGroupHolder(var v: View) : ViewHolder(v) {
        //ViewBinding
        lateinit var binding: UsuarioGrupoReciclableBinding
        //Función de vinculación de datos
        fun bind(
            user: User, //Usuario a cargar
            seleccionando: Boolean, //Booleana que controla si la vista reciclable es una u otra
            funcionPromocionar: () -> Unit, //Función para hacer un usuario administrador
            funcionEliminar: () -> Unit, //Función para borrar un usuario del grupo
            funcionCheck: (CheckBox)->Unit //Función para seleccionar un usuario
        ) {
            //Se carga el binding y los datos del usuario (nombre de usuario e imagen)
            binding = UsuarioGrupoReciclableBinding.bind(v)
            binding.nombreUsuarioTxt.text = user.username
            binding.usuarioDeGrupoIMGView.cargarImagen(user.image)
            //Si la vista reciclable es la de selección de usuarios:
            if (seleccionando) {
                //Se selecciona el check si el usuario aparece en la lista de usuarios seleccionados
                binding.checkUsuarioSeleccionado.isChecked =
                    (user in FragmentGroupMain.listaSeleccionados)
                //Se pone la función de selección de usuarios
                binding.checkUsuarioSeleccionado.setOnClickListener {
                    funcionCheck(binding.checkUsuarioSeleccionado)
                }
            //Si la vista reciclable es la de vista de usuarios:
            } else {
                //Se quitan los CheckBox
                binding.checkUsuarioSeleccionado.isVisible = false
                binding.checkUsuarioSeleccionado.isEnabled = false
                //Si el usuario actual es administrador del grupo y no es él mismo:
                if (APIUtils.isThisUserAdmin && APIUtils.mainUser!!.email != user.email) {
                    //Se carga una función si la vista es pulsada de forma prolongada
                    v.setOnLongClickListener {
                        //Se carga un popup menu
                        var popUpMenu: PopupMenu = PopupMenu(v.context, v)
                        //Se infla el menú del popup
                        popUpMenu.menuInflater.inflate(R.menu.menu_popup_usuario, popUpMenu.menu)
                        //Se pone un listener de los items seleccionados
                        popUpMenu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                //Si se ha pulsado a hacer admin, se promociona al usuario a administrador
                                R.id.hacer_admin -> {
                                    funcionPromocionar()
                                }
                                //Si se ha pulsado a expulsar, se elimina el usuario
                                R.id.expulsar_usuario -> {
                                    funcionEliminar()
                                }
                            }
                            true
                        }
                        //Se muestra el popup
                        popUpMenu.show()
                        true
                    }
                }
            }

        }

    }
    //Función de creación de ViewHolder: Se infla una vista y se devuelve un holder con ella.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersGroupHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usuario_grupo_reciclable, parent, false)

        return UsersGroupHolder(view)
    }
    //Función de cuenta de objetos: Devuelve el tamaño de la lista
    override fun getItemCount(): Int {
        return listaUsuarios.size
    }
    //Función de carga de datos en el holder: Envía el usuario actual, la booleana que controla el recycler actual y las funciones de promoción, eliminación y check
    override fun onBindViewHolder(holder: UsersGroupHolder, position: Int) {
        holder.bind(listaUsuarios[position], seleccionando,
            { //Función de promoción: Se realiza la función de promoción pasada en el constructor
                funcionPromocionar(listaUsuarios[position])
            },
            { //Función de eliminación: Se realiza la función de eliminación pasada en el constructor
                funcionEliminar(listaUsuarios[position])
            },
            {checkBox -> //Función de Check de objetos
                //HAY QUE TENER EN CUENTA QUE, ANTES DE QUE SE REALICE ESTA FUNCIÓN, SE ALTERNA AUTOMÁTICAMENTE EL CHECKBOX
                //Por lo que hay que trabajar con él al revés
                if(!checkBox.isChecked){ //Si el check está desactivado (estaba activado antes de darle) borra el usuario de la lista de selección
                    FragmentGroupMain.listaSeleccionados.remove(listaUsuarios[position])
                }else{ //Si el check está activado (estaba desactivado antes de darle) añade el usuario a la lista de selección
                    FragmentGroupMain.listaSeleccionados.add(listaUsuarios[position])
                }
                notifyItemChanged(position) //Se notifica el cambio del check en la posición pulsada
            })
    }
    //Función de Carga de Lista: Recarga el adaptador con la nueva lista
    fun cambiarLista(lista:MutableList<User>){
        listaUsuarios = lista
        notifyDataSetChanged()
    }
}