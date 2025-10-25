package com.otlante.news.data.mapper

import com.otlante.news.data.local.ArticleDbModel
import com.otlante.news.data.remote.NewsResponseDto
import com.otlante.news.domain.entity.Article
import com.otlante.news.domain.entity.Interval
import com.otlante.news.domain.entity.Language
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsResponseDto.toDbModels(topic: String): List<ArticleDbModel> {
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            publishedAt = it.publishedAt.toTimestamp(),
            url = it.url,
            topic = topic
        )
    }
}

fun Language.toQueryParam(): String {
    return when (this) {
        Language.ENGLISH -> "en"
        Language.RUSSIAN -> "ru"
        Language.FRENCH -> "fr"
        Language.GERMAN -> "de"
    }
}

fun Int.toInterval(): Interval {
    return Interval.entries.first { it.minutes == this }
}

fun List<ArticleDbModel>.toEntities(): List<Article> {
    return map {
        Article(
            title = it.title,
            description = it.description,
            imageUrl = it.imageUrl,
            sourceName = it.sourceName,
            publishedAt = it.publishedAt,
            url = it.url
        )
    }.distinct()
}

private fun String.toTimestamp(): Long {
    val dateFormatter = SimpleDateFormat("dddd-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}
