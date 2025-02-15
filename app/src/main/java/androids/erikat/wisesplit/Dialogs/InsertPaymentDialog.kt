package androids.erikat.wisesplit.Dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androids.erikat.wisesplit.DAO.PaymentDAO
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Model.Payer
import androids.erikat.wisesplit.Model.Payment
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.adapters.UsersGroupAdapter
import androids.erikat.wisesplit.databinding.AlertCrearPagoBinding
import androids.erikat.wisesplit.fragments.FragmentGroupMain
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.round
//Diálogo de Inserción de Pagos
class InsertPaymentDialog(var funcion: () -> Unit) : DialogFragment() {
    lateinit var binding: AlertCrearPagoBinding
    //Lista de usuarios
    var listaUsuarios:MutableList<User> = mutableListOf()
    lateinit var adapter:UsersGroupAdapter

    lateinit var pagoAEditar:Payment

    var editando = false //Booleana que comprueba si se está editando un pago existente o no
    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            var builder = AlertDialog.Builder(requireActivity())
            //Infla la vista para el builder y le asigna un ViewBinding
            var view = layoutInflater.inflate(R.layout.alert_crear_pago, null)
            binding = AlertCrearPagoBinding.bind(view)

            binding.usuariosElegirRView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            binding.usuariosElegirRView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
            //El adaptador de este caso es para seleccionar, por lo que no tiene funciones de promoción ni borrado
            adapter = UsersGroupAdapter(listaUsuarios, true, {}, {})
            //Vinculo el adapter
            binding.usuariosElegirRView.adapter = adapter
            //Si se está editando, se añade la lista de elementos del pago a editar, asi como sus datos
            if (editando) {
                FragmentGroupMain.listaSeleccionados.addAll(pagoAEditar.listaPagadores.map { it.user })
                binding.nombrePagoEText.setText(pagoAEditar.arg)
                binding.cantidadEText.setText(pagoAEditar.quantity.toString())
            }
            //Carga de la lista de usuarios
            cargarListaUsuarios()
            binding.crearPagoBtt.setOnClickListener {
                if(FragmentGroupMain.listaSeleccionados.isEmpty()){ //Si no hay usuarios seleccionados
                    Toast.makeText(view.context, "Elige al menos un usuario", Toast.LENGTH_SHORT).show()
                } else {
                    if (binding.cantidadEText.text.isEmpty() || binding.nombrePagoEText.text.isEmpty()){ //Si algun campo está vacío
                        binding.cantidadEText.error = if(binding.cantidadEText.text.isEmpty()) "El campo no puede estar vacío" else null
                        binding.nombrePagoEText.error = if(binding.nombrePagoEText.text.isEmpty()) "El campo no puede estar vacío" else null
                    } else {
                        lifecycleScope.launch {
                            //Se calcula la cantidad a pagar de cada usuario:
                            //Se coge la cantidad total y se divide entre los usuarios a pagar + 1 (Porque el creador cuenta)
                            var cantidad_por_usuario = binding.cantidadEText.text.toString().toDouble()
                            cantidad_por_usuario = cantidad_por_usuario/(FragmentGroupMain.listaSeleccionados.size+1)
                            if(editando) {
                                var payment = Payment(id = pagoAEditar.id,
                                    binding.nombrePagoEText.text.toString(),
                                    APIUtils.mainUser!!,
                                    pagoAEditar.date,
                                    binding.cantidadEText.text.toString().toDouble(),
                                    APIUtils.selectedGroup!!,
                                    FragmentGroupMain.listaSeleccionados.map {
                                        Payer(it, (String.format("%.2f", cantidad_por_usuario).toDouble()), false)
                                    }.toMutableList())
                                PaymentDAO().update(payment)
                            }else {
                                //Se redondea y se crea una lista de Payers, la cual se introduce en el pago
                                var payment = Payment(
                                    -1,
                                    binding.nombrePagoEText.text.toString(),
                                    APIUtils.mainUser!!,
                                    LocalDate.now(),
                                    binding.cantidadEText.text.toString().toDouble(),
                                    APIUtils.selectedGroup!!,
                                    FragmentGroupMain.listaSeleccionados.map {
                                        Payer(it, (String.format("%.2f", cantidad_por_usuario).toDouble()), false)
                                    }.toMutableList())
                                //Se insertan y se cierra el dialogo, además de hacer una función pasada por parámetro
                                PaymentDAO().insert(payment)
                            }
                            //Se borran todos los usuarios de la lista de seleccionados
                            FragmentGroupMain.listaSeleccionados.removeAll(FragmentGroupMain.listaSeleccionados)
                            dismiss()
                            funcion()
                        }
                    }
                }
            }
            builder.setView(view) //Se inserta la lista en el builder
            builder.create() //Se devuelve la creación de la lista
        }
    }

    //Función de carga de lista de usuarios
    private fun cargarListaUsuarios() {
        lifecycleScope.launch {
            //Obtiene los usuarios de la API en función del grupo actual
            listaUsuarios = UserDAO().getUsersFromGroup(APIUtils.selectedGroup!!).toMutableList()
            listaUsuarios.removeIf { //Elimina al usuario cuyo email coincida con el nuestro
                it.email == APIUtils.mainUser!!.email
            }
            adapter.cambiarLista(listaUsuarios) //Actualiza el adapter
        }
    }
    //Función de carga de pago para editarlo:
    fun cargarPagoAEditar(p:Payment){
        editando = true
        pagoAEditar = p
    }
}