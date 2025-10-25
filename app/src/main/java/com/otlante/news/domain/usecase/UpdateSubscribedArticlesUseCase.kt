package com.otlante.news.domain.usecase

import com.otlante.news.domain.repository.NewsRepository
import com.otlante.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateSubscribedArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): List<String> {
        val settings = settingsRepository.getSettings().first()
        return newsRepository.updateArticlesForAllSubscriptions(settings.language)
    }
}
