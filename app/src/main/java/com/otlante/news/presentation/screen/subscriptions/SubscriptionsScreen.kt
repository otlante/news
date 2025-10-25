@file:OptIn(ExperimentalMaterial3Api::class)

package com.otlante.news.presentation.screen.subscriptions

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.otlante.news.R
import com.otlante.news.domain.entity.Article
import com.otlante.news.presentation.ui.theme.CustomIcons
import com.otlante.news.presentation.utils.formatDate

@Composable
fun SubscriptionsScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SubscriptionsTopBar(
                onRefreshDataClick = {
                    viewModel.processCommand(SubscriptionCommand.RefreshData)
                },
                onClearArticlesClick = {
                    viewModel.processCommand(SubscriptionCommand.ClearArticles)
                },
                onSettingsClick = onNavigateToSettings,
            )
        }
    ) {innerPadding ->
        val state by viewModel.state.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Subscriptions(
                    subscriptions = state.subscriptions,
                    query = state.query,
                    isSubscribeButtonEnabled = state.subscribeButtonEnabled,
                    onQueryChange = {
                        viewModel.processCommand(SubscriptionCommand.InputTopic(it))
                    },
                    onTopicClick = {
                        viewModel.processCommand(SubscriptionCommand.ToggleTopicSelection(it))
                    },
                    onDeleteSubscription = {
                        viewModel.processCommand(SubscriptionCommand.RemoveSubscription(it))
                    },
                    onSubscribeButtonClick = {
                        viewModel.processCommand(SubscriptionCommand.ClickSubscribe)
                    },
                )
            }
            if (state.articles.isNotEmpty()) {
                item {
                    HorizontalDivider()
                }
                item {
                    Text(
                        text = "Articles (${state.articles.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    HorizontalDivider()
                }
                items(
                    items = state.articles,
                    key = { it.url }
                ) { article ->
                    ArticleCard(
                        article = article
                    )
                }
            } else if (state.subscriptions.isNotEmpty()){
                item {
                    HorizontalDivider()
                }
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.no_articles_for_selected_topics),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionsTopBar(
    onRefreshDataClick: () -> Unit,
    onClearArticlesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.subscriptions_title))
        },
        actions = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onRefreshDataClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.update_articles)
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onClearArticlesClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.clear_articles)
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onSettingsClick()
                    }
                    .padding(8.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_screen)
            )
        }
    )
}

@Composable
private fun SubscriptionChip(
    topic: String,
    isSelected: Boolean,
    onSubscriptionClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = {
            onSubscriptionClick(topic)
        },
        label = {
            Text(text = topic)
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        onDelete(topic)
                    },
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.remove_subscription)
            )
        }
    )
}

@Composable
private fun Subscriptions(
    subscriptions: Map<String, Boolean>,
    query: String,
    isSubscribeButtonEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeleteSubscription: (String) -> Unit,
    onSubscribeButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            label = {
                Text(text = stringResource(R.string.what_interests_you))
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSubscribeButtonClick,
            enabled = isSubscribeButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_subscription)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_subscription_btn))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (subscriptions.isNotEmpty()) {
            Text(
                text = stringResource(R.string.subscriptions, subscriptions.size),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subscriptions.forEach { (topic, isSelected) ->
                    item(key = topic) {
                        SubscriptionChip(
                            topic = topic,
                            isSelected = isSelected,
                            onSubscriptionClick = onTopicClick,
                            onDelete = onDeleteSubscription
                        )
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.no_subscriptions),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ArticleCard(
    article: Article,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        article.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.image_for_article, article.title),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = article.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (article.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = article.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.sourceName,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = article.publishedAt.formatDate(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val context = LocalContext.current

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = CustomIcons.OpenInNew,
                    contentDescription = stringResource(R.string.read_article)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.read))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.url}")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_article)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.share))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
