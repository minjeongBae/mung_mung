package com.android.mungmung.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.R
import com.android.mungmung.data.ArticleItem
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var articleAdapter: HomeArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        auth = Firebase.auth

        initAddButton(view)
        setupRecyclerView()
        fetchFirestoreData()


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

    private fun setupRecyclerView() {
        articleAdapter = HomeArticleAdapter(

            onItemClicked = {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                        userId = it.userId.orEmpty(),
                        articleId = it.articleId.orEmpty()
                    )
                )
            },
            onBookmarkClicked = { articleId, isBookmark ->
                val uid = Firebase.auth.currentUser?.uid ?: return@HomeArticleAdapter
                Firebase.firestore.collection("bookmarks").document(uid)
                    .update(
                        "articleIds",
                        if (isBookmark) {
                            FieldValue.arrayUnion(articleId)
                        } else {
                            FieldValue.arrayRemove(articleId)
                        }
                    ).addOnFailureListener {
                        if (it is FirebaseFirestoreException && it.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                            if (isBookmark) {
                                Firebase.firestore.collection("bookmarks").document(uid)
                                    .set(hashMapOf (
                                        "articleIds" to listOf(articleId)
                                    ))
                            }
                        }
                    }
            }

        )

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = articleAdapter
        }

    }

    private fun fetchFirestoreData() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("bookmarks")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val bookMarkList = it.get("articleIds") as? List<*>

                Firebase.firestore.collection("articles")
                    .get()
                    .addOnSuccessListener { result ->
                        val list = result
                            .map { queryDocumentSnapshot -> queryDocumentSnapshot.toObject<ArticleModel>() }
                            .map { model ->

                                ArticleItem(
                                    articleId = model.articleId.orEmpty(),
                                    userId = model.userId.orEmpty(),
                                    description = model.description.orEmpty(),
                                    imageUrl = model.imageUrl.orEmpty(),
                                    isBookMark = bookMarkList?.contains(model.articleId.orEmpty()) ?: false
                                )

                            }

                        articleAdapter.submitList(list)
                    }

            }
    }


}