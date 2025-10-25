package com.otlante.news.domain.usecase

import com.otlante.news.domain.entity.Language
import com.otlante.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(language: Language) {
        settingsRepository.updateLanguage(language)
    }
}
