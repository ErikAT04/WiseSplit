package androids.erikat.wisesplit.Model

//Pagador: Persona que tiene que hacer un pago. Nace de la relación N:N entre Usuario y Pago
class Payer(var user:User, //Usuario que tiene que pagar
            var quantity:Double, //Cantidad a pagar
            var hasPaid:Boolean //Indicador de si ya ha pagado
){
}