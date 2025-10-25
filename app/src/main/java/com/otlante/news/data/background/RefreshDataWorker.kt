package com.otlante.news.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.otlante.news.domain.usecase.GetSettingsUseCase
import com.otlante.news.domain.usecase.UpdateSubscribedArticlesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase,
    private val notificationsHelper: NotificationsHelper,
    private val getSettingsUseCase: GetSettingsUseCase
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("RefreshDataWorker", "Start")
        val settings = getSettingsUseCase().first()
        val updatedTopics = updateSubscribedArticlesUseCase()
        if (updatedTopics.isNotEmpty() && settings.notificationsEnabled) {
            notificationsHelper.showNewArticlesNotifications(updatedTopics)
        }
        Log.d("RefreshDataWorker", "Finish")
        return Result.success()
    }
}
