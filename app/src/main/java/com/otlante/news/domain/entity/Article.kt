package com.otlante.news.domain.entity

data class Article(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val sourceName: String,
    val publishedAt: Long,
    val url: String
)
