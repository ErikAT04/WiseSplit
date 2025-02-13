package androids.erikat.wisesplit.Model

import java.time.LocalDate

//Notificación de Invitación a Grupo
class Notification(var id:Int?, //ID de invitacion
                   var group:Group, //Grupo al que ha sido invitado
                   var userDest:User, //Usuario al que han invitado
                   var date:LocalDate //Fecha de notificación
) {
}