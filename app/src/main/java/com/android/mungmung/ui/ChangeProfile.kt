package com.android.mungmung.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentChangeProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class ChangeProfile : Fragment(R.layout.fragment_change_profile) {

    private lateinit var binding: FragmentChangeProfileBinding
    private lateinit var auth: FirebaseAuth

    private var selectedUri: Uri? = null
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            selectedUri = uri
            binding.changeProfileImageview.setImageURI(selectedUri)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangeProfileBinding.bind(view)
        auth = Firebase.auth

        binding.editProfileFinishBtn.setOnClickListener {
            val name = binding.editProfileNameEdit
            val pet = binding.editProfilePetEdit
            val userdb = Firebase.firestore.collection("users")
            userdb.whereEqualTo("email", auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents){
                        val map = mutableMapOf<String,String>()
                        if(name.text.toString()!="") {
                            map.put("name", name.text.toString())
                        }
                        if(pet.text.toString()!="") {
                            map.put("pet_name", pet.text.toString())
                        }
                        document.reference.update(map as Map<String, Any>)
                        val action = ChangeProfileDirections.actionChangeProfileToProfileFragment()
                        findNavController().navigate(action)
                    }}


            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener

                uploadStorage(
                    uri = photoUri,
                    successHandler = {
                        // Fire store 에 데이터 업로드
                        uploadFireStore(it)
                    },
                    errorHandler = {
                        Snackbar.make(view, "다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
                        Log.e("ERROR", it.toString())
                    }
                )
            }
        }

        binding.changeProfileImageview.setOnClickListener {
            startPicker()
        }
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadStorage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ){
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("users").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Firebase.storage.reference.child("users").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener {
                            successHandler(it.toString())
                        }.addOnFailureListener {
                            errorHandler(it)
                        }
                } else {
                    errorHandler(task.exception)
                }
            }

    }

    private fun uploadFireStore(photoUrl: String) {
        val userdb = Firebase.firestore.collection("users")
        userdb.whereEqualTo("email", auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents){
                    document.reference.update("profile_image_url", photoUrl)
                        .addOnSuccessListener {
                            Log.d("UPLOAD PROFILE IMAGE","SUCCESS")
                        }
                }}
    }
}