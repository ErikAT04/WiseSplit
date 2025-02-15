package androids.erikat.wisesplit

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.ActivityGroupBinding
import androids.erikat.wisesplit.fragments.FragmentGroupMain
import androids.erikat.wisesplit.fragments.FragmentGroupNotifications
import androids.erikat.wisesplit.fragments.FragmentGroupSettings
import androids.erikat.wisesplit.fragments.FragmentGroupUsers
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace

//Actividad de los grupos
class GroupActivity : AppCompatActivity() {
    lateinit var binding:ActivityGroupBinding
    //Función de Cambio de Toolbar
    var funcionCambioToolbar:(Toolbar, String)->Unit = { toolbar, s ->
        //Pone el toolbar del fragmento en la barra de acciones
        setSupportActionBar(toolbar)
        //Pone un título
        supportActionBar?.title = s
        //Añade el botón de volver hacia atrás
        var upArrow = getDrawable(R.drawable.baseline_arrow_back_24);
        upArrow?.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }

    //Fragmentos
    var fragmentoPrincipal:FragmentGroupMain = FragmentGroupMain(funcionCambioToolbar)
    var fragmentoUsuarios:FragmentGroupUsers = FragmentGroupUsers(funcionCambioToolbar)
    var fragmentoNotificaciones:FragmentGroupNotifications = FragmentGroupNotifications(funcionCambioToolbar)
    var fragmentoAjustes:FragmentGroupSettings = FragmentGroupSettings(funcionCambioToolbar){ //Función de cierre de sesión en grupo
        finish() //Termina la actividad
        funcionSalir() //Llama a la función encargada de actualizar la lista
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Carga el binding
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Cambia el fragmento al principal
        cambiarPantalla(fragmentoPrincipal)

        //Carga las opciones del cambio de pantallas del bottomnavbar
        binding.vistaGrupoBottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_group_main -> {
                    cambiarPantalla(fragmentoPrincipal)
                }
                R.id.navigation_group_users -> {
                    cambiarPantalla(fragmentoUsuarios)
                }
                R.id.navigation_group_notifications -> {
                    cambiarPantalla(fragmentoNotificaciones)
                }
                R.id.navigation_group_settings -> {
                    cambiarPantalla(fragmentoAjustes)
                }
            }
            true
        }
    }
    //Función de cambio de pantalla
    fun cambiarPantalla(f:Fragment){
        supportFragmentManager.commit { //Cambia el fragmento al pasado por pantalla
            replace(binding.grupoFragmentContainerView.id, f)
        }
    }
    //Si el usuario da al botón de atrás, termina la actividad
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return true
    }

    companion object{
        var funcionSalir:()->Unit = { //Función sobrescrita más tarde
        }
    }
}