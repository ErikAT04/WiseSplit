package androids.erikat.wisesplit.services
import androids.erikat.wisesplit.DTO.PayerDTO
import androids.erikat.wisesplit.DTO.UserDTO
import androids.erikat.wisesplit.Model.Payer
import androids.erikat.wisesplit.Model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//Servicio API que controla los usuarios
interface UserService {
    //Listado de todos los usuarios
    @GET("users/")
    suspend fun getAllUsers():Response<List<User>>
    //Búsqueda de usuario por ID (email o nombre de usuario)
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id:String):Response<User>
    //Borrar usuario por email
    @DELETE("users/{email}/borrar")
    suspend fun eraseUser(@Path("email") email:String):Response<String>
    //Actualizar datos de usuario por email
    @PUT("users/{email}/actualizar")
    suspend fun updateUser(@Path("email") email:String, @Body user:UserDTO):Response<String>
    //Insertar usuario en bd
    @POST("users/insertar")
    suspend fun insertUser(@Body user:UserDTO):Response<String>
    //Obtener los usuarios de un grupo
    @GET("users/fromGroup/{id}")
    suspend fun getUsersFromGroup(@Path("id") id: Int): Response<List<User>>
    //Obtiene true si el usuario es administrador del grupo
    @GET("users/{email}/isAdminIn/{group}")
    suspend fun getIfUserIsAdmin(@Path("email") email: String, @Path("group") id: Int?):Response<Boolean>
    //Obtiene los usuarios de un pago
    @GET("users/fromPayment/{payment}")
    suspend fun getUsersFromPayment(@Path("payment") id: Int): Response<List<PayerDTO>>
}