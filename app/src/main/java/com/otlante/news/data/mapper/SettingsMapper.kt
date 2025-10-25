package com.otlante.news.data.mapper

import com.otlante.news.domain.entity.RefreshConfig
import com.otlante.news.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig {
    return RefreshConfig(language, interval, wifiOnly)
}
