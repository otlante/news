package com.otlante.news.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.otlante.news.data.background.RefreshDataWorker
import com.otlante.news.data.local.ArticleDbModel
import com.otlante.news.data.local.NewsDao
import com.otlante.news.data.local.SubscriptionDbModel
import com.otlante.news.data.mapper.toDbModels
import com.otlante.news.data.mapper.toEntities
import com.otlante.news.data.mapper.toQueryParam
import com.otlante.news.data.remote.NewsApiService
import com.otlante.news.domain.entity.Article
import com.otlante.news.domain.entity.Language
import com.otlante.news.domain.entity.RefreshConfig
import com.otlante.news.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val newsDao: NewsDao,
    private val workManager: WorkManager,
) : NewsRepository {

    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { it.topic }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String, language: Language): Boolean {
        val articles = loadArticles(topic, language)
        val ids = newsDao.addArticles(articles)
        return ids.any { it != -1L }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun loadArticles(topic: String, language: Language): List<ArticleDbModel> {
        return try {
            apiService.loadArticles(topic, language.toQueryParam()).toDbModels(topic)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            emptyList()
        }
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllSubscriptions(language: Language): List<String> {
        val updatedTopics = mutableListOf<String>()
        coroutineScope {
            newsDao.getAllSubscriptions().first().forEach {
                launch {
                    val updated = updateArticlesForTopic(it.topic, language)
                    if (updated) {
                        updatedTopics.add(it.topic)
                    }
                }
            }
        }
        return updatedTopics
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics).map {
            it.toEntities()
        }
    }

    override fun startBackgroundRefresh(refreshConfig: RefreshConfig) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (refreshConfig.wifiOnly) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            refreshConfig.interval.minutes.toLong(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "Refresh data",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }
}
