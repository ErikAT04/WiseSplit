package androids.erikat.wisesplit

import RegisterDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.ActivityLoginBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils
import kotlin.concurrent.fixedRateTimer

//Actividad de Inicio de Sesión
class LoginActivity : AppCompatActivity() {
    //Binding y vistas
    lateinit var viewBinding: ActivityLoginBinding
    lateinit var emailOrUserField: EditText
    lateinit var passwordField: EditText
    lateinit var loginBtt: Button
    lateinit var registerBtt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        iniciarComponentes()
    }

    private fun iniciarComponentes() {
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setSupportActionBar(viewBinding.loginToolbar)
        supportActionBar!!.title = "Iniciar Sesión"
        setContentView(viewBinding.root)
        emailOrUserField = viewBinding.userOrEmailTextLogIn
        passwordField = viewBinding.passwordFieldLogin
        loginBtt = viewBinding.logInBtt
        registerBtt = viewBinding.initRegisterBtt

        //Si se pulsa al botón de iniciar sesión, se carga su función
        loginBtt.setOnClickListener {
            logIn()
        }

        registerBtt.setOnClickListener { //Se abre un menú de registro
            var dialog = RegisterDialog(this)
            dialog.show(supportFragmentManager, "Registrarse")
        }
    }
    //Función de Inicio de sesión
    private fun logIn() {
        //Se guardan el nombre o correo de usuario y la contraseña
        var userData: String = emailOrUserField.text.toString()
        var pass: String = passwordField.text.toString()

        //Se ponen los errores en null
        emailOrUserField.error = null
        passwordField.error = null

        //Se comprueban que los datos no estén vacíos
        if (userData.isEmpty() || pass.isEmpty()) {
            emailOrUserField.error =
                if (userData.isEmpty()) "Introduce un correo electrónico o nombre de usuario" else null
            passwordField.error = if (pass.isEmpty()) "Introduce una contraseña" else null
        } else {
            var u: User?
            lifecycleScope.launch {
                try {
                    //Se busca un usuario con los datos dados
                    u = UserDAO().getItem(emailOrUserField.text.toString())
                    //Se encripta la contraseña
                    var passEncrypted = DigestUtils.sha256Hex(passwordField.text.toString())
                    //Si no se encuentra el usuario con esos datos, avisa por pantalla
                    if (u == null) {
                        emailOrUserField.error =
                            "No se ha encontrado el usuario con esas credenciales"
                    } else {
                        //Si lo encuentra, comprueba que la contraseña sea correcta
                        if (passEncrypted == u!!.password) {
                            APIUtils.mainUser = u
                            //Si es asi, muestra el texto de que se ha iniciado sesión y cambia de pantalla
                            Toast.makeText(
                                this@LoginActivity,
                                "Todo en orden, ${APIUtils.mainUser!!.username}",
                                Toast.LENGTH_SHORT
                            ).show()
                            cambiarPantalla()
                        } else {
                            passwordField.error = "Contraseña incorrecta"
                        }
                    }
                }catch (e:Exception){
                    viewBinding.userOrEmailTextLogIn.error = "No existe el usuario introducido"
                }
            }
        }
    }

    //Función de Registro: Como todos los fallos se controlan en el menú de registro, aquí basta con insertar el usuario
    fun register(u: User) {
        lifecycleScope.launch {
            //Inserta el usuario
            UserDAO().insert(u)
            //Pone el usuario en el provider de la aplicación
            APIUtils.mainUser = u
            Toast.makeText(
                this@LoginActivity,
                "Todo en orden, ${APIUtils.mainUser!!.username}",
                Toast.LENGTH_SHORT
            ).show()
            //Cambia de pantalla
            cambiarPantalla()
        }
    }

    fun cambiarPantalla() {
        //Crea un intent explícito de una actividad y lo carga
        val miintent = Intent(this, UserMainActivity::class.java)
        startActivity(miintent)
    }
}