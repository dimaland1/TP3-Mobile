package com.example.tp3_mobile

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tp3_mobile.databinding.FragmentAffichageBinding
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager

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
    private var jsonContent: String = ""

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAffichageBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun checkPermissionsAndSaveData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            saveData()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveData()
        } else {
            Toast.makeText(context, "Permission refusée, impossible de sauvegarder les données.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.signupFormData.observe(viewLifecycleOwner) { formData ->

            val interests = formData.interests
            if (interests != null) {
                val text = interests.joinToString(", ")
                binding.interestsTextView.text = text
            } else {
                binding.interestsTextView.text = "Aucun"
            }

            binding.nameTextView.text = formData.name
            binding.firstNameTextView.text = formData.firstName
            binding.DateTextView.text = formData.birthDate
            binding.EmailTextView.text = formData.email
            binding.NumberTextView.text = formData.phoneNumber
            Data = formData
        }

        //bouton de retour à la page de saisie
        binding.retourButton.setOnClickListener {
            findNavController().navigate(R.id.action_affichageFragment_to_saisieFragment)
        }

        binding.validateButton.setOnClickListener {
            checkPermissionsAndSaveData()
        }
    }

    private fun saveData() {
        jsonContent = Gson().toJson(Data)
        val fileName = "${Data?.name ?: "inconnue"}_Data.json"

        val createFileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(createFileIntent, WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.let { context ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonContent.toByteArray())
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}