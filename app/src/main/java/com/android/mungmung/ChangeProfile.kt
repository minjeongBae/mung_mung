package com.android.mungmung

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.mungmung.databinding.FragmentChangeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChangeProfile : Fragment(R.layout.fragment_change_profile) {

    private lateinit var binding: FragmentChangeProfileBinding
    private lateinit var auth: FirebaseAuth

    private val userName = arguments?.getString("user_name")
    private val petName = arguments?.getString("pet_name")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangeProfileBinding.bind(view)
        auth = Firebase.auth

        val name = binding.editProfileNameEdit
        val pet = binding.editProfileNameEdit

        name.setText(userName)
        pet.setText(petName)

        binding.editProfileFinishBtn.setOnClickListener {


            val userdb = Firebase.firestore.collection("users")
            userdb.whereEqualTo("email", auth.currentUser?.email.toString()).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents){
                        document.reference.set(
                            mapOf("name" to name.text,
                                "pet_name" to pet.text))
                    }}


            val action = ChangeProfileDirections.actionChangeProfileToProfileFragment()
            findNavController().navigate(action)
        }

    }


}