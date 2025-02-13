package androids.erikat.wisesplit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androids.erikat.wisesplit.Model.Notification
import androids.erikat.wisesplit.Model.Payer
import androids.erikat.wisesplit.Model.Payment
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.databinding.NotificacionReciclableBinding
import androids.erikat.wisesplit.databinding.PagoReciclableBinding
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
//Adapter de pagos
class PaymentsAdapter(var listaPagos:MutableList<Payment>, var funcionPagar:(Payment)->Unit) : RecyclerView.Adapter<PaymentsAdapter.PaymentHolder>() {
    //Holder de Pagos
    class PaymentHolder(var v:View) : ViewHolder(v) {
        //ViewBinding
        lateinit var binding: PagoReciclableBinding
        //Función de carga de datos en el holder:
        fun bind(pago:Payment, funcionPagar:()->Unit){
            /*
            Pueden pasar varias cosas con los pagos:
                1. Eres el que lo ha pagado (apareces en la relación 1:N)
                2. No eres el que lo ha pagado
                    2.1. Participas en el pago (apareces en la relación N:N)
                        2.1.1. Ya lo has pagado (En la N:N aparece que ya está pagado)
                        2.1.2. Tienes que pagar (En la N:N aparece que no está pagado)
                    2.2. No participas en el pago (no apareces en la relación N:N)
             */
            //Esta primera booleana busca si el el usuario aparece en la lista de pagadores
            var participaEnPago = (APIUtils.mainUser!!.email in pago.listaPagadores.map { it.user.email })
            var usuarioPagador:Payer? = null //Se inicia una instancia de Payer, que guardará el usuario actual
            if (participaEnPago){
                //Si ha participado, se recorre la lista y se guarda en la instancia de Payer el que tenga el email de nuestra cuenta actual
                pago.listaPagadores.forEach {
                    if (it.user.email == APIUtils.mainUser!!.email){
                        usuarioPagador = it
                    }
                }
            }
            //Se carga el binding
            binding = PagoReciclableBinding.bind(v)
            //El botón de pagar sólo aparecerá si el usuario aparece en la lista de pagos y si el usuario no ha pagado ya
            binding.pagarBtt.isEnabled = participaEnPago && !usuarioPagador!!.hasPaid
            binding.pagarBtt.isVisible = participaEnPago && !usuarioPagador!!.hasPaid
            //Se carga el nombre del pago
            binding.nombrePagoTv.text = pago.arg
            //Se carga el nombre del que realizó el pago
            binding.nombrePagadorTV.text = pago.payer.username
            //En el texto grande aparecerá la cantidda a pagar del usuario si participa en el pago o 0€ si no participa
            binding.cantidadAPagarTv.text = if (participaEnPago) "${usuarioPagador!!.quantity}€" else "0€"
            //Este texto guarda si el usuario tiene que pagar, ha pagado, lo ha creado o si no participa
            val texto =
            if (participaEnPago){
                if(usuarioPagador!!.hasPaid){ //Si participa y ha pagado:
                    "Ya has pagado"
                } else { //Si participa y no ha pagado
                    "Tienes que pagar"
                }
            } else if (pago.payer.email == APIUtils.mainUser!!.email){ //Si es el que ha creado el pago
                "Has realizado tú este pago"
            } else { //Si no participa en el pago
                "No participas en este pago"
            }
            //Se guarda el texto previo
            binding.hayQuePagarTv.text = texto
            //Se pone en otro texto la cantidad original del pago
            binding.cantidadInicialTView.text = "${pago.quantity}€ repartidos"
            binding.pagarBtt.setOnClickListener { //Si se pulsa al botón de pagar, se realiza la función pasada por parámetro
                funcionPagar()
            }
        }
    }
    //Función de creación del ViewHolder: Se crea una vista y devuelve un holder con esa vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.pago_reciclable, parent, false)

        return PaymentHolder(view)
    }
    //Función de cuenta de objetos: Devuelve el tamaño de la lista
    override fun getItemCount(): Int {
        return listaPagos.size
    }
    //Función de carga de datos: Carga en el holder el pago en función de la posición y una función pasada por el constructor en función del pago
    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        holder.bind(listaPagos[position]){
            funcionPagar(listaPagos[position])
        }
    }
    //Función de cambio de lista: Actualiza el holder con una nueva lista.
    fun cambiarLista(lista:MutableList<Payment>){
        listaPagos = lista
        notifyDataSetChanged()
    }
}