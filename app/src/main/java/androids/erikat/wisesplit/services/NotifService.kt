package androids.erikat.wisesplit.services
import androids.erikat.wisesplit.DTO.NotifDTO
import androids.erikat.wisesplit.Model.Notification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//Servicio API que controla las notificaciones
interface NotifService {
    //Obtener todas las notificaciones
    @GET("notifs/")
    suspend fun getAllNotifs():Response<List<NotifDTO>>
    //Obtener notificación por ID
    @GET("notifs/{id}")
    suspend fun getNotif(@Path("id") id:Int):Response<NotifDTO>
    //Obtener notificaciones de un usuario
    @GET("notifs/fromUser/{email}")
    suspend fun getNotifsFromUser(@Path("email") email:String):Response<List<NotifDTO>>
    //Obtener notificaciones de un grupo
    @GET("notifs/fromGroup/{id}")
    suspend fun getNotifsFromGroup(@Path("id") id:Int):Response<List<NotifDTO>>
    //Actualizar notificaciones
    @PUT("notifs/{id}/actualizar")
    suspend fun updateNotifs(@Path("id") id:Int, @Body notif:NotifDTO):Response<String>
    //Insertar notificación
    @POST("notifs/insertar")
    suspend fun insertNotif(@Body notif:NotifDTO):Response<String>
    //Borrar notificación
    @DELETE("notifs/{id}/borrar")
    suspend fun eraseNotif(@Path("id") id:Int):Response<String>
}