package com.android.mungmung.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.android.mungmung.R
import com.android.mungmung.data.Abandoned
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.FragmentArticleBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ArticleFragment: Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        var userId = args.userId
        var articleId = args.articleId


        if(!userId.contains("@")){
            val temp = userId
            userId = articleId
            articleId = temp

            Log.d("userId",userId)
            binding.toolBar.title = userId
        }


        auth = Firebase.auth
        val articleRef = Firebase.firestore

        articleRef.collection("abandoned").document(articleId)
            .get()
            .addOnSuccessListener{
                if(it.exists()){
                    val model = it.toObject<Abandoned>()!!
                    binding.descriptionTextView.text = model.description

                    Log.d("model",articleId)


                    binding.abandonedText.visibility = View.VISIBLE
                    binding.abandonedText.text = "이 유기견의 추정 나이는 " + model.age.toString()+
                            "살입니다.\n 이 강아지를 보호하고 싶으신 분은 ["+
                            model.phone.toString()+"]으로 연락주시길 바랍니다."

                    Glide.with(binding.ImageView)
                        .load(model.imageUrl)
                        .into(binding.ImageView)

                    if(auth.currentUser?.email == model.userId.toString()){
                        binding.removeArticleBtn.visibility = View.VISIBLE
                    }
                }


                else {
                    articleRef.collection("articles").document(articleId)
                        .get()
                        .addOnSuccessListener {

                            val model = it.toObject<ArticleModel>()
                            binding.descriptionTextView.text = model?.description

                            Log.d("model",articleId)

                            Glide.with(binding.ImageView)
                                .load(model?.imageUrl)
                                .into(binding.ImageView)

                            if(auth.currentUser?.email == model?.userId.toString()){
                                binding.removeArticleBtn.visibility = View.VISIBLE
                            }
                        }
                        .addOnFailureListener {

                        }
                }
            }


        binding.toolBar.setupWithNavController(findNavController())

        binding.toolBar.setOnClickListener {
            // 다른 사용자 페이지 보여주기
            findNavController().navigate(
                ArticleFragmentDirections.actionArticleProfileToOtherUserFragment(
                    userId = userId
                )
            )
        }


        binding.removeArticleBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(context).setTitle("게시물 삭제")
                .setMessage("해당 게시물을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialogInterface, i ->
                    articleRef.collection("articles").document(articleId).delete()
                    Firebase.firestore.collection("users")
                        .whereEqualTo("email", auth.currentUser?.email.toString()).get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                document.reference.collection("my_articles")
                                    .document(articleId).delete()
                                document.reference.update(
                                    "posts",
                                    Integer.parseInt(document.get("posts").toString()) - 1
                                )
                            }
                        }
                    Snackbar.make(view, "해당 게시물이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                }
                .show()
        }

    }
}