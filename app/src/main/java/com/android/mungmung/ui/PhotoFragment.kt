package com.android.mungmung.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentPhotoBinding
import com.android.mungmung.databinding.FragmentSearchBinding
import com.android.mungmung.ui.home.BookmarkArticleAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private lateinit var binding: FragmentPhotoBinding
    private lateinit var bookmarkArticleAdapter: BookmarkArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhotoBinding.bind(view)

        bookmarkArticleAdapter = BookmarkArticleAdapter {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToArticleFragment(
                    it.articleId.orEmpty(),
                    it.userId.orEmpty()
                )
            )
        }

        binding.abandonedRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = bookmarkArticleAdapter

        }

        val uid = Firebase.auth.currentUser?.uid.orEmpty()
        Firebase.firestore.collection("bookmarks")
            .document(uid)
            .get()
            .addOnSuccessListener {

                val list = it.get("articleIds") as List<*>
                Log.d("dataList", {list}.toString())

                if (list.isNotEmpty()) {
                    Firebase.firestore.collection("articles")
                        .whereIn("articleId", list)
                        .get()
                        .addOnSuccessListener { result ->
                            bookmarkArticleAdapter.submitList(result.map { article -> article.toObject() })
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }

                }

            }
            .addOnFailureListener {
                Log.d("data", it.toString())
            }
    }
}