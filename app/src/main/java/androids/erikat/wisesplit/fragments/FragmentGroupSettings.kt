package androids.erikat.wisesplit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.UserDAO

import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.databinding.FragmentGroupMainBinding
import androids.erikat.wisesplit.databinding.FragmentGroupSettingsBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

//Fragmento de Ajustes del grupo. Aqui solo tienen acceso los administradores
class FragmentGroupSettings(var funcionCambioToolbar: (Toolbar, String) -> Unit, var funcionSalirGrupo:()->Unit) : Fragment() {
    //Binding
    lateinit var binding: FragmentGroupSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Se infla la vista y el binding
        var view = inflater.inflate(R.layout.fragment_group_settings, container, false)
        binding = FragmentGroupSettingsBinding.bind(view)
        //Se cargan los datos del grupo
        binding.NombreGrupoTView.text = APIUtils.selectedGroup!!.group_name
        binding.grupoImageView.cargarImagen(APIUtils.selectedGroup!!.image)
        //Se cambia el toolbar
        funcionCambioToolbar(binding.groupSettingsToolbar, "Ajustes")
        //Se da una función al clicar a la imagen del grupo: Permite cambiar su imagen

        if (APIUtils.isThisUserAdmin) { //Si el usuario es administrador, tiene más opciones que el usuario corriente
            //Si pulsa la imagen del grupo:
            binding.grupoImageView.setOnClickListener {
                var builder = AlertDialog.Builder(view.context)

                var tfield = EditText(view.context)
                tfield.hint = "Escribe una URL válida"
                tfield.setText(APIUtils.selectedGroup!!.image)

                builder.setView(tfield)

                builder.setPositiveButton("Enviar") { dialog, _ -> //Función de cambio de imagen
                    lifecycleScope.launch {
                        if (tfield.text.isNotEmpty()) { //Si el campo no está vacío, actualiza la imagen del grupo
                            APIUtils.selectedGroup!!.image = tfield.text.toString()
                            GroupDAO().update(APIUtils.selectedGroup!!) //Actualiza los cambios
                            binding.grupoImageView.cargarImagen(APIUtils.selectedGroup!!.image)
                        }
                    }
                }

                builder.create().show()
            }
            //Si pulsa en el apartado del cambio de nombre, podrá cambiar el nombre del grupo
            binding.changeGroupNameLayout.setOnClickListener {
                var builder = AlertDialog.Builder(view.context)

                var tfield = EditText(view.context)
                tfield.hint = "Escribe un nombre de grupo"
                tfield.setText(APIUtils.selectedGroup!!.group_name)

                builder.setView(tfield)

                builder.setPositiveButton("Enviar") { dialog, _ -> //Función de cambio de nombre
                    lifecycleScope.launch {
                        if (tfield.text.isNotEmpty()) { //Si el texto no está vacío, cambia el nombre al del texto puesto
                            APIUtils.selectedGroup!!.group_name = tfield.text.toString()
                            GroupDAO().update(APIUtils.selectedGroup!!) //Actualiza en la BD
                            binding.NombreGrupoTView.text = tfield.text.toString()
                        }
                    }
                }
                builder.create().show()
            }
            //Si da al botón de borrar grupo, aparecerña un dialog para eliminarlo
            binding.eraseCLayout.setOnClickListener {
                AlertDialog.Builder(view.context)
                    .setMessage("¿Estás seguro de que quieres eliminar el grupo? No podrás volver atrás")
                    .setPositiveButton("Si") { _, _ -> //Función de eliminación
                        lifecycleScope.launch {
                            //Borra el grupo
                            GroupDAO().delete(APIUtils.selectedGroup!!)
                            //Muestra un aviso
                            Toast.makeText(
                                view.context,
                                "Grupo eliminado: ${APIUtils.selectedGroup!!.group_name}",
                                Toast.LENGTH_SHORT
                            ).show()
                            //Llama a una función para salir de la pantalla del grupo
                            funcionSalirGrupo()
                        }
                    }.setNegativeButton("No") { _, _ -> }.show()
            }
        } else {
            //Si no es administrador, no tendrá acceso ni al cambio de nombre ni a su eliminación
            binding.eraseCLayout.isEnabled = false
            binding.eraseCLayout.isVisible = false
            binding.changeGroupNameLayout.isEnabled = false
            binding.changeGroupNameLayout.isVisible = false
        }
        //Si cualquier usuario pulsa a "Salirse del grupo", se ejecutará el siguiente código
        binding.getOutCLayout.setOnClickListener {
            lifecycleScope.launch {
                //Se comprueba que el usuario actual no sea el único administrador del grupo (si es que lo es)
                if (APIUtils.isThisUserAdmin && UserDAO().getListOfAdmins(APIUtils.selectedGroup!!).size == 1) {
                    //Si es el único administrador, no permite abandonar el grupo
                    Toast.makeText(
                        view.context,
                        "No puedes abandonar el grupo al ser el único administrador",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    //Muestra un mensaje en un dialog para avisar de que se quiere salir del grupo
                    AlertDialog.Builder(view.context)
                        .setMessage("¿Estás seguro de que te quieres salir del grupo? No podrás volver atrás")
                        .setPositiveButton("Si") { _, _ -> //Función de Cierre de Sesión
                            lifecycleScope.launch {
                                //Se expulsa al usuario
                                GroupDAO().expulsarUsuario(
                                    APIUtils.selectedGroup!!.id!!,
                                    APIUtils.mainUser!!.email
                                )
                                //Se muestra un mensaje
                                Toast.makeText(
                                    view.context,
                                    "Saliste del grupo ${APIUtils.selectedGroup!!.group_name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Se llama a la función de salida de grupo
                                funcionSalirGrupo()
                            }
                        }.setNegativeButton("No") { _, _ -> }.show()
                }
            }
        }
        return view
    }
}