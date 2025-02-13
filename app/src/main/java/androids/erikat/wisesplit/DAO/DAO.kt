package androids.erikat.wisesplit.DAO

//INTERFAZ DE CLASES DE ACCESO A DATOS
//Ofrece las funciones CRUD principales de todas las DAO: Insercion, actualización, borrado, obtención y listado
//Las funciones son SUSPEND porque trabajan de forma asíncrona, se activan con LifeCycleScope
//K es el tipo genérico de la clave primaria de la clase a conseguir
//T es el tipo genérico de la clase a obtener o utilizar.
interface DAO<K, T> {
    suspend fun insert(obj:T):Boolean
    suspend fun update(obj:T):Boolean
    suspend fun delete(obj:T):Boolean
    suspend fun getItem(value:K):T?
    suspend fun getList():List<T>
}