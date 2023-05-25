package com.android.mungmung.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.ItemArticleBinding
import com.bumptech.glide.Glide

class AbandonedAdapter  (val onItemClicked: (ArticleModel) -> Unit)
    : ListAdapter<ArticleModel, AbandonedAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemArticleBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(articleModel: ArticleModel){

            binding.bookmarkImageView.isVisible = false

            Glide.with(binding.articleImageView)
                .load(articleModel.imageUrl)
                .into(binding.articleImageView)

            binding.root.setOnClickListener {
                onItemClicked(articleModel)
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
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.articleId == newItem.articleId
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.articleId == newItem.articleId
            }
        }
    }
}