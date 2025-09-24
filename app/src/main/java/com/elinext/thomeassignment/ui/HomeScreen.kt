package com.elinext.thomeassignment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elinext.thomeassignment.ui.theme.ElinextHomeAssignmentTheme

data class GridItem(
    val id: Int,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var gridItems by remember {
        mutableStateOf(generateSampleData())
    }

    val itemsPerPage = 70
    val totalPages = (gridItems.size + itemsPerPage - 1) / itemsPerPage
    val pagerState = rememberPagerState(pageCount = { totalPages })
    
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

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
                        gridItems = gridItems + GridItem(
                            id = gridItems.size + 1,
                            title = "Item ${gridItems.size + 1}",
                            color = generateRandomColor()
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item"
                    )
                }
                IconButton(
                    onClick = {
                        gridItems = generateSampleData()
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val startIndex = page * itemsPerPage
            val endIndex = minOf(startIndex + itemsPerPage, gridItems.size)
            val pageItems = gridItems.subList(startIndex, endIndex)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pageItems) { item ->
                    GridItemCard(
                        item = item,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                }
            }
        }
        if (totalPages > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(totalPages) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
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

@Composable
fun GridItemCard(
    item: GridItem,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val availableWidth = screenWidth - 8.dp - 12.dp
    val itemWidth = availableWidth / 7
    
    val availableHeight = screenHeight - 64.dp - 8.dp - 18.dp - 40.dp
    val itemHeight = availableHeight / 10
    
    Card(
        modifier = modifier
            .width(itemWidth)
            .height(itemHeight),
        colors = CardDefaults.cardColors(
            containerColor = item.color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.id.toString(),
                color = Color.White,
                fontSize = minOf(itemHeight.value / 6, 14f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun generateSampleData(): List<GridItem> {
    return (1..140).map { index ->
        GridItem(
            id = index,
            title = "Item $index",
            color = generateRandomColor()
        )
    }
}

fun generateRandomColor(): Color {
    val colors = listOf(
        Color(0xFF6650a4),
        Color(0xFF625b71),
        Color(0xFF7D5260),
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF607D8B),
        Color(0xFF795548),
        Color(0xFF009688)
    )
    return colors.random()
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ElinextHomeAssignmentTheme {
        HomeScreen()
    }
}
