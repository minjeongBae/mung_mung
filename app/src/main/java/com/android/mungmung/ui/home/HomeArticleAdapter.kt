package com.android.mungmung.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.mungmung.R
import com.android.mungmung.data.ArticleItem
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.ItemArticleBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeArticleAdapter
    (val onItemClicked: (ArticleItem) -> Unit,
     val onBookmarkClicked: (String, Boolean) -> Unit
) : ListAdapter<ArticleItem, HomeArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemArticleBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(articleItem: ArticleItem) {

                Glide.with(binding.articleImageView)
                    .load(articleItem.imageUrl)
                    .into(binding.articleImageView)

                binding.root.setOnClickListener {
                    onItemClicked(articleItem)
                }

                if (articleItem.isBookMark) {
                    binding.bookmarkImageView.setBackgroundResource(R.drawable.ic_baseline_bookmark_24)
                } else {
                    binding.bookmarkImageView.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24)
                }

                binding.bookmarkImageView.setOnClickListener {
                    onBookmarkClicked.invoke(articleItem.articleId, articleItem.isBookMark.not())

                    articleItem.isBookMark = articleItem.isBookMark.not()
                    if (articleItem.isBookMark) {
                        binding.bookmarkImageView.setBackgroundResource(R.drawable.ic_baseline_bookmark_24)
                    } else {
                        binding.bookmarkImageView.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24)
                    }
                }

            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleItem>() {
            override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.articleId == newItem.articleId
            }

            override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.articleId == newItem.articleId
            }
        }
    }
}