package androids.erikat.wisesplit.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.DialogCambiarContraseniaBinding
import androids.erikat.wisesplit.databinding.DialogCambiarNombreBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils

//Dialog que aparece para cambiar de nombre de usuario
class ChangeNameDialog(var funcion:()->Unit):DialogFragment() {
    lateinit var binding: DialogCambiarNombreBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            //Se carga el builder, la vista y el binding
            var builder = AlertDialog.Builder(it)
            var v = layoutInflater.inflate(R.layout.dialog_cambiar_nombre, null)
            binding = DialogCambiarNombreBinding.bind(v)
            //Se activa el listener del botón de cambiar
            binding.cambiarNombreBtt.setOnClickListener {
                //Se quitan los errores
                binding.escribeNombreNuevoEText.error = null
                binding.confirmarConContraEText.error = null
                //Se notifica si alguno de los campos está vacío
                if(binding.escribeNombreNuevoEText.text.isEmpty() || binding.confirmarConContraEText.text.isEmpty()){
                    binding.escribeNombreNuevoEText.error = if(binding.escribeNombreNuevoEText.text.isEmpty()) "No dejes el campo vacío" else null
                    binding.confirmarConContraEText.error = if(binding.confirmarConContraEText.text.isEmpty()) "No dejes el campo vacío" else null
                } else {
                    //Se guarda el nombre y la contraseña encriptada
                    var nombreNuevo:String = binding.escribeNombreNuevoEText.text.toString()
                    var password:String = DigestUtils.sha256Hex(binding.confirmarConContraEText.text.toString())
                    //Se comprueba que el usuario no sea el mismo
                    if(nombreNuevo == APIUtils.mainUser!!.username){
                        binding.escribeNombreNuevoEText.error = "El nombre no puede ser igual que el actual"
                    } else {
                        //Se comprueba que la contraseña sea la misma
                        if (password != APIUtils.mainUser!!.password){
                            binding.confirmarConContraEText.error = "Contraseña incorrecta"
                        }else{
                            lifecycleScope.launch {
                                //Se busca un usuario con ese nombre
                                var u:User? = UserDAO().getItem(nombreNuevo)
                                //Si se encuentra uno, se avisa al usuario
                                if(u!=null){
                                    binding.escribeNombreNuevoEText.error = "Este nombre ya es utilizado por otra persona"
                                }else {
                                    //Si no, se cambia el nombre de usuario
                                    APIUtils.mainUser!!.username = nombreNuevo
                                    UserDAO().update(APIUtils.mainUser!!)
                                    Toast.makeText(v.context, "¡Nombre Cambiado!", Toast.LENGTH_SHORT)
                                    funcion()
                                    dismiss()
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