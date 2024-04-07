package com.example.tp3_mobile

import ParseJsonService
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.tp3_mobile.databinding.FragmentSaisieBinding
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import java.io.InputStreamReader

/**
 * A simple [Fragment] subclass.
 * Use the [SaisieFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SaisieFragment : Fragment() {

    private var _binding: FragmentSaisieBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaisieModel by activityViewModels()
    private val fileRequestCode = 1
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaisieBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateUI(data: SignupFormData) {
        binding.nameEditText.setText(data.name)
        binding.firstNameEditText.setText(data.firstName)
        binding.editTextDate.setText(data.birthDate)
        binding.editTextPhone.setText(data.phoneNumber)
        binding.editTextTextEmailAddress.setText(data.email)
        binding.sportCheckBox.isChecked = data.interests.contains(binding.sportCheckBox.text.toString())
        binding.musicCheckBox.isChecked = data.interests.contains(binding.musicCheckBox.text.toString())
        binding.lectureCheckBox.isChecked = data.interests.contains(binding.lectureCheckBox.text.toString())
        binding.syncSwitch.isChecked = data.sync
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.submitButton.setOnClickListener {
            var interests = mutableListOf<String>()
            if(binding.sportCheckBox.isChecked){
                // add the text of the checkbox to the list
                interests.add(binding.sportCheckBox.text.toString())
            }

            if(binding.musicCheckBox.isChecked){
                interests.add(binding.musicCheckBox.text.toString())
            }

            if(binding.lectureCheckBox.isChecked){
                interests.add(binding.lectureCheckBox.text.toString())
            }

            val formData = SignupFormData(
                name = binding.nameEditText.text.toString(),
                firstName = binding.firstNameEditText.text.toString(),
                birthDate = binding.editTextDate.text.toString(),
                phoneNumber = binding.editTextPhone.text.toString(),
                email = binding.editTextTextEmailAddress.text.toString(),
                interests = interests,
                sync = binding.syncSwitch.isChecked
            )
            viewModel.submitFormData(formData)
            val bundle = Bundle()
            bundle.putStringArrayList("interests", ArrayList(interests))
            findNavController().navigate(R.id.action_saisieFragment_to_affichageFragment, bundle)
        }

        binding.addButton.setOnClickListener {
            val chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            chooseFileIntent.type = "application/json"
            startActivityForResult(chooseFileIntent, fileRequestCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fileRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.let { context ->
                    parseJsonFileFromUri(uri, context)

                    //ParseJsonService.startActionParse(context, uri)

                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.example.tp3_mobile.ACTION_DATA_LOADED")
        if (!binding.syncSwitch.isChecked && !isDataLoaded) {
        }else{
            isDataLoaded = false
        }
    }

    fun parseJsonFileFromUri(fileUri: Uri, context: Context) {
        try {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream)
                val gson = Gson()
                val myDataType = gson.fromJson(reader, SignupFormData::class.java)

                binding.nameEditText.setText(myDataType.name)
                binding.firstNameEditText.setText(myDataType.firstName)
                binding.editTextDate.setText(myDataType.birthDate)
                binding.editTextPhone.setText(myDataType.phoneNumber)
                binding.editTextTextEmailAddress.setText(myDataType.email)
                binding.sportCheckBox.isChecked = myDataType.interests.contains(binding.sportCheckBox.text.toString())
                binding.musicCheckBox.isChecked = myDataType.interests.contains(binding.musicCheckBox.text.toString())
                binding.lectureCheckBox.isChecked = myDataType.interests.contains(binding.lectureCheckBox.text.toString())
                binding.syncSwitch.isChecked = myDataType.sync

                isDataLoaded = true

                //afficher le message de succès dans un toast
                Toast.makeText(context, "Fichier lu avec succès", Toast.LENGTH_SHORT).show()


            }
        } catch (e: Exception) {
            e.printStackTrace()
            //afficher le message d'erreur dans un toast
            Toast.makeText(context, "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
