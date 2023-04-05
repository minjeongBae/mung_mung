package com.android.mungmung.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)
        auth = Firebase.auth


    }
}