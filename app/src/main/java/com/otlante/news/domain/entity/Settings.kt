package com.otlante.news.domain.entity

data class Settings(
    val language: Language,
    val interval: Interval,
    val notificationsEnabled: Boolean,
    val wifiOnly: Boolean
) {
    companion object {
        val DEFAULT_LANGUAGE = Language.ENGLISH
        val DEFAULT_INTERVAL = Interval.MIN_15
        const val DEFAULT_NOTIFICATIONS_ENABLED = false
        const val DEFAULT_WIFI_ONLY = false
    }
}

enum class Language {

    ENGLISH, RUSSIAN, FRENCH, GERMAN
}

@Suppress("MagicNumber")
enum class Interval(val minutes: Int) {

    MIN_15(15),
    MIN_30(30),
    HOUR_1(60),
    HOUR_2(120),
    HOUR_4(240),
    HOUR_8(480),
    HOUR_24(1440)
}
