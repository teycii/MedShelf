package com.example.medshelf.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medshelf.model.DocumentEntity
import com.example.medshelf.viewmodel.DocumentViewModel
import com.example.medshelf.viewmodel.FamilyMemberViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun DocumentLibraryScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel,
    familyMemberViewModel: FamilyMemberViewModel,
    initialOwnerFilter: String = "All Profiles"
) {
    val documents by documentViewModel.documents.collectAsState()
    val familyMembers by familyMemberViewModel.familyMembers.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedOwnerFilter by remember(initialOwnerFilter) {
        mutableStateOf(initialOwnerFilter.ifBlank { "All Profiles" })
    }
    var searchQuery by remember { mutableStateOf("") }
    var documentToDelete by remember { mutableStateOf<DocumentEntity?>(null) }

    val categories = documentCategories()

    val ownerFilters = remember(familyMembers) {
        listOf("All Profiles", "Main Profile") +
                familyMembers.map { member ->
                    "${member.firstName} ${member.lastName}".trim()
                        .ifBlank { "Unnamed Profile" }
                }
    }

    LaunchedEffect(ownerFilters, initialOwnerFilter) {
        selectedOwnerFilter = if (initialOwnerFilter in ownerFilters) {
            initialOwnerFilter
        } else {
            "All Profiles"
        }
    }

    val filteredDocuments = documents
        .filter { document ->
            selectedOwnerFilter == "All Profiles" ||
                    document.owner.equals(selectedOwnerFilter, ignoreCase = true)
        }
        .filter { document ->
            selectedFilter == "All" ||
                    document.type.equals(selectedFilter, ignoreCase = true)
        }
        .filter { document ->
            searchQuery.isBlank() ||
                    document.name.contains(searchQuery, ignoreCase = true) ||
                    document.type.contains(searchQuery, ignoreCase = true) ||
                    document.owner.contains(searchQuery, ignoreCase = true) ||
                    document.clinic.contains(searchQuery, ignoreCase = true) ||
                    document.date.contains(searchQuery, ignoreCase = true) ||
                    document.notes.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Document Library",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_document") },
                containerColor = MedGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF9FFFC),
                            Color(0xFFEFFFF8)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LibraryHeader(
                documentCount = documents.size,
                filteredCount = filteredDocuments.size,
                selectedOwner = selectedOwnerFilter
            )

            SearchSection(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onClearSearch = { searchQuery = "" }
            )

            ProfileFilterSection(
                selectedOwner = selectedOwnerFilter,
                owners = ownerFilters,
                onOwnerSelected = { selectedOwnerFilter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CategorySection(
                categories = categories,
                selectedFilter = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredDocuments.isEmpty()) {
                EmptyDocumentState(
                    selectedFilter = selectedFilter,
                    selectedOwner = selectedOwnerFilter,
                    searchQuery = searchQuery,
                    onAddClick = { navController.navigate("add_document") }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 120.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredDocuments,
                        key = { it.id }
                    ) { doc ->
                        DocumentGridItem(
                            title = doc.name,
                            type = doc.type,
                            owner = doc.owner,
                            date = doc.date,
                            fileUri = doc.fileUri,
                            onClick = {
                                navController.navigate("document_details/${doc.id}")
                            },
                            onEditClick = {
                                navController.navigate("edit_document/${doc.id}")
                            },
                            onDeleteClick = {
                                documentToDelete = doc
                            }
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

        documentToDelete?.let { document ->
            DeleteDocumentDialog(
                document = document,
                onDismiss = { documentToDelete = null },
                onConfirmDelete = {
                    documentViewModel.deleteDocument(document)
                    documentToDelete = null
                }
            )
        }
    }
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 14.dp),
        placeholder = { Text("Search documents, owner, clinic...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = SoftText)
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = SoftText)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedGreen,
            unfocusedBorderColor = SoftBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = MedGreen
        )
    )
}

@Composable
private fun ProfileFilterSection(
    selectedOwner: String,
    owners: List<String>,
    onOwnerSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Profiles",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(owners) { owner ->
                val selected = selectedOwner == owner

                Surface(
                    modifier = Modifier.clickable { onOwnerSelected(owner) },
                    shape = RoundedCornerShape(50),
                    color = if (selected) MedGreen else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selected) MedGreen else SoftBorder
                    ),
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (owner == "All Profiles") Icons.Default.Groups else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selected) Color.White else MedGreen,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = owner,
                            color = if (selected) Color.White else DarkText,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteDocumentDialog(
    document: DocumentEntity,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Document", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to delete \"${document.name}\"?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LibraryHeader(
    documentCount: Int,
    filteredCount: Int,
    selectedOwner: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Text(
            text = "Medical Documents",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = if (selectedOwner == "All Profiles") {
                "$documentCount saved record${if (documentCount == 1) "" else "s"} across all profiles"
            } else {
                "$filteredCount record${if (filteredCount == 1) "" else "s"} for $selectedOwner"
            },
            style = MaterialTheme.typography.bodySmall,
            color = SoftText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategorySection(
    categories: List<CategoryItem>,
    selectedFilter: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                CategoryCapsule(
                    label = category.label,
                    icon = category.icon,
                    accentColor = category.color,
                    selected = selectedFilter == category.label,
                    onClick = { onSelect(category.label) }
                )
            }
        }
    }
}

