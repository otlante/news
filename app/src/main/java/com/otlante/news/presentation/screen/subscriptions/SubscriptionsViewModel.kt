@file:OptIn(ExperimentalCoroutinesApi::class)

package com.otlante.news.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otlante.news.domain.entity.Article
import com.otlante.news.domain.usecase.AddSubscriptionUseCase
import com.otlante.news.domain.usecase.ClearAllArticlesUseCase
import com.otlante.news.domain.usecase.GetAllSubscriptionsUseCase
import com.otlante.news.domain.usecase.GetArticlesByTopicsUseCase
import com.otlante.news.domain.usecase.RemoveSubscriptionUseCase
import com.otlante.news.domain.usecase.UpdateSubscribedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val removeSubscriptionUseCase: RemoveSubscriptionUseCase,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionCommand) {
        when (command) {

            SubscriptionCommand.ClearArticles -> {
                viewModelScope.launch {
                    val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }

            SubscriptionCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = state.value.query.trim()
                        addSubscriptionUseCase(topic)
                        previousState.copy(
                            query = ""
                        )
                    }
                }
            }

            is SubscriptionCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(query = command.query)
                }
            }

            SubscriptionCommand.RefreshData -> {
                viewModelScope.launch {
                    updateSubscribedArticlesUseCase()
                }
            }

            is SubscriptionCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionUseCase(command.topic)
                }
            }

            is SubscriptionCommand.ToggleTopicSelection -> {
                _state.update { previousState ->
                    val subscriptions = previousState.subscriptions.toMutableMap()
                    val isSelected = subscriptions[command.topic] ?: false
                    subscriptions[command.topic] = !isSelected
                    previousState.copy(subscriptions = subscriptions)
                }
            }
        }
    }

    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            .distinctUntilChanged()
            .flatMapLatest {
                getArticlesByTopicsUseCase(it)
            }
            .onEach {
                _state.update { previousState ->
                    previousState.copy(articles = it)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase()
            .onEach { subscriptions ->
                _state.update { previousState ->
                    val updatedTopics = subscriptions.associateWith { topic ->
                        previousState.subscriptions[topic] ?: true
                    }
                    previousState.copy(
                        subscriptions = updatedTopics
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed interface SubscriptionCommand {

    data class InputTopic(val query: String) : SubscriptionCommand

    data object ClickSubscribe : SubscriptionCommand

    data object RefreshData : SubscriptionCommand

    data class ToggleTopicSelection(val topic: String) : SubscriptionCommand

    data object ClearArticles : SubscriptionCommand

    data class RemoveSubscription(val topic: String) : SubscriptionCommand
}

data class SubscriptionsState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf()
) {

    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()

    val selectedTopics: List<String>
        get() = subscriptions.filter { it.value }.map { it.key }
}
