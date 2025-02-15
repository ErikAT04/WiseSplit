package androids.erikat.wisesplit.services
import androids.erikat.wisesplit.DTO.PayerDTO
import androids.erikat.wisesplit.DTO.PaymentDTO
import androids.erikat.wisesplit.DTO.UserDTO
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.Payment
import androids.erikat.wisesplit.Model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//Servicio API que controla los pagos
interface PaymentService {
    //Listar todos los pagos
    @GET("payments/")
    suspend fun getAllPayments():Response<List<PaymentDTO>>
    //Buscar un pago por ID
    @GET("payments/{id}")
    suspend fun getPayment(@Path("id") id:Int):Response<PaymentDTO>
    //Actualizar pago
    @PUT("payments/{id}/actualizar")
    suspend fun updatePayment(@Path("id") id:Int, @Body payment: PaymentDTO):Response<String>
    //Insertar pago
    @POST("payments/insertar")
    suspend fun insertPayment(@Body payment: PaymentDTO):Response<String>
    //Borrar pago
    @DELETE("payments/{id}/borrar")
    suspend fun erasePayment(@Path("id") id:Int):Response<String>
    //Buscar lo que usuario debe al resto del grupo
    @GET("payments/{groupId}/getTotalFrom/{userEmail}")
    suspend fun getUserDebt(@Path("groupId") groupId:Int, @Path("userEmail") email:String):Response<String>
    //Buscar pagos de un grupo
    @GET("payments/group/{groupId}")
    suspend fun getPaymentsFromGroup(@Path("groupId") id:Int):Response<List<PaymentDTO>>
    //Actualizar estado de pago de una persona
    @PUT("users/{email}/pay/{payment}")
    suspend fun payUser(@Path("email") username: String, @Path("payment")id: Int): Response<String>
    //Buscar lo que el grupo debe a un usuario
    @GET("payments/{groupId}/getIOUsTo/{email}")
    suspend fun getMyPayments(@Path("email") email: String,@Path("groupId") id: Int): Response<Double>
    //Insertar usuario en un pago
    @POST("payments/addUserToPayment")
    suspend fun insertUserInPayment(@Body payerDTO: PayerDTO): Response<String>
    //Borrar todos los usuarios de un pago
    @DELETE("payments/{id}/erasePayers")
    suspend fun removeAllPayersFromPayment(@Path("id") id: Int): Response<String>


}