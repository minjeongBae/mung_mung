package com.android.mungmung

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.android.mungmung.databinding.FragmentChangeProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangeProfile : Fragment(R.layout.fragment_change_profile) {

    private lateinit var binding: FragmentChangeProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangeProfileBinding.bind(view)
        auth = Firebase.auth


    }


}