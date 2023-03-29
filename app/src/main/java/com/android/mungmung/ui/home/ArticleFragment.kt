package com.android.mungmung.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.android.mungmung.R
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentArticleBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ArticleFragment: Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        val userId = args.userId
        val articleId = args.articleId

        binding.toolBar.setupWithNavController(findNavController())


        Firebase.firestore.collection("articles").document(articleId)
            .get()
            .addOnSuccessListener {
                val model = it.toObject<ArticleModel>()
                binding.descriptionTextView.text = model?.description

                Glide.with(binding.ImageView)
                    .load(model?.imageUrl)
                    .into(binding.ImageView)
            }
            .addOnFailureListener {

            }


    }
}