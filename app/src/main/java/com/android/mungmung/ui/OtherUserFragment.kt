package com.android.mungmung.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.android.mungmung.R
import com.android.mungmung.databinding.FragmentOtherUserBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.data.UserData
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OtherUserFragment : Fragment(R.layout.fragment_other_user) {

    private lateinit var binding: FragmentOtherUserBinding
    private lateinit var auth: FirebaseAuth
    private val args: OtherUserFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtherUserBinding.bind(view)

        val otherUserAdapter = OtherUserAdapter{
            findNavController().navigate(OtherUserFragmentDirections.actionOtherUserToArticleFragment(
                userId = it.userId.orEmpty(),
                articleId = it.articleId.orEmpty(),
                abandoned = "n"
            ))
        }

        binding.otherProfileRecyclerview.apply {
            layoutManager = GridLayoutManager(context,3)
            adapter = otherUserAdapter
        }

        auth = Firebase.auth
        val userId = args.userId
        Log.d("userId_In_Other_User", userId)

        val userdb = FirebaseFirestore.getInstance().collection("users")
        var articles = mutableListOf<ArticleModel>()
        var userData= UserData("","",0,0,0)
        var profileImage: String?=null

        userdb.whereEqualTo("email", userId).get()
            .addOnSuccessListener{ querySnapshot ->
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
                                    articles.add(article)
                                }
                            }
                            otherUserAdapter.submitList(articles)
                        }
                    binding.otherProfileTextPostsNum.text = userData.posts.toString()
                    binding.otherProfileUserName.text = userData.name
                    binding.otherProfilePetName.text = userData.pet
                    binding.otherProfileTextFollowsNum.text = userData.following.toString()
                    binding.otherProfileTextFollowersNum.text = userData.followers.toString()
                    if(profileImage!="null"){
                        Glide.with(binding.otherProfileImage)
                            .load(profileImage)
                            .into(binding.otherProfileImage)
                    }
                }
            }
        binding.otherUserFollowBtn.setOnClickListener {
            Log.d("팔로우버튼","clicked")


            
            userdb.whereEqualTo("email", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents){
                        val map = mutableMapOf<String,Int>()
                        map.put("followers", Integer.parseInt(document.get("followers").toString())+1)
                        document.reference.update(map as Map<String, Any>)
                    }}
        }
    }
}
