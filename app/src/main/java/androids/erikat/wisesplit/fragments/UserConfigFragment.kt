package androids.erikat.wisesplit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androids.erikat.wisesplit.DAO.UserDAO
import androids.erikat.wisesplit.Dialogs.ChangeNameDialog
import androids.erikat.wisesplit.Dialogs.ChangePasswordDialog
import androids.erikat.wisesplit.R
import androids.erikat.wisesplit.Utils.APIUtils
import androids.erikat.wisesplit.Utils.cargarImagen
import androids.erikat.wisesplit.databinding.FragmentUserSettingsBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UserConfigFragment(var funcionActionBar:(Toolbar, String)->Unit):Fragment() {

    lateinit var binding:FragmentUserSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Se cargan la vista y el binding
        var view = inflater.inflate(R.layout.fragment_user_settings, container, false)
        binding = FragmentUserSettingsBinding.bind(view)

        binding.changeNameLayout.setOnClickListener {
            ChangeNameDialog(){
                binding.userNameTv.text = APIUtils.mainUser!!.username
            }.show(requireActivity().supportFragmentManager, "Cambio de Nombre")
        }

        binding.changePasswordLayout.setOnClickListener {
            ChangePasswordDialog().show(requireActivity().supportFragmentManager, "Cambio de Contraseña")
        }

        binding.logOutLayout.setOnClickListener {
            requireActivity().finish()
        }

        binding.eraseLayout.setOnClickListener {
            AlertDialog.Builder(view.context)
                .setMessage("¿Estás seguro de que te quieres salir del grupo? No podrás volver atrás")
                .setPositiveButton("Si") { _, _ ->
                    lifecycleScope.launch {
                        UserDAO().delete(APIUtils.mainUser!!)
                        Toast.makeText(
                            view.context,
                            "Cuenta eliminada. ¡Vuelva pronto!",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
                }.setNegativeButton("No") { _, _ -> }.show()
        }

        binding.userImageView.setOnClickListener {
            var builder = AlertDialog.Builder(view.context)

            var tfield = EditText(view.context)
            tfield.hint = "Escribe una URL válida"
            tfield.setText(APIUtils.mainUser!!.image)

            builder.setView(tfield)

            builder.setPositiveButton("Enviar"){dialog, _ ->
                lifecycleScope.launch {
                    APIUtils.mainUser!!.image = tfield.text.toString()
                    UserDAO().update(APIUtils.mainUser!!)
                    binding.userImageView.cargarImagen(APIUtils.mainUser!!.image)
                }
            }

            builder.create().show()
        }

        binding.userImageView.cargarImagen(APIUtils.mainUser!!.image)
        binding.userNameTv.text = APIUtils.mainUser!!.username
        binding.userEmailTv.text = APIUtils.mainUser!!.email
        funcionActionBar(binding.userConfigToolbar, "Ajustes")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}