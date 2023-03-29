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
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentAddArticleBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class AddArticleFragment: Fragment(R.layout.fragment_add_article) {

    private lateinit var binding: FragmentAddArticleBinding
    private lateinit var auth: FirebaseAuth
    private var selectedUri: Uri? = null

    // todo 여러 장 업로드
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            selectedUri = uri
            binding.photoImageView.setImageURI(selectedUri)
            binding.clearImageView.isVisible = false
            binding.addImageView.isVisible = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddArticleBinding.bind(view)
        auth = Firebase.auth


        startPicker()
        setupPhotoImageView()
        setupClearImageView()
        setupBackImageButton()
        setupSubmitButton(view)

    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupPhotoImageView() {
        binding.addImageView.setOnClickListener {
            if (selectedUri == null) {
                startPicker()
            }
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

    private fun setupBackImageButton() {
        binding.backImageButton.setOnClickListener {
            findNavController().navigate(AddArticleFragmentDirections.actionBack())
        }
    }

    private fun setupSubmitButton(view: View) {
        binding.addButton.setOnClickListener {
            showProgress()

            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadImage(
                    uri = photoUri,
                    successHandler = {
                        // Fire store 에 데이터 업로드
                        uploadArticle(it, binding.descriptionEditText.text.toString())
                    },
                    errorHandler = {
                        Snackbar.make(view, "이미지 업로드에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                        Log.e("ERROR", it.toString())
                        hideProgress()
                    }
                )

            } else {
                Snackbar.make(view, "이미지가 선택되지 않았습니다", Snackbar.LENGTH_SHORT).show()
                hideProgress()
            }
        }
    }



    private fun uploadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("articles").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Firebase.storage.reference.child("articles").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener {
                            successHandler(it.toString())

                        }.addOnFailureListener {
                            errorHandler(it)
                        }

                } else {
                    errorHandler(task.exception)
                }
            }
    }


    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }

    private fun uploadArticle(photoUrl: String, description: String) {
        val articleId = UUID.randomUUID().toString()
        val articleModel = ArticleModel(
            articleId = articleId,
            userId = auth.currentUser?.email.toString(),
            createdAt = System.currentTimeMillis(),
            description = description,
            imageUrl = photoUrl
        )

        Firebase.firestore.collection("articles").document(articleId)
            .set(articleModel)
            .addOnSuccessListener {
                findNavController().navigate(AddArticleFragmentDirections.actionBack())

                view?.let { view ->
                    Snackbar.make(view, "글 작성에 성공했습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                view?.let { view ->
                    Snackbar.make(view, "글 작성에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                }
                hideProgress()
            }

        hideProgress()

    }


}