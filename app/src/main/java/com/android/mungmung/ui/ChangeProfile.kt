package com.android.mungmung.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentChangeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChangeProfile : Fragment(R.layout.fragment_change_profile) {

    private lateinit var binding: FragmentChangeProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangeProfileBinding.bind(view)
        auth = Firebase.auth

        var user_name=""
        var pet_name=""

        binding.editProfileFinishBtn.setOnClickListener {
            val name = binding.editProfileNameEdit
            val pet = binding.editProfilePetEdit
            val userdb = Firebase.firestore.collection("users")
            userdb.whereEqualTo("email", auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents){
                        val map = mutableMapOf<String,String>()
                        if(name.text.toString()!="") {
                            user_name = name.text.toString()
                            map.put("name", user_name)
                        }
                        if(pet.text.toString()!="") {
                            pet_name = pet.text.toString()
                            map.put("pet_name", pet_name)
                        }
                        document.reference.update(map as Map<String, Any>)
                        val action = ChangeProfileDirections.actionChangeProfileToProfileFragment(user_name,pet_name)
                        findNavController().navigate(action)
                    }}
        }

    }


}