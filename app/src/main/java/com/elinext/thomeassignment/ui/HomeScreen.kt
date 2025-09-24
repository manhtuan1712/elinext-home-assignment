package com.elinext.thomeassignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        LazyHorizontalGrid(
            rows = GridCells.Fixed(10),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(gridItems) { item ->
                GridItemCard(
                    item = item,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
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
    val availableHeight = screenHeight - 64.dp - 8.dp - 18.dp
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
