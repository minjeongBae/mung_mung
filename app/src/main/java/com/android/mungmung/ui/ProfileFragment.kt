package com.android.mungmung.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.R
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.data.UserData
import com.android.mungmung.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileAdapter = ProfileAdapter{
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToArticleFragment(
                userId = it.userId.orEmpty(),
                articleId = it.articleId.orEmpty()
            ))
        }

        binding = FragmentProfileBinding.bind(view)
        binding.profileRecyclerview.apply {
            layoutManager = GridLayoutManager(context,3)
            adapter = profileAdapter
        }

        auth = Firebase.auth

        val currentUser = auth.currentUser
        var userData= UserData("","",0,0,0)
        var profileImage: String?=null
        val userdb = FirebaseFirestore.getInstance().collection("users")
        var myArticles = mutableListOf<ArticleModel>()

        userdb.whereEqualTo("email", currentUser?.email.toString()).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    userData= UserData(
                        document.getString("name").toString(),
                        document.getString("pet_name").toString(),
                        Integer.parseInt(document.getLong("following").toString()),
                        Integer.parseInt(document.getLong("followers").toString()),
                        Integer.parseInt(document.getLong("posts").toString())
                    )
                    profileImage = document.getString("profile_image_url").toString()

                    userdb.document(document.id).collection("my_articles")
                        .get()
                        .addOnSuccessListener { snapshot ->
                            for (doc in snapshot.documents) {
                                val article = doc.toObject<ArticleModel>()
                                if (article != null) {
                                    myArticles.add(article)
                                }
                            }
                            profileAdapter.submitList(myArticles)
                    }
                    binding.profileTextPostsNum.text = userData.posts.toString()
                    binding.profileUserName.text = userData.name
                    binding.profilePetName.text = userData.pet
                    binding.profileTextFollowsNum.text = userData.following.toString()
                    binding.profileTextFollowersNum.text = userData.followers.toString()
                    if(profileImage!="null"){
                        Glide.with(binding.profileImage)
                            .load(profileImage)
                            .into(binding.profileImage)
                    }
                }
            }

        binding.profileEditBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToChangeProfileFragment()
            findNavController().navigate(action)
        }

    }

}