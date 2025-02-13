package androids.erikat.wisesplit.DAO

import android.util.Log
import androids.erikat.wisesplit.DTO.GroupDTO
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.Utils.APIUtils.Companion.retrofit
import androids.erikat.wisesplit.services.GroupService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.PreparedStatement

//Clase de Acceso a datos de la tabla 'grupos'
class GroupDAO : DAO<Int, Group> {
    //Servicio de la API de Grupos
    val apiService = retrofit.create(GroupService::class.java)
    //Función de Inserción
    override suspend fun insert(obj: Group): Boolean {
        //Se crea una clase DTO que carga datos del grupo a introducir en la base de datos
        var dto: GroupDTO = GroupDTO(0, obj.group_name, obj.image)
        //Se hace una petición de inserción en la API con ese grupo
        var response = apiService.insertGroup(dto)
        //Se hace la petición y se guarda si se ha completado correctamente
        var resp = response.isSuccessful
        //Devuelve la respuesta
        return resp
    }
    //Función de Actualización
    override suspend fun update(obj: Group): Boolean {
        //Se crea una clase DTO que carga datos del grupo a editar en la base de datos
        var dto: GroupDTO = GroupDTO(obj.id, obj.group_name, obj.image)
        //Se hace una petición de actualización con el DTO y el id del grupo
        var response = apiService.updateGroup(obj.id!!, dto)
        //Se hace la petición y se guarda si se ha completado correctamente
        var resp = response.isSuccessful
        //Devuelve la respuesta
        return resp
    }
    //Función de Eliminación
    override suspend fun delete(obj: Group): Boolean {
        //Se hace una petición de eliminación con el id del grupo
        var response = apiService.eraseGroup(obj.id!!)
        //Se hace la petición y se guarda si se ha completado correctamente
        var resp = response.isSuccessful
        //Devuelve la petición
        return resp
    }

    override suspend fun getItem(value: Int): Group? {
        //Se crea un objeto nulo de grupo
        var group: Group? = null
        //Se hace una petición para conseguir el grupo
        var response = apiService.getGroup(value)
        //Se hace la petición y, si se completa correctamente, guarda el grupo en la variable
        if (response.isSuccessful) {
            group = response.body()
        }
        //Se devuelve el grupo, sea o no nulo
        return group
    }
    //Función de Listado
    override suspend fun getList(): List<Group> {
        //Se crea una lista vacía
        var groupList: List<Group> = mutableListOf()
        //Se hace una petición para obtener todos los grupos
        var response = apiService.getAllGroups()
        //Si la respuesta es correcta, se guarda la lista obtenida del API
        if (response.isSuccessful) {
            groupList = response.body()!!
        }
        //Devuelve la lista, vacía o con datos
        return groupList
    }
    //Función de Listado de grupos de un mismo usuario
    suspend fun getListFromUser(u: User): List<Group> {
        //Se crea una lista vacía
        var groupList: List<Group> = mutableListOf()
        //Se hace una petición para obtener todos los grupos que tengan una relación con este usuario.
        var response = apiService.getGroupsFromUser(u.email)
        //Si la respuesta es correcta, se guarda la lista obtenida del API
        if (response.isSuccessful) {
            groupList = response.body()!!
        }
        //Devuelve la lista, vacía o con datos
        return groupList
    }
    //Función de Inserción de usuario en grupo
    suspend fun insertarUsuario(user: User, group:Group): Boolean {
        //Se hace una petición para insertar un usuario (con su email) en un grupo
        var response = apiService.insertarUsuario(group.id!!, user.email)
        //Se guarda si la respuesta de la api es correcta
        var resp = response.isSuccessful
        //Devuelve esa booleana
        return resp
    }
    //Función de Promoción de Usuario
    suspend fun promocionarUsuario(id: Int, email: String):String {
        //Se hace una petición para actualizar el estado de usuario de no administrador a administrador
        var res = apiService.promoteUser(id, email)
        //Si se completa correctamente, actualiza al usuario y muestra el mensaje dado por la API
        if(res.isSuccessful){
            return res.body()!!
        } else {
            return "Error de BD"
        }
    }
    //Función ed Expulsión de Usuario
    suspend fun expulsarUsuario(id: Int, email: String):String {
        //Se hace una petición para borrar un usuario del grupo
        var res = apiService.eraseUser(id, email)
        //Si se completa correctamente, borra el usuario y muestra el mensaje dado por la API
        if(res.isSuccessful){
            return res.body()!!
        } else {
            return "Error de BD"
        }
    }
}