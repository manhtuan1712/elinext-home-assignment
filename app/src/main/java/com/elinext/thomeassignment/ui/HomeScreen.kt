package com.elinext.thomeassignment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elinext.thomeassignment.ui.theme.ElinextHomeAssignmentTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GridItem(
    val id: Int,
    val title: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var reloadSession by remember { mutableIntStateOf(0) }
    var gridItems by remember { mutableStateOf(emptyList<GridItem>()) }
    var isReloading by remember { mutableStateOf(false) }
    var currentChunk by remember { mutableIntStateOf(0) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var isRetrying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            errorState = null
            loadAllChunks(
                reloadSession,
                onUpdate = { newItems ->
                    gridItems = newItems
                },
                onError = { error ->
                    errorState = error
                }
            )
        } catch (e: Exception) {
            errorState = "Failed to load images: ${e.message}"
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val itemsPerPage = 70
    val totalPages = (gridItems.size + itemsPerPage - 1) / itemsPerPage
    val pagerState = rememberPagerState(pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Home Screen",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        val newId = gridItems.size + 1 + (reloadSession * 1000)
                        gridItems = gridItems + GridItem(
                            id = newId,
                            title = "Image $newId",
                            imageUrl = "https://picsum.photos/200/200"
                        )
                        coroutineScope.launch {
                            val lastPage = (gridItems.size - 1) / 70
                            pagerState.animateScrollToPage(lastPage)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item"
                    )
                }
                IconButton(
                    onClick = {
                        isReloading = true
                        errorState = null
                        currentChunk = 0

                        coroutineScope.launch {
                            try {
                                gridItems = emptyList()
                                delay(150)

                                reloadSession++
                                loadAllChunks(
                                    reloadSession,
                                    onUpdate = { newItems ->
                                        gridItems = newItems
                                    },
                                    onError = { error ->
                                        errorState = error
                                        isReloading = false
                                    }
                                )

                                pagerState.animateScrollToPage(0)
                                isReloading = false
                            } catch (e: Exception) {
                                errorState = "Failed to reload: ${e.message}"
                                isReloading = false
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload All"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        when {
            errorState != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Oops! Something went wrong",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorState!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                isRetrying = true
                                errorState = null
                                coroutineScope.launch {
                                    try {
                                        loadAllChunks(
                                            reloadSession,
                                            onUpdate = { newItems ->
                                                gridItems = newItems
                                                isRetrying = false
                                            },
                                            onError = { error ->
                                                errorState = error
                                                isRetrying = false
                                            }
                                        )
                                    } catch (e: Exception) {
                                        errorState = "Retry failed: ${e.message}"
                                        isRetrying = false
                                    }
                                }
                            },
                            enabled = !isRetrying
                        ) {
                            if (isRetrying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Try Again")
                        }
                    }
                }
            }

            gridItems.isEmpty() && isReloading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Reloading images...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) { page ->
                        val startIndex = page * itemsPerPage
                        val endIndex = minOf(startIndex + itemsPerPage, gridItems.size)
                        val pageItems = gridItems.subList(startIndex, endIndex)
                        
                        val availableWidth = screenWidth - 20.dp
                        val availableHeight = screenHeight - 160.dp
                        val itemWidth = availableWidth / 7
                        val itemHeight = availableHeight / 10
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            contentPadding = PaddingValues(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize(),
                            userScrollEnabled = false
                        ) {
                            items(
                                items = pageItems,
                                key = { it.id }
                            ) { item ->
                                GridItemCard(
                                    item = item,
                                    itemWidth = itemWidth,
                                    itemHeight = itemHeight,
                                    reloadSession = reloadSession
                                )
                            }
                        }
                    }
                    if (totalPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(totalPages) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 10.dp else 8.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                )
                                if (index < totalPages - 1) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridItemCard(
    item: GridItem,
    itemWidth: androidx.compose.ui.unit.Dp,
    itemHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    reloadSession: Int = 0,
) {
    Card(
        modifier = modifier
            .width(itemWidth)
            .height(itemHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(7.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var isLoading by remember { mutableStateOf(true) }
            val context = LocalContext.current
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(7.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.imageUrl)
                        .size(itemWidth.value.toInt(), itemHeight.value.toInt())
                        .crossfade(200)
                        .memoryCacheKey("session_${reloadSession}_image_${item.id}")
                        .diskCacheKey("session_${reloadSession}_image_${item.id}")
                        .allowHardware(false)
                        .build(),
                    contentDescription = "Image ${item.id}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 1.5.dp
                )
            }
        }
    }
}


private const val CHUNK_SIZE = 35
private const val TOTAL_ITEMS = 140

suspend fun loadNextChunk(
    chunkIndex: Int,
    session: Int,
    onUpdate: (List<GridItem>) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    try {
        delay(100)
        val startIndex = chunkIndex * CHUNK_SIZE + 1
        val endIndex = minOf(startIndex + CHUNK_SIZE - 1, TOTAL_ITEMS)
        val chunkItems = (startIndex..endIndex).map { index ->
            GridItem(
                id = index + (session * 1000),
                title = "Image $index",
                imageUrl = "https://picsum.photos/200/200"
            )
        }

        onUpdate(chunkItems)
    } catch (e: Exception) {
        onError?.invoke("Failed to load chunk ${chunkIndex + 1}: ${e.message}")
    }
}

suspend fun loadAllChunks(
    session: Int,
    onUpdate: (List<GridItem>) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    try {
        val totalChunks = (TOTAL_ITEMS + CHUNK_SIZE - 1) / CHUNK_SIZE
        val allItems = mutableListOf<GridItem>()
        var failedChunks = 0

        for (chunkIndex in 0 until totalChunks) {
            loadNextChunk(
                chunkIndex, session,
                onUpdate = { chunkItems ->
                    allItems.addAll(chunkItems)
                    onUpdate(allItems.toList())
                },
                onError = { error ->
                    failedChunks++
                    if (failedChunks > totalChunks / 2) {
                        onError?.invoke("Too many chunks failed to load. Please check your internet connection.")
                        return@loadNextChunk
                    }
                }
            )
            delay(200)
        }

        if (failedChunks > 0 && failedChunks <= totalChunks / 2) {
            onError?.invoke("Some images failed to load ($failedChunks chunks). Showing partial results.")
        }

    } catch (e: Exception) {
        onError?.invoke("Failed to load images: ${e.message}")
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ElinextHomeAssignmentTheme {
        HomeScreen()
    }
}
