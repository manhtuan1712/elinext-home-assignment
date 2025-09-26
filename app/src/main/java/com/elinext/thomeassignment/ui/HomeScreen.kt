package com.elinext.thomeassignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elinext.thomeassignment.ui.theme.ElinextHomeAssignmentTheme

data class GridItem(
    val id: Int,
    val title: String,
    val imageUrl: String
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
                        val newId = gridItems.size + 1
                        gridItems = gridItems + GridItem(
                            id = newId,
                            title = "Image $newId",
                            imageUrl = "https://picsum.photos/400/400?random=$newId"
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(7.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var isLoading by remember { mutableStateOf(true) }
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Image ${item.id}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false },
                onError = { isLoading = false }
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 1.5.dp
                )
            }
            Text(
                text = "#${item.id}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 5.dp)
            )
        }
    }
}

fun generateSampleData(): List<GridItem> {
    return (1..140).map { index ->
        GridItem(
            id = index,
            title = "Image $index",
            imageUrl = "https://picsum.photos/200/200?random=$index"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ElinextHomeAssignmentTheme {
        HomeScreen()
    }
}
