
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.LoginActivity
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils

class RegisterDialog(var loginActivity:LoginActivity): DialogFragment() {
    lateinit var userField:TextView
    lateinit var passField:TextView
    lateinit var emailField:TextView
    lateinit var repPassField:TextView
    lateinit var btt:Button
    lateinit var user:User
    var loginCorrect = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return activity.let {
            val builder:AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            //Infla la vista y declara las instancias de todas las vistas
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_register, null)
            builder.setView(view)
            userField = view.findViewById(R.id.unameTextRegister)
            passField = view.findViewById(R.id.passTextRegister)
            emailField = view.findViewById(R.id.emailTextRegister)
            repPassField = view.findViewById(R.id.repPassRegister)
            btt = view.findViewById(R.id.finishRegisterBtt)
            //Al pulsar a registrar, se lleva a cabo la función de registro
            btt.setOnClickListener {
                register()
                if (loginCorrect){
                    dismiss()
                    loginActivity.register(user)
                }
            }
            builder.create()
        }
    }

    private fun register() {
        val username:String = userField.text.toString()
        val password:String = passField.text.toString()
        val repPassword:String = repPassField.text.toString()
        val email:String = emailField.text.toString()

        //Si se ha entrado antes a esta función, desaparecen todos los errores para dar entrada a los posibles siguientes.
        userField.error = null
        passField.error = null
        repPassField.error = null
        emailField.error = null
        //Si alguno de los campos está vacío lo indica su error
        if(username.isEmpty() || password.isEmpty() || repPassword.isEmpty() || email.isEmpty()){
            userField.error = if(username.isEmpty())"Rellene este campo" else null
            passField.error = if (password.isEmpty())"Rellene este campo" else null
            repPassField.error = if (repPassword.isEmpty())"Rellene este campo" else null
            emailField.error = if (email.isEmpty())"Rellene este campo" else null
        } else {
            //Si el email no cumple la expresión regular de los correos electrónicos:
            if (!email.matches(Regex("^[\\w \\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))){
                emailField.error = "El correo electrónico no cumple los requisitos"
            } else {
                lifecycleScope.launch {
                    var dao = UserDAO()
                    //Se busca un usuario con el nombre dado
                    var u: User? = dao.getItem(username)
                    if (u != null) { //Si existe, se marca que no puede haber un usuario con el nombre escrito
                        userField.error = "Ya existe un usuario con este nombre"
                    } else {
                        //Se busca un usuario con ese email
                        u = dao.getItem(email)
                        if (u != null) { //Si existe, se avisa de que no se puede poner ese DNI
                            emailField.error = "El correo electrónico ya se está utilizando"
                        } else {
                            if(passField.text.toString() == repPassField.text.toString()) {
                                //Se encripta la contraseña con SHA256
                                var passEncrypted = DigestUtils.sha256Hex(password)
                                //Se genera un usuario con una foto por defecto, su correo, su contraseña encriptada y su nombre
                                u = User(
                                    email,
                                    username,
                                    passEncrypted,
                                    "https://i.discogs.com/_kK2FFfyrhNnbdTjCGMqfy_2gsMw120aUhKTb3M9kyE/rs:fit/g:sm/q:40/h:300/w:300/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzMTk5/NTg3LTE1NDk4MTEy/MjMtNTczMC5qcGVn.jpeg"
                                )
                                //Se inserta el usuario generado
                                dao.insert(u)
                                //Se convierte en el usuario principal
                                APIUtils.mainUser = u
                                //Se cambia de pantalla
                                loginActivity.cambiarPantalla()
                                //Cierra dialog
                                dismiss()
                            } else { //Si las contraseñas no son iguales
                                repPassField.error = "Las contraseñas no coinciden"
                            }
                        }
                    }
                }
            }
        }
    }
}