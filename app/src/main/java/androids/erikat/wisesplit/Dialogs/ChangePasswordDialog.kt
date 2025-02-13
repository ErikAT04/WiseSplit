package androids.erikat.wisesplit.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.DialogCambiarContraseniaBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils

class ChangePasswordDialog() : DialogFragment() {
    lateinit var binding:DialogCambiarContraseniaBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            //Se carga el builder, la vista y el binding
            var builder = AlertDialog.Builder(it)
            var v = layoutInflater.inflate(R.layout.dialog_cambiar_contrasenia, null)
            binding = DialogCambiarContraseniaBinding.bind(v)
            //Se carga el listener del botón de cambiar
            binding.button.setOnClickListener {
                //Se ponen los errores en null
                binding.repPassEText.error = null
                binding.passActualEText.error = null
                binding.nuevaPassEText.error = null
                //Se comprueba que todos los campos estén rellenos
                if(binding.repPassEText.text.isEmpty() || binding.passActualEText.text.isEmpty() || binding.nuevaPassEText.text.isEmpty()){
                    binding.nuevaPassEText.error = if(binding.nuevaPassEText.text.isEmpty()) "No puedes dejar el campo vacío" else null
                    binding.repPassEText.error = if(binding.repPassEText.text.isEmpty()) "No puedes dejar el campo vacío" else null
                    binding.passActualEText.error = if(binding.passActualEText.text.isEmpty()) "No puedes dejar el campo vacío" else null
                } else {
                    //Se guarda la contraseña antigua y se encripta
                    var contraAntigua = binding.passActualEText.text.toString()
                    var antiguaEncriptada = DigestUtils.sha256Hex(contraAntigua)
                    //Se comprueba que la contraseña actual coincida
                    if(APIUtils.mainUser!!.password != antiguaEncriptada){
                        binding.nuevaPassEText.error = "Contraseña Incorrecta"
                    } else {
                        //Se comprueba que la contraseña nueva y la antigua no sean iguales
                        if(binding.passActualEText.text.toString() == binding.nuevaPassEText.text.toString()){
                            binding.nuevaPassEText.error = "La nueva contraseña no puede ser igual a la anterior"
                        } else {
                            //Se comprueba que las contraseñas nueva y la de repetición coincidan
                            if(binding.nuevaPassEText.text.toString() != binding.repPassEText.text.toString()){
                                binding.repPassEText.error = "Las contraseñas no coinciden"
                            } else {
                                lifecycleScope.launch {
                                    //Se cambia la contraseña del usuario
                                    APIUtils.mainUser!!.password = DigestUtils.sha256Hex(binding.repPassEText.text.toString())
                                    UserDAO().update(APIUtils.mainUser!!) //Se actualiza
                                    Toast.makeText(v.context, "Contraseña Cambiada", Toast.LENGTH_SHORT).show() //Se muestra
                                    dismissNow() //Cierre
                                }
                            }
                        }
                    }
                }
            }
            builder.setView(v)
            builder.create()
        }
    }
}