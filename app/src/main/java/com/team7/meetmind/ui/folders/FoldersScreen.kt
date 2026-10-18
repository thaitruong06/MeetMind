package com.team7.meetmind.ui.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.team7.meetmind.navigation.Routes
import com.team7.meetmind.ui.common.FolderUi
import com.team7.meetmind.ui.common.MeetMindBottomBar
import com.team7.meetmind.ui.common.MeetMindTopBar
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary

@Composable
fun FoldersScreen(
    onNavigate: (String) -> Unit,
    onRecordClick: () -> Unit,
    onFolderClick: (String) -> Unit
) {
    Scaffold(
        topBar = { MeetMindTopBar(title = "Thư mục") },
        bottomBar = { MeetMindBottomBar(currentRoute = Routes.FOLDERS, onNavigate = onNavigate, onRecordClick = onRecordClick) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(SampleData.folders) { folder -> FolderCard(folder) { onFolderClick(folder.name) } }
        }
    }
}

@Composable
private fun FolderCard(folder: FolderUi, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(18.dp))
            .background(InkSurface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(folder.color.copy(alpha = 0.18f))
        ) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                tint = folder.color,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            folder.name,
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text("${folder.meetingCount} cuộc họp", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
    }
}
