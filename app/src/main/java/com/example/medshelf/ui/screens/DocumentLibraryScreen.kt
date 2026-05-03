package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.viewmodel.DocumentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentLibraryScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel
) {
    val documents = documentViewModel.documents.value
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        documentViewModel.loadDocuments()
    }

    val categories = listOf(
        CategoryItem("All", Icons.Default.Folder, MaterialTheme.colorScheme.primary),
        CategoryItem("Lab Results", Icons.Default.Biotech, Color(0xFF0EA5E9)),
        CategoryItem("Prescriptions", Icons.Default.Medication, Color(0xFF7C3AED)),
        CategoryItem("Certificates", Icons.Default.Badge, Color(0xFFF59E0B))
    )

    val filteredDocuments = if (selectedFilter == "All") {
        documents
    } else {
        documents.filter { it.type.equals(selectedFilter, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Documents",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "All your important medical documents in one place",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_document") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategoryCapsule(
                        label = category.label,
                        icon = category.icon,
                        accentColor = category.color,
                        selected = selectedFilter == category.label,
                        onClick = { selectedFilter = category.label }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 100.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredDocuments) { doc ->
                    DocumentGridItem(
                        title = doc.name,
                        type = doc.type
                    )
                }

                item {
                    AddDocumentGridItem {
                        navController.navigate("add_document")
                    }
                }
            }
        }
    }
}

data class CategoryItem(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun CategoryCapsule(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (selected) accentColor else MaterialTheme.colorScheme.surface,
        shadowElevation = if (selected) 5.dp else 2.dp,
        tonalElevation = if (selected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else accentColor,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = label,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DocumentGridItem(title: String, type: String) {
    val accent = categoryColor(type)
    val icon = categoryIcon(type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                color = accent.copy(alpha = 0.13f),
                                shape = PaperFoldShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 7.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "PDF",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Saved document",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = type.ifBlank { "Uncategorized" },
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun AddDocumentGridItem(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Document",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Add Document",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

val PaperFoldShape: Shape = GenericShape { size, _ ->
    val fold = size.width * 0.28f

    moveTo(0f, 0f)
    lineTo(size.width - fold, 0f)
    lineTo(size.width, fold)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

fun categoryIcon(type: String): ImageVector {
    return when {
        type.contains("Lab", ignoreCase = true) -> Icons.Default.Biotech
        type.contains("Prescription", ignoreCase = true) -> Icons.Default.Medication
        type.contains("Certificate", ignoreCase = true) -> Icons.Default.Badge
        else -> Icons.Default.Description
    }
}

fun categoryColor(type: String): Color {
    return when {
        type.contains("Lab", ignoreCase = true) -> Color(0xFF0EA5E9)
        type.contains("Prescription", ignoreCase = true) -> Color(0xFF7C3AED)
        type.contains("Certificate", ignoreCase = true) -> Color(0xFFF59E0B)
        else -> Color(0xFF009488)
    }
}