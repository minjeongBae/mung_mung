package com.android.mungmung.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentAddArticleBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class AddArticleFragment: Fragment(R.layout.fragment_add_article) {

    private lateinit var binding: FragmentAddArticleBinding
    private var selectedUri: Uri? = null

    // todo 여러 장 업로드
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            selectedUri = uri
            binding.photoImageView.setImageURI(selectedUri)
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddArticleBinding.bind(view)


        startPicker()
        setupPhotoImageView()
        setupClearImageView()

        binding.addButton.setOnClickListener {

            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                val fileName = "${UUID.randomUUID()}.png"

                Firebase.storage.reference.child("articles").child(fileName)
                    .putFile(photoUri)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("upload image", "success")
                            Firebase.storage.reference.child("articles").child(fileName)
                                .downloadUrl
                                .addOnSuccessListener {
                                    Log.d("upload image", it.toString())

                                    // todo animation, .... +@
                                    findNavController().navigate(AddArticleFragmentDirections.actionBack())

                                }.addOnFailureListener {

                                }

                        } else {

                            task.exception?.printStackTrace()
                        }
                    }

            } else {
                Snackbar.make(view, "이미지가 선택되지 않았습니다", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backImageButton.setOnClickListener {
            findNavController().navigate(AddArticleFragmentDirections.actionBack())
        }






    }

    private fun setupClearImageView() {
        binding.clearImageView.setOnClickListener {
            binding.photoImageView.setImageURI(null)
            selectedUri = null
            binding.clearImageView.isVisible = false
            binding.addImageView.isVisible = true

        }
    }


    private fun setupPhotoImageView() {
        binding.addImageView.setOnClickListener {
            if (selectedUri == null) {
                startPicker()
            }
        }
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }



}