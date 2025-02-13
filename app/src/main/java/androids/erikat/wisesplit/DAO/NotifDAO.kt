package androids.erikat.wisesplit.DAO

import android.os.Build
import androids.erikat.wisesplit.DTO.NotifDTO
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.Utils.APIUtils.Companion.retrofit
import androids.erikat.wisesplit.services.NotifService
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.LocalDateTime

class NotifDAO : DAO<Int, Notification> {
    //Servicio de API de Notificaciones
    val apiService = retrofit.create(NotifService::class.java)

    //Función de Inserción
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insert(obj: Notification): Boolean {
        //Se crea un una instancia de un objeto DTO con los datos de la notificación
        var dto: NotifDTO = NotifDTO(
            0,
            obj.userDest.email,
            obj.group.id!!,
            obj.date.toString()
        )
        //Se crea una petición de inserción con ese DTO
        var response = apiService.insertNotif(dto)
        //Se guarda si la respuesta a esa petición es correcta
        var resp = response.isSuccessful
        //Se devuelve esa booleana
        return resp
    }

    //Función de Actualización
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun update(obj: Notification): Boolean {
        //Se crea un una instancia de un objeto DTO con los datos de la notificación
        var dto: NotifDTO = NotifDTO(
            obj.id,
            obj.userDest.email,
            obj.group.id!!,
            obj.date.toString()
        )
        //Se crea una petición de actualización con ese DTO y el id de la notificación
        var response = apiService.updateNotifs(obj.id!!, dto)
        //Se guarda si la respuesta a esa petición es correcta
        var resp = response.isSuccessful
        //Se devuelve esa booleana
        return resp
    }

    //Función de Eliminación
    override suspend fun delete(obj: Notification): Boolean {
        //Se crea una petición de eliminación con el ID de la notificación
        var response = apiService.eraseNotif(obj.id!!)
        //Se guarda si la respuesta a esa petición es correcta
        var resp = response.isSuccessful
        //Se devuelve esa booleana
        return resp
    }

    //Función de Obtención
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getItem(value: Int): Notification? {
        //Se crea una instancia de DTO nula
        var notif: NotifDTO? = null
        //Se realiza una petición de obtención con el id de la notificación
        var response = apiService.getNotif(value)
        //Si la respuesta es correcta
        if (response.isSuccessful) {
            //Se guarda el DTO obtenido
            notif = response.body()!!
            //Devuelve una notificación con su ID, el grupo al que pertenece, el usuario y la fecha
            return Notification(
                notif.notif_id,
                GroupDAO().getItem(notif.group_id)!!,
                UserDAO().getItem(notif.user_email)!!,
                LocalDate.parse(notif.notif_date)
            )
        }
        //Si la respuesta no es correcta, devuelve un objeto nulo
        return null
    }
    //Función de Listado
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getList(): List<Notification> {
        //Se crea una lista de DTO vacía
        var notifs: List<NotifDTO> = mutableListOf()
        //Se hace una petición api
        var response = apiService.getAllNotifs()
        //Si la API devuelve una respuesta correcta, carga la lista de notificaciones
        if (response.isSuccessful) {
            notifs = response.body()!!
        }
        //Devuelve la lista mapeada de NotifDTO a Notification
        return notifs.map {
            Notification(
                it.notif_id,
                GroupDAO().getItem(it.group_id)!!,
                UserDAO().getItem(it.user_email)!!,
                LocalDate.parse(it.notif_date)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNotifsFromUser(u: User): List<Notification> {
        //Se crea una lista de DTO vacía
        var notifs: List<NotifDTO> = mutableListOf()
        //Se hace una petición api de listado de notificaciones de un usuario
        var response = apiService.getNotifsFromUser(u.email)
        //Si la API devuelve una respuesta correcta, carga la lista de notificaciones
        if (response.isSuccessful) {
            notifs = response.body()!!
        }
        //Devuelve la lista mapeada de NotifDTO a Notification
        return notifs.map {
            Notification(
                it.notif_id,
                GroupDAO().getItem(it.group_id)!!,
                UserDAO().getItem(it.user_email)!!,
                LocalDate.parse(it.notif_date)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNotifsFromGroup(g: Group): List<Notification> {
        //Se crea una lista de DTO vacía
        var notifs: List<NotifDTO> = mutableListOf()
        //Se hace una petición API para obtener las notificaciones de un grupo
        var response = apiService.getNotifsFromGroup(g.id!!)
        //Si la API devuelve una respuesta correcta, carga la lista de notificaciones
        if (response.isSuccessful) {
            notifs = response.body()!!
        }
        //Devuelve la lista mapeada de NotifDTO a Notification
        return notifs.map {
            Notification(
                it.notif_id,
                GroupDAO().getItem(it.group_id)!!,
                UserDAO().getItem(it.user_email)!!,
                LocalDate.parse(it.notif_date)
            )
        }
    }
}