package com.otlante.news.domain.usecase

import com.otlante.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.updateNotificationsEnabled(enabled)
    }
}
