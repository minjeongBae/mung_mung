package com.android.mungmung

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.mungmung.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        finish()
                    }
                }
            }
        }


        binding.btnGoogleLogin.setOnClickListener{
            val signInIntent : Intent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }


    }
}