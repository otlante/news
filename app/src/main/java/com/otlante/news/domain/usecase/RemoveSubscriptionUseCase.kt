package com.otlante.news.domain.usecase

import com.otlante.news.domain.repository.NewsRepository
import javax.inject.Inject

class RemoveSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(topic: String) {
        newsRepository.removeSubscription(topic)
    }
}
