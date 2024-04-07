package com.example.tp3_mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.tp3_mobile.databinding.FragmentAffichageBinding
import com.google.gson.Gson
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AffichageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class AffichageFragment : Fragment() {
    private var _binding: FragmentAffichageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaisieModel by activityViewModels()
    private var Data: SignupFormData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAffichageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.signupFormData.observe(viewLifecycleOwner) { formData ->

            val interests = formData.interests
            if(interests != null){
                val text = interests.joinToString(", ")
                binding.interestsTextView.text = text
            }else{
                binding.interestsTextView.text = "Aucun"
            }

            binding.nameTextView.text = formData.name
            binding.firstNameTextView.text = formData.firstName
            binding.DateTextView.text = formData.birthDate
            binding.EmailTextView.text = formData.email
            binding.NumberTextView.text = formData.phoneNumber
            Data = formData
        }

        binding.validateButton.setOnClickListener {
            // Logique de validation ou navigation
            saveData()
        }
    }

    private fun saveData(){
        try {
            val jsonFile = Gson().toJson(Data)
            context?.let {
                val file = File(it.filesDir, (Data?.name ?: "inconnue") + "_Data.json")
                file.writeText(jsonFile)
            }

            Toast.makeText(context, "Données sauvegardées", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}