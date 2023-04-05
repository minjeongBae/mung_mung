package com.android.mungmung.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.android.mungmung.R
import com.android.mungmung.data.UserData
import com.android.mungmung.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        var user = UserData("user","pet",0,0,0)

        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", currentUser?.email.toString()).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    user.name = document.getString("name").toString()
                    user.pet = document.getString("pet_name").toString()
                    user.following = Integer.parseInt(document.getLong("following").toString())
                    user.followers = Integer.parseInt(document.getLong("followers").toString())
                    user.posts = Integer.parseInt(document.getLong("posts").toString())
                }

                binding.profileTextPostsNum.text = user.posts.toString()
                binding.profileUserName.text = user.name
                binding.profilePetName.text = user.pet
                binding.profileTextFollowsNum.text = user.following.toString()
                binding.profileTextFollowersNum.text = user.followers.toString()
            }


    }
}