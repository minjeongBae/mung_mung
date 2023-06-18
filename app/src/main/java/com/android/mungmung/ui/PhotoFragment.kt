package com.android.mungmung.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.R
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentPhotoBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private lateinit var binding: FragmentPhotoBinding
    private lateinit var abandonedAdapter: AbandonedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhotoBinding.bind(view)

        abandonedAdapter = AbandonedAdapter {

            findNavController().navigate(
                PhotoFragmentDirections.actionPhotoFragmentToArticleFragment(
                    it.userId.orEmpty(),
                    it.articleId.orEmpty(),
                    abandoned = "y"
                )
            )
        }

        binding.abandonedRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = abandonedAdapter

        }

        Firebase.firestore.collection("abandoned")
            .get()
            .addOnSuccessListener { result ->
                val list = result
                    .map { queryDocumentSnapshot -> queryDocumentSnapshot.toObject<ArticleModel>() }
                    .map { model ->

                        ArticleModel(
                            articleId = model.articleId.orEmpty(),
                            userId = model.userId.orEmpty(),
                            description = model.description.orEmpty(),
                            imageUrl = model.imageUrl.orEmpty()
                        )

                    }

                abandonedAdapter.submitList(list)
            }
    }
}