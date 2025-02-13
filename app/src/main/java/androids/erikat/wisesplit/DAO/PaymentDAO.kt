package androids.erikat.wisesplit.DAO

import android.os.Build
import androids.erikat.wisesplit.DTO.GroupDTO
import androids.erikat.wisesplit.DTO.PayerDTO
import androids.erikat.wisesplit.DTO.PaymentDTO
import androids.erikat.wisesplit.Model.Group
import androids.erikat.wisesplit.Model.Payment
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.Utils.APIUtils.Companion.retrofit
import androids.erikat.wisesplit.services.GroupService
import androids.erikat.wisesplit.services.PaymentService
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
//Clase de Acceso a Datos de Pagos
class PaymentDAO : DAO<Int, Payment> {
    //Servicio API de Pagos
    val apiService = retrofit.create(PaymentService::class.java)
    //Función de Inserción de Pago
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insert(obj: Payment): Boolean {
        //Se crea un objeto DTO con los datos a cargar
        var dto: PaymentDTO = PaymentDTO(
            -1,
            obj.payer.email,
            obj.group.id!!,
            obj.date.toString(),
            obj.arg,
            obj.quantity.toFloat()
        )
        //Se crea una petición de inserción de pago en la API
        var response = apiService.insertPayment(dto)
        //Se guarda si se ha hecho correctamente
        var resp = response.isSuccessful
        //Si se ha hecho correctamente, se procede a guardar todos los pagadores de esa respuesta
        if (resp){
            //El id del último pago realizado (No está preparado contra posibles problemas de concurrencia de datos)
            var id = PaymentDAO().getList().last().id!!
            for (payer in obj.listaPagadores){
                //Se inserta un usuario en el pago con la cantidad de cada uno, si ha pagado ya (normalmente es falso de primeras), el id del pago y el email del usuario
                apiService.insertUserInPayment(PayerDTO(payer.user.email, id, payer.quantity, payer.hasPaid))
            }
        }
        //Se devuelve la respuesta
        return resp
    }
    //Función de Actualización (Actualmente no se utiliza)
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun update(obj: Payment): Boolean {
        //Se crea una clase DTO con los datos del pago
        var dto: PaymentDTO = PaymentDTO(
            obj.id,
            obj.payer.email,
            obj.group.id!!,
            obj.date.toString(),
            obj.arg,
            obj.quantity.toFloat()
        )
        //Se hace una petición API para actualizar el DTO con el id del pago
        var response = apiService.updatePayment(obj.id!!, dto)
        //Se guarda si se ha realizado correctamente
        var resp = response.isSuccessful
        //Se devuelve la booleana
        return resp
    }
    //Función de Eliminación (Actualmente no se utiliza)
    override suspend fun delete(obj: Payment): Boolean {
        //Se hace una petición API para borrar el pago en función de su ID
        var response = apiService.erasePayment(obj.id!!)
        //Se guarda si se ha realizado correctamente
        var resp = response.isSuccessful
        //Se devuelve la booleana
        return resp
    }
    //Función de obtención
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getItem(value: Int): Payment? {
        //Se crea un objeto DTO nulo
        var payment: PaymentDTO? = null
        //Se hace una petición para obtener el pago con ese ID
        var response = apiService.getPayment(value)
        //Si la respuesta es correcta, se devuelve un pago con esos datos obtenidos
        if (response.isSuccessful) {
            payment = response.body()!!
            return Payment(payment.payment_id, payment.payment_args, UserDAO().getItem(payment.payer_email)!!, LocalDate.parse(payment.payment_date), payment.total_payment.toDouble(), GroupDAO().getItem(payment.group_id)!!, mutableListOf())
        }
        //Si no es correcta, se devuelve null
        return null
    }
    //Función de Listado
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getList(): List<Payment> {
        //Se crea una lista DTO vacía
        var payments: List<PaymentDTO> = mutableListOf()
        //Se hace una petición para obtener todos los pagos
        var response = apiService.getAllPayments()
        //Si la respuesta es correcta, introduce en la lista todos los pagos encontrados
        if (response.isSuccessful) {
            payments = response.body()!!
        }
        //Devuelve la lista mapeada de DTO a Pago
        return payments.map {
            Payment(it.payment_id, it.payment_args, UserDAO().getItem(it.payer_email)!!, LocalDate.parse(it.payment_date), it.total_payment.toDouble(), GroupDAO().getItem(it.group_id)!!,UserDAO().getPayersfromPayment(it.payment_id!!))
        }
    }
    //Función de Obtención de Pagos de un grupo
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPaymentsByGroup(g: Group): List<Payment> {
        //Se crea una lista DTO vacía
        var payments: List<PaymentDTO> = mutableListOf()
        //Se hace una petición para obtener los pagos con el ID del grupo
        var response = apiService.getPaymentsFromGroup(g.id!!)
        //La respuesta es correcta, guarda en la lista todos los pagos encontrados
        if (response.isSuccessful) {
            payments = response.body()!!
        }
        //Devuelve la lista mapeada de DTO a Pago
        return payments.map {
            Payment(it.payment_id, it.payment_args, UserDAO().getItem(it.payer_email)!!, LocalDate.parse(it.payment_date), it.total_payment.toDouble(), GroupDAO().getItem(it.group_id)!!, UserDAO().getPayersfromPayment(it.payment_id!!))
        }
    }
    //Función para obtener la deuda del usuario (Lo que él debe al grupo)
    suspend fun getDebt(u: User, g: Group): Double {
        //Carga una respuesta con valor 0
        var res: Double = 0.0
        //Hace una petición API para obtener la deuda del usuario en el grupo
        var response = apiService.getUserDebt(g.id!!, u.email)
        //Si la respuesta es correcta y es distinto a "Error", parsea la respuesta a la cantidad
        if (response.isSuccessful) {
            if (response.body()!! != "Error") {
                res = response.body()!!.toDouble()
            }
        }
        //Devuelve la cantidad calculada
        return res
    }
    //Función de pago
    suspend fun pay(email: String, id: Int) {
        //Hace una llamada a la API para que el usuario (su email) realice un pago (con su ID)
        apiService.payUser(email, id)
    }
    //Función para obtener las deudas hacia el usuario (Lo que le deben los demás)
    suspend fun getWhatTheyOweMe(email: String, id: Int): Double {
        //Carga una respuesta con valor 0
        var res: Double = 0.0
        //Hace una petición API que calcula lo que los demás le deben
        var response = apiService.getMyPayments(email, id)
        //Si la respuesta es correcta, guarda esa cantidad
        if (response.isSuccessful){
            res = response.body()!!
        }
        //Devuelve la respuesta
        return res
    }
}