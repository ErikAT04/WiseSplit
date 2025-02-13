package androids.erikat.wisesplit.services

import androids.erikat.wisesplit.DTO.GroupDTO
import androids.erikat.wisesplit.Model.Group
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//Servicio API que controla los grupos
interface GroupService{
    //Lista de todos los grupos
    @GET("groups/")
    suspend fun getAllGroups():Response<List<Group>>
    //Obtención de un grupo específico
    @GET("groups/{id}")
    suspend fun getGroup(@Path("id") id:Int):Response<Group>
    //Lista de grupos de un usuario
    @GET("groups/fromUser/{email}")
    suspend fun getGroupsFromUser(@Path("email") email:String):Response<List<Group>>
    //Actualización de parámetros de un grupo
    @PUT("groups/{id}/actualizar")
    suspend fun updateGroup(@Path("id") id:Int, @Body group:GroupDTO):Response<String>
    //Inserción de grupo en BD
    @POST("groups/insertar")
    suspend fun insertGroup(@Body group: GroupDTO):Response<String>
    //Eliminación de un grupo
    @DELETE("groups/{id}/borrar")
    suspend fun eraseGroup(@Path("id") id:Int):Response<String>
    //Inserción de usuario en un grupo
    @PUT("groups/{id}/insertarUsuario/{email}")
    suspend fun insertarUsuario(@Path("id") id:Int, @Path("email") email:String):Response<String>
    //Promoción de un usuario en un grupo a administrador
    @PUT("groups/{id}/promocionarUsuario/{email}")
    suspend fun promoteUser(@Path("id") id: Int, @Path("email") email: String):Response<String>
    //Eliminación de un usuario en un grupo
    @DELETE("groups/{id}/eliminarUsuario/{email}")
    suspend fun eraseUser(@Path("id") id: Int, @Path("email") email: String):Response<String>

}