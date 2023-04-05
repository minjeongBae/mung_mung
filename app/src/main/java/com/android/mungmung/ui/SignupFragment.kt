package com.android.mungmung.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentSignupBinding

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)

    }
}