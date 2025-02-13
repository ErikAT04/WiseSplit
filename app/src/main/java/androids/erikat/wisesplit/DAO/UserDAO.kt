package androids.erikat.wisesplit.DAO

import androids.erikat.wisesplit.DTO.PayerDTO
import androids.erikat.wisesplit.DTO.UserDTO
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.Utils.APIUtils.Companion.retrofit
import androids.erikat.wisesplit.Model.Payer
import androids.erikat.wisesplit.services.UserService

//Clase de Acceso a Datos de lo relacionado al usuario
class UserDAO : DAO<String, User> {
    //Servicio API de usuarios
    val apiService = retrofit.create(UserService::class.java)
    //Función de Inserción
    override suspend fun insert(obj: User): Boolean {
        //Crea una instancia DTO con los datos del usuario
        var dto: UserDTO = UserDTO(obj.email, obj.username, obj.password, obj.image)
        //Hace una petición API de inserción de ese DTO
        var response = apiService.insertUser(dto)
        //Guarda si la petición es correcta
        var resp = response.isSuccessful
        //Devuelve esa booleana
        return resp
    }
    //Función de Actualización
    override suspend fun update(obj: User): Boolean {
        //Crea una instancia DTO con los datos del usuario
        var dto: UserDTO = UserDTO(obj.email, obj.username, obj.password, obj.image)
        //Hace una petición API de actualización del usuario con su email y el DTO con los datos a cambiar
        var response = apiService.updateUser(obj.email, dto)
        //Guarda si la petición es correcta
        var resp = response.isSuccessful
        //Devuelve esa booleana
        return resp
    }
    //Función de Eliminación
    override suspend fun delete(obj: User): Boolean {
        //Hace una petición para borrar el usuario con el email pedido
        var response = apiService.eraseUser(obj.email)
        //Guarda si la petición es correcta
        var resp = response.isSuccessful
        //Devuelve esa booleana
        return resp
    }
    //Función de Obtención
    override suspend fun getItem(value: String): User? {
        //Se crea un usuario nulo
        var user: User? = null
        //Se hace una petición API para obteneer los usuarios
        var response = apiService.getUser(value)
        //Si la petición es correcta, se guarda el usuario obtenido
        if (response.isSuccessful) {
            user = response.body()
        }
        //Se devuelve el usuario con datos si la respuesta es correcta y, si no lo es, null.
        return user
    }
    //Función de Listado
    override suspend fun getList(): List<User> {
        //Crea una lista vacía
        var list: List<User> = mutableListOf()
        //Hace una petición para obtener todos los usuarios
        var response = apiService.getAllUsers()
        //Si la respuesta es correcta, introduce la respuesta en la lista
        if (response.isSuccessful) {
            list = response.body()!!
        }
        //Devuelve la lista con o sin datos
        return list
    }
    //Función de obtención de usuarios de un grupo.
    suspend fun getUsersFromGroup(group: Group): List<User> {
        //Crea una lista vacía
        var list: List<User> = mutableListOf()
        //Hace una petición para obtener todos los usuarios con el id del grupo
        var response = apiService.getUsersFromGroup(group.id!!)
        //Si la respuesta es correcta, introduce la respuesta en la lista
        if (response.isSuccessful) {
            list = response.body()!!
        }
        //Devuelve la lista con o sin datos
        return list
    }
    //Función de comprobación de si un usuario es o no administrador.
    suspend fun checkIfThisUserIsAdmin(mainUser: User, selectedGroup: Group): Boolean {
        //Crea petición que devuelve si el usuario es administrador o no
        var response = apiService.getIfUserIsAdmin(mainUser.email, selectedGroup.id);
        return response.body()?:false; //En caso de no tener respuesta, devuelve false
    }
    //Función de obtención de usuarios de un pago
    suspend fun getPayersfromPayment(id:Int): List<Payer> {
        //Se crea una lista DTO de pagadores
        var list:List<PayerDTO> = mutableListOf()
        //Se hace una petición API para obtener los pagadores de un pago específico
        var response = apiService.getUsersFromPayment(id)
        //Si la respuesta es correcta, añade los usuarios a la lista vacía
        if (response.isSuccessful){
            list = response.body()!!
        }
        //Devuelve la lista mapeada de PayerDTO a Payer
        return list.map {
            Payer(getItem(it.user_email)!!, it.quantity, it.paid)
        }
    }
    //Función de Obtención de usuarios administradores d un grupo
    suspend fun getListOfAdmins(g:Group): List<User> {
        //Se saca la lista de usuarios
        var groupList: List<User> = getUsersFromGroup(g)

        groupList = groupList.filter { //Se filtra con el método de chequeo si es administrador
            checkIfThisUserIsAdmin(it, g)
        }

        //Devuelve la lista, vacía o con datos
        return groupList
    }
}