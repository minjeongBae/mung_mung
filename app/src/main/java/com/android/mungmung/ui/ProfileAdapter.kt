package com.android.mungmung.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.mungmung.data.ArticleModel
import com.android.mungmung.databinding.ItemProfileArticleBinding
import com.bumptech.glide.Glide

class ProfileAdapter (val onItemClicked: (ArticleModel) -> Unit) : ListAdapter<ArticleModel,ProfileAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder (private val binding: ItemProfileArticleBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(articleModel: ArticleModel) {

            Glide.with(binding.imageViewProfileArticle)
                .load(articleModel.imageUrl)
                .into(binding.imageViewProfileArticle)

            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):ViewHolder{
        return ViewHolder(
            ItemProfileArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
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