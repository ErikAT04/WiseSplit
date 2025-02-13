package androids.erikat.wisesplit.DTO

import java.time.LocalDateTime

//Clase de Transferencia de Datos de Payment
data class PaymentDTO(var payment_id:Int?, var payer_email:String, var group_id:Int, var payment_date:String, var payment_args:String, var total_payment:Float)