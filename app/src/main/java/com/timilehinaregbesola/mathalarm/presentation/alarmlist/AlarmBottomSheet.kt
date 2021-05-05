package com.timilehinaregbesola.mathalarm.presentation.alarmlist

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import com.timilehinaregbesola.mathalarm.domain.model.Alarm
import com.timilehinaregbesola.mathalarm.presentation.components.RingDayChip
import com.timilehinaregbesola.mathalarm.presentation.ui.unSelectedDay
import com.timilehinaregbesola.mathalarm.utils.days
import com.timilehinaregbesola.mathalarm.utils.getFormatTime
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.datetime.timepicker.timepicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

@ExperimentalMaterialApi
@Composable
fun AlarmBottomSheet(
    fromAdd: Boolean,
    activeAlarm: Alarm?,
    viewModel: AlarmListViewModel,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
//    scaffoldState.bottomSheetState.progress

    val activity = LocalContext.current as Activity
    var timeCal = LocalTime.now()
    if (!fromAdd) {
        timeCal = timeCal.withHour(activeAlarm!!.hour).withMinute(activeAlarm.minute)
    }
    val dialog = remember { MaterialDialog() }
    dialog.build {
        timepicker(initialTime = timeCal) { time ->
            activeAlarm!!.hour = time.hour
            activeAlarm.minute = time.minute
            viewModel.onUpdate(activeAlarm)
        }
        buttons {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    }

    Column(
        Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 16.dp),
            backgroundColor = unSelectedDay,
            elevation = 0.dp,
            shape = MaterialTheme.shapes.medium.copy(CornerSize(24.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = {
                            dialog.show()
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    text = if (fromAdd) activeAlarm?.getFormatTime().toString() else "Dummy AM",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                if (activeAlarm != null) {
                    val sb = StringBuilder(activeAlarm.repeatDays)
                    val sel = activeAlarm.repeatDays[index] == 'T'
                    val checkedState = remember { mutableStateOf(sel) }
                    RingDayChip(
                        day = day,
                        selected = checkedState.value,
                        onSelectChange = {
                            checkedState.value = it
                            if (it) {
                                sb.setCharAt(index, 'T')
                                activeAlarm.repeatDays = sb.toString()
                                viewModel.onUpdate(activeAlarm)
                            } else {
                                sb.setCharAt(index, 'F')
                                activeAlarm.repeatDays = sb.toString()
                                viewModel.onUpdate(activeAlarm)
                            }
                        }
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 17.dp, start = 16.dp, end = 16.dp),
            thickness = 10.dp,
            color = unSelectedDay
        )
        Row(
            modifier = Modifier
                .padding(top = 38.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(end = 38.dp)) {
                val repeatWeekCheckboxState = remember { mutableStateOf(true) }
                Checkbox(
                    modifier = Modifier.padding(end = 14.dp),
                    checked = repeatWeekCheckboxState.value,
                    onCheckedChange = { repeatWeekCheckboxState.value = it }
                )
                Text(
                    text = "Repeat Weekly",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Row {
                val vibrateCheckboxState = remember { mutableStateOf(true) }
                Checkbox(
                    modifier = Modifier.padding(end = 14.dp),
                    checked = vibrateCheckboxState.value,
                    onCheckedChange = { vibrateCheckboxState.value = it }
                )
                Text(
                    text = "Vibrate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 14.dp),
                imageVector = Icons.Outlined.Label,
                contentDescription = null
            )
            Text(
                text = "Good Morning",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 14.dp),
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            try {
                                startActivityForResult(
                                    activity,
                                    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, activeAlarm!!.alarmTone)

                                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))

                                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                                    },
                                    42, null
                                )
                            } catch (e: Exception) {
//                                Toast.makeText(context, requireContext().getString(R.string.details_no_ringtone_picker), Toast.LENGTH_LONG)
//                                    .show()
                            }
                        }
                    ),
                text = if (activeAlarm?.alarmTone == "") {
                    "Alarm Tone (Default)"
                } else {
                    RingtoneManager.getRingtone(activity, activeAlarm?.alarmTone?.toUri()).getTitle(activity)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 14.dp),
                imageVector = Icons.Outlined.EmojiSymbols,
                contentDescription = null
            )
            Difficulty()
        }
        Button(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            onClick = {
                if (activeAlarm != null) {
//                    navController.navigate(Navigation.buildAlarmMathPath(alarmId = activeAlarm.alarmId))
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = unSelectedDay,
                contentColor = Color.Black
            )
        ) {
            Text(
                fontSize = 14.sp,
                text = "TEST ALARM"
            )
        }
        Button(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.getAlarms()
                scope.launch {
                    scaffoldState.bottomSheetState.collapse()
                }
            }
        ) {
            Text(
                fontSize = 14.sp,
                text = "SAVE"
            )
        }
    }
}

@Composable
fun Difficulty() {
    var expanded = remember { mutableStateOf(false) }
    val items = listOf("Easy Math", "Medium Math", "Hard Math")
    var selectedIndex = remember { mutableStateOf(0) }
    Box {
        Text(
            items[selectedIndex.value],
            modifier = Modifier
                .clickable(onClick = { expanded.value = true })
                .background(unSelectedDay)
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex.value = index
                        expanded.value = false
                    }
                ) {
                    Text(text = s)
                }
            }
        }
    }
}