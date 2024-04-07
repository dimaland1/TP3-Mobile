package com.example.tp3_mobile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SaisieModel : ViewModel() {
    private val _signupFormData = MutableLiveData<SignupFormData>()
    val signupFormData: LiveData<SignupFormData> = _signupFormData

    fun submitFormData(formData: SignupFormData) {
        _signupFormData.value = formData
    }
}

data class SignupFormData(
    val name: String,
    val firstName: String,
    val birthDate: String,
    val phoneNumber: String,
    val email: String,
    val interests: List<String>,
    val sync: Boolean
)