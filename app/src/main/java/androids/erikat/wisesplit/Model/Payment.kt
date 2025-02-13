package androids.erikat.wisesplit.Model

import java.time.LocalDate
//Pago
class Payment(var id:Int?, //ID del pago
              var arg:String, //Argumentos del Pago (Texto que explica qué se pagó)
              var payer:User, //Usuario que pagó (al que le deben x cantidad)
              var date:LocalDate, //Fecha del pago
              var quantity:Double, //Cantidad pagada
              var group:Group, //Grupo al que pertenece el pago
              var listaPagadores:List<Payer> //Lista de gente que tiene que pagar
)