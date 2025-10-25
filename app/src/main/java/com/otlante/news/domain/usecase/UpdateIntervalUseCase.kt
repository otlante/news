package com.otlante.news.domain.usecase

import com.otlante.news.domain.entity.Interval
import com.otlante.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(interval: Interval) {
        settingsRepository.updateInterval(interval.minutes)
    }
}
