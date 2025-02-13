package androids.erikat.wisesplit.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androids.erikat.wisesplit.DAO.GroupDAO
import androids.erikat.wisesplit.DAO.NotifDAO
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.AlertInvitarUsuarioBinding
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class InviteUserDialog : DialogFragment() {
    lateinit var binding: AlertInvitarUsuarioBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            var builder = AlertDialog.Builder(requireActivity())
            //Infla la vista del builder y su binding
            var v = layoutInflater.inflate(R.layout.alert_invitar_usuario, null)
            binding = AlertInvitarUsuarioBinding.bind(v)

            binding.invitarPersonaBtt.setOnClickListener {
                lifecycleScope.launch {
                    try {
                        binding.escribirCorreoParaIntroducirEText.error = null
                        if(binding.escribirCorreoParaIntroducirEText.text.isEmpty()){ //Si el texto está vacío:
                            binding.escribirCorreoParaIntroducirEText.error = "No puedes dejar este campo vacío"
                        } else {
                            //Carga un usuario de la API con el nombre escrito
                            var u:User? = UserDAO().getItem(binding.escribirCorreoParaIntroducirEText.text.toString())
                            //Si el usuario no es nulo y no está ya en el grupo:
                            if(u!=null && u.email !in UserDAO().getUsersFromGroup(APIUtils.selectedGroup!!).map { it.email }){
                                var yaEstaInvitado = false //Booleana que controla si el usuario está ya invitado
                                //Se cargan todas las invitaciones que tiene el grupo actualmente
                                var allInvitations = NotifDAO().getNotifsFromGroup(APIUtils.selectedGroup!!)
                                //Se comprueba que el email no aparezca ya en la lista de invitaciones
                                allInvitations.forEach {
                                    if (it.userDest.email == u.email){
                                        yaEstaInvitado = true
                                    }
                                }
                                if(yaEstaInvitado) { //Si aparece el email en las invitaciones:
                                    binding.escribirCorreoParaIntroducirEText.error = "Este usuario ya está invitado a este grupo"
                                }else { //Si no: Se inserta una notificación nueva con el grupo, el usuario y la fecha actual
                                    NotifDAO().insert(
                                        androids.erikat.wisesplit.Model.Notification(
                                            0,
                                            APIUtils.selectedGroup!!,
                                            u,
                                            LocalDate.now()
                                        )
                                    )
                                    //Muestra el mensaje
                                    Toast.makeText(
                                        context,
                                        "Usuario invitado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dismiss()
                                }

                            }else if(u==null){ //Si el usuario es nulo, es decir, si no se encuentra el usuario en la bd:
                                binding.escribirCorreoParaIntroducirEText.error = "No se ha encontrado este usuario"
                            }else { //Si el usuario se encuentra en el grupo:
                                binding.escribirCorreoParaIntroducirEText.error = "Este usuario ya se encuentra en el grupo"
                            }
                        }
                    }catch (e:Exception){ //Puede saltar una excepción por la forma que tiene la API de devolver datos en caso de que no encuentre nada, por lo que se controla:
                        binding.escribirCorreoParaIntroducirEText.error = "No se ha encontrado este usuario"
                    }
                }
            }

            //Se carga la vista en el builder y se crea
            builder.setView(v)
            builder.create()
        }
    }
}