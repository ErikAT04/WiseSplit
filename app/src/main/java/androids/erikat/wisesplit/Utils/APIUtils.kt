package androids.erikat.wisesplit.Utils

import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Clase que controla la API y distintas variables
class APIUtils {
    companion object{
        //Grupo seleccionado actualmente
        var selectedGroup: Group? = null
        //Usuario seleccionado actualmente
        var mainUser:User? = null
        //Marca si el usuario seleccionado es administrador del grupo actual
        var isThisUserAdmin:Boolean = false
        val retrofit = Retrofit.Builder()
            //ES NECESARIO CAMBIAR EL HOST POR EL QUE TENGA LA API
            .baseUrl("http://192.168.1.130:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}