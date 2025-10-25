package com.otlante.news.domain.usecase

import com.otlante.news.domain.repository.NewsRepository
import javax.inject.Inject

class ClearAllArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(topics: List<String>) {
        newsRepository.clearAllArticles(topics)
    }
}
