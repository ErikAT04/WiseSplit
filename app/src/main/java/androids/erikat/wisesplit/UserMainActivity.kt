package androids.erikat.wisesplit

import android.content.Intent
import android.os.Bundle
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Utils.APIUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androids.erikat.wisesplit.databinding.ActivityUserMainBinding
import androids.erikat.wisesplit.fragments.GroupListFragment
import androids.erikat.wisesplit.fragments.UserConfigFragment
import androids.erikat.wisesplit.fragments.UserNotifsFragment
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

//Actividad del Usuario
class UserMainActivity : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityUserMainBinding

    //Fragmento de Grupos
    var fragmentoGrupos = GroupListFragment({ toolbar, texto-> //Función de cambio de toolbar
        cambiarToolbar(toolbar, texto)
    }, { //Función de inicio de actividad de grupo
        var act = this
        lifecycleScope.launch {
            //Carga en el provider si el usuario es admin en ese grupo e inicia la actividad
            APIUtils.isThisUserAdmin = UserDAO().checkIfThisUserIsAdmin(APIUtils.mainUser!!, APIUtils.selectedGroup!!)
            startActivity(Intent(act, GroupActivity::class.java))
        }
    })
    //Fragmento de Notificaciones
    var fragmentoNotificaciones = UserNotifsFragment(){ toolbar, texto-> //Función de cambio de toolbar
        cambiarToolbar(toolbar, texto)
    }
    //Fragmento de Configuración
    var fragmentoConfiguracion = UserConfigFragment(){ toolbar, texto-> //Función de cambio de toolbar
        cambiarToolbar(toolbar, texto)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Carga el Binding
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Carga la pantalla principal
        cambiarPantallas(fragmentoGrupos)
        //Carga las opciones de cambio de pantalla del menú de navegación inferior
        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener { menuitem->
            when(menuitem.itemId){
                R.id.navigation_home -> {
                    cambiarPantallas(fragmentoGrupos)
                }
                R.id.navigation_notifications -> {
                    cambiarPantallas(fragmentoNotificaciones)
                }
                R.id.navigation_dashboard -> {
                    cambiarPantallas(fragmentoConfiguracion)
                }
            }
            true
        }

    }
    //Función de Cambio de Pantallas
    fun cambiarPantallas(fragment:Fragment){
        supportFragmentManager.commit { //Carga en el container el fragmento pasado por parámetro
            replace(R.id.userMainFragmentContainer, fragment)
        }
    }
    //Función de cambio de toolbar
    fun cambiarToolbar(toolbar: Toolbar, texto:String){
        //Pone el toolbar del fragment actual en el toolbar
        setSupportActionBar(toolbar)
        //Cambia el texto del toolbar
        supportActionBar?.title = texto
    }
}