private data class CategoryItem(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private fun documentCategories(): List<CategoryItem> {
    return listOf(
        CategoryItem("All", Icons.Default.Folder, MedGreen),
        CategoryItem("Laboratory Result", Icons.Default.Biotech, Color(0xFF0EA5E9)),
        CategoryItem("Prescription", Icons.Default.Medication, Color(0xFF7C3AED)),
        CategoryItem("Medical Certificate", Icons.Default.Badge, Color(0xFFF59E0B)),
        CategoryItem("X-Ray / Imaging", Icons.Default.MonitorHeart, Color(0xFF6366F1)),
        CategoryItem("Vaccination Record", Icons.Default.Vaccines, Color(0xFF16A34A)),
        CategoryItem("Discharge Summary", Icons.Default.LocalHospital, Color(0xFFEF4444)),
        CategoryItem("Hospital Bill", Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFEA580C)),
        CategoryItem("Insurance Document", Icons.Default.Shield, Color(0xFF0891B2)),
        CategoryItem("Doctor's Note", Icons.Default.NoteAlt, Color(0xFF8B5CF6)),
        CategoryItem("Medical Clearance", Icons.Default.HealthAndSafety, Color(0xFF14B8A6)),
        CategoryItem("ECG / Heart Test", Icons.Default.Favorite, Color(0xFFE11D48)),
        CategoryItem("Other", Icons.Default.Description, Color(0xFF64748B))
    )
}

