package com.android.mungmung.data

data class ArticleItem(
    val articleId: String,
    var userId: String,
    val description: String,
    val imageUrl: String,
    var isBookMark: Boolean
)
