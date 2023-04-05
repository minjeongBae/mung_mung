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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var task : Task<GoogleSignInAccount>
    private lateinit var name : String
    private lateinit var pet : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id)).build()
        mGoogleSignInClient = GoogleSignIn.getClient(binding.root.context,gso)
        mGoogleSignInClient.signOut()


        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == Activity.RESULT_OK){
                task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.d("user's email : ",task.result.email.toString())
                if(task.isSuccessful){
                    val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)
                    FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("email", task.result.email)
                        .get().addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.documents.isNotEmpty()){
                                Log.d("doc", querySnapshot.documents[0].data.toString())
                            }
                            else{
                                Log.d("No such document"," ")
                                FirebaseFirestore.getInstance()
                                    .collection("users").add(
                                        hashMapOf(
                                            "email" to task.result.email,
                                            "following" to 0,
                                            "followers" to 0,
                                            "name" to name,
                                            "pet_name" to pet,
                                            "posts" to 0
                                        ))}}
                    auth.signInWithCredential(credential).addOnCompleteListener{
                        finish()
                    }
                }
            }
        }

        binding.btnGoogleLogIn.setOnClickListener{
            name = binding.signupName.text.toString()
            pet = binding.signupPet.text.toString()
            Log.d(name,pet)

            if(name!="" && pet!=""){
                val signInIntent : Intent = mGoogleSignInClient.signInIntent
                resultLauncher.launch(signInIntent)
            }
            else{
                Snackbar.make(binding.root, "정보를 모두 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}