@Composable
private fun CategoryCapsule(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (selected) accentColor else Color.White,
        shadowElevation = if (selected) 3.dp else 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) accentColor else SoftBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else accentColor,
                modifier = Modifier.size(17.dp)
            )

            Text(
                text = label,
                color = if (selected) Color.White else DarkText,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DocumentGridItem(
    title: String,
    type: String,
    owner: String,
    date: String,
    fileUri: String,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val accent = categoryColor(type)
    val icon = categoryIcon(type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DocumentPreviewBox(
                    fileUri = fileUri,
                    type = type,
                    accent = accent,
                    icon = icon
                )

                DocumentMoreMenu(
                    onViewDetails = onClick,
                    onEdit = onEditClick,
                    onDelete = onDeleteClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title.ifBlank { "Untitled Document" },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = DarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = owner.ifBlank { "Main Profile" },
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = date.ifBlank { "No date set" },
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = accent.copy(alpha = 0.10f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = type.ifBlank { "Uncategorized" },
                        color = accent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentPreviewBox(
    fileUri: String,
    type: String,
    accent: Color,
    icon: ImageVector
) {
    val context = LocalContext.current
    val imageFile = isImageFile(context, fileUri)

    Box {
        if (imageFile) {
            AsyncImage(
                model = Uri.parse(fileUri),
                contentDescription = type,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = accent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = accent.copy(alpha = 0.12f),
                        shape = PaperFoldShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(29.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 6.dp),
            shape = RoundedCornerShape(6.dp),
            color = accent
        ) {
            Text(
                text = if (imageFile) "IMG" else "FILE",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DocumentMoreMenu(
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = SoftText,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("View Details") },
                onClick = {
                    menuExpanded = false
                    onViewDetails()
                }
            )

            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    menuExpanded = false
                    onEdit()
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    menuExpanded = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
private fun AddDocumentGridItem(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MedGreen.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Document",
                        tint = MedGreen,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Add Document",
                    color = MedGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyDocumentState(
    selectedFilter: String,
    selectedOwner: String,
    searchQuery: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color(0xFFE6F7F4), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "No results found"
                selectedOwner != "All Profiles" -> "No documents for $selectedOwner"
                selectedFilter == "All" -> "No documents yet"
                else -> "No $selectedFilter documents"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = if (searchQuery.isNotBlank()) {
                "Try another keyword or clear the search."
            } else {
                "Upload your first medical document to start organizing."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = SoftText
        )

        if (searchQuery.isBlank()) {
            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = MedGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)

                Spacer(modifier = Modifier.width(8.dp))

                Text("Add Document")
            }
        }
    }
}

private val PaperFoldShape: Shape = GenericShape { size, _ ->
    val fold = size.width * 0.28f

    moveTo(0f, 0f)
    lineTo(size.width - fold, 0f)
    lineTo(size.width, fold)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

private fun isImageFile(
    context: Context,
    fileUri: String
): Boolean {
    return try {
        val uri = Uri.parse(fileUri)
        val mimeType = context.contentResolver.getType(uri)
        mimeType?.startsWith("image/") == true
    } catch (_: Exception) {
        false
    }
}

private fun categoryIcon(type: String): ImageVector {
    return when {
        type.contains("Lab", ignoreCase = true) -> Icons.Default.Biotech
        type.contains("Prescription", ignoreCase = true) -> Icons.Default.Medication
        type.contains("Certificate", ignoreCase = true) -> Icons.Default.Badge
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Icons.Default.MonitorHeart
        type.contains("Vaccination", ignoreCase = true) -> Icons.Default.Vaccines
        type.contains("Discharge", ignoreCase = true) -> Icons.Default.LocalHospital
        type.contains("Bill", ignoreCase = true) -> Icons.AutoMirrored.Filled.ReceiptLong
        type.contains("Insurance", ignoreCase = true) -> Icons.Default.Shield
        type.contains("Note", ignoreCase = true) -> Icons.Default.NoteAlt
        type.contains("Clearance", ignoreCase = true) -> Icons.Default.HealthAndSafety
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Icons.Default.Favorite
        else -> Icons.Default.Description
    }
}

private fun categoryColor(type: String): Color {
    return when {
        type.contains("Lab", ignoreCase = true) -> Color(0xFF0EA5E9)
        type.contains("Prescription", ignoreCase = true) -> Color(0xFF7C3AED)
        type.contains("Certificate", ignoreCase = true) -> Color(0xFFF59E0B)
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Color(0xFF6366F1)
        type.contains("Vaccination", ignoreCase = true) -> Color(0xFF16A34A)
        type.contains("Discharge", ignoreCase = true) -> Color(0xFFEF4444)
        type.contains("Bill", ignoreCase = true) -> Color(0xFFEA580C)
        type.contains("Insurance", ignoreCase = true) -> Color(0xFF0891B2)
        type.contains("Note", ignoreCase = true) -> Color(0xFF8B5CF6)
        type.contains("Clearance", ignoreCase = true) -> Color(0xFF14B8A6)
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Color(0xFFE11D48)
        else -> MedGreen
    }
}