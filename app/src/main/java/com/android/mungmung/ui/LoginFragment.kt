package com.android.mungmung.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id)).build()
        mGoogleSignInClient = GoogleSignIn.getClient(binding.root.context,gso)
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == Activity.RESULT_OK){
                val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.d("user's email : ",task.result.email.toString())

                if(task.isSuccessful){
                    val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener{
                        Log.d("user: ",auth.currentUser?.email.toString())

                    }
                }
            }
        }

        binding.btnGoogleLogin.setOnClickListener{
            val signInIntent : Intent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }

        binding.txtSignUp.setOnClickListener {
            Log.d("signupText clicked","OK")
            view.findNavController().navigate(R.id.signupFragment)
        }

        binding.btnLogin.setOnClickListener {
            Log.d("signInBtn clicked","OK")
        }

        binding.txtFindPassword.setOnClickListener{
            signOut()
            view.findNavController().navigate(R.id.newPwFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut(){
        auth = FirebaseAuth.getInstance()
    }
}