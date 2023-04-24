package com.android.mungmung.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.R
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        auth = Firebase.auth

        initAddButton(view)

        val articleAdapter = HomeArticleAdapter {

            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                userId = it.userId.orEmpty(),
                articleId = it.articleId.orEmpty()
            ))
        }

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = articleAdapter
        }

        Firebase.firestore.collection("articles")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map {
                    it.toObject<ArticleModel>()
                }

                articleAdapter.submitList(list)
            }

    }

    private fun initAddButton(view: View) {
        binding.addArticleButton.setOnClickListener {

            if (auth.currentUser != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToAddArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_SHORT).show()
            }


        }
    }


}