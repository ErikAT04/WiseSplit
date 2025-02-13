package androids.erikat.wisesplit.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androids.erikat.wisesplit.DAO.PaymentDAO
import androids.erikat.wisesplit.Dialogs.InsertPaymentDialog
import androids.erikat.wisesplit.Model.Payment
import androids.erikat.wisesplit.Model.User
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.adapters.PaymentsAdapter
import androids.erikat.wisesplit.databinding.FragmentGroupMainBinding
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

//Fragmento principal de la actividad de grupos
class FragmentGroupMain(var funcionCambioToolbar: (Toolbar, String) -> Unit) : Fragment() {
    //Binding
    lateinit var binding: FragmentGroupMainBinding
    //Lista de pagos del grupo
    var listaPagos: MutableList<Payment> = mutableListOf()
    //Adapter
    lateinit var adapter: PaymentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla la vista y el binding
        var view = inflater.inflate(R.layout.fragment_group_main, container, false)
        binding = FragmentGroupMainBinding.bind(view)
        //Prepara el RecyclerView
        binding.pagosGrupoRView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, true) //En este caso es True porque interesa que los pagos más recientes salgan los primeros
        binding.pagosGrupoRView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        //Prepara el adapter
        adapter = PaymentsAdapter(listaPagos) { pago -> //Función del botón de pagar
            lifecycleScope.launch {
                PaymentDAO().pay(APIUtils.mainUser!!.email, pago.id!!) //El usuario actual realiza el pago del elemento seleccionado
                Toast.makeText(view?.context, "¡¡Pago realizado!!", Toast.LENGTH_SHORT).show() //Muestra el resultado
                cargarDatos() //Recarga los datos
            }
        }
        binding.pagosGrupoRView.adapter = adapter
        //Añade funcionalidad al FAB
        binding.addPagoFAB.setOnClickListener {
            InsertPaymentDialog { //Muestra el diálogo de inserción de pagos, al que le pasa la función de recarga de datos
                cargarDatos()
            }.show(requireActivity().supportFragmentManager, "Insertar Pago")
        }
        //Se cargan los datos
        cargarDatos()
        //Se llama a la función de cambio de toolbar
        funcionCambioToolbar(binding.grupoMainToolbar, APIUtils.selectedGroup!!.group_name)

        return view
    }
    //Carga de datos
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatos() {
        cargarLista() //Carga la lista de pagos
        cargarMisDeudas() //Carga lo que el usuario debe
        cargarLoQueMeDeben() //Carga lo que le deben al usuario
    }
    //Función de carga de lista
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarLista() {
        lifecycleScope.launch {
            //Se sacan todos los pagos del grupo
            listaPagos =
                PaymentDAO().getPaymentsByGroup(APIUtils.selectedGroup!!).toMutableList()
            adapter.cambiarLista(listaPagos) //Se actualiza el adapter
        }
    }
    //Función de carga de deudas
    private fun cargarMisDeudas() {
        lifecycleScope.launch {
            //Saca lo que el usuario debe en el grupo
            var totalDebo: Double =
                PaymentDAO().getDebt(APIUtils.mainUser!!, APIUtils.selectedGroup!!)
            binding.debesTView.text = "${totalDebo}€" //Lo muestra en el texto designado
        }
    }
    //Función de carga de deudas
    private fun cargarLoQueMeDeben() {
        lifecycleScope.launch {
            //Saca lo que los demás usuarios deben a éste
            var totalMeDeben: Double = PaymentDAO().getWhatTheyOweMe(
                APIUtils.mainUser!!.email,
                APIUtils.selectedGroup!!.id!!
            )
            binding.teDebenTview.text = "${totalMeDeben}€" //Lo muestra en el texto designado
        }
    }

    companion object {
        //Lista que guarda temporalmente los usuarios seleccionados
        var listaSeleccionados: MutableList<User> = mutableListOf()
    }
}