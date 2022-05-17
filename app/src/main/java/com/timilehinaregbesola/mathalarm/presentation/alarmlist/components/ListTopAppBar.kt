package com.timilehinaregbesola.mathalarm.presentation.alarmlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.timilehinaregbesola.mathalarm.utils.TestTags

@Composable
fun ListTopAppBar(
    openDialog: MutableState<Boolean>,
    onSettingsClick: () -> Unit
) {
    val showSettings = rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Alarms", fontSize = 16.sp)
            }
        },
        actions = {
            IconButton(onClick = { openDialog.value = true }) {
                Icon(imageVector = Icons.Outlined.DeleteSweep, contentDescription = null)
            }
            IconButton(onClick = { onSettingsClick.invoke() }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Preview
@Composable
fun AppBarPreview() {
    ListTopAppBar(
        openDialog = remember { mutableStateOf(false) }
    ) {}
}
