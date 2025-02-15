package androids.erikat.wisesplit.Model

import android.media.Image

//Usuario de la aplicación
class User(var email:String, //Correo electrónico
           var username:String, //Nombre de Usuario
           var password:String, //Contraseña
           var image:String //Imagen de perfil
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return email == other.email
    }
}