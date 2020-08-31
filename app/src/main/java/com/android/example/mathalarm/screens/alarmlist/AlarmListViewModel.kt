package com.android.example.mathalarm.screens.alarmlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.example.mathalarm.database.Alarm
import com.android.example.mathalarm.database.AlarmDao
import com.android.example.mathalarm.database.AlarmRepository
import kotlinx.coroutines.*

class AlarmListViewModel(private val repository: AlarmRepository): ViewModel(){
    var addClicked = MutableLiveData<Boolean?>()
    val alarms = MutableLiveData<List<Alarm>>()

    private val _navigateToAlarmSettings = MutableLiveData<Long>()
    val navigateToAlarmSettings
        get() = _navigateToAlarmSettings


    fun onUpdate(alarm: Alarm){
        viewModelScope.launch {
            repository.update(alarm)
            getAlarms()
        }
    }

    fun getAlarms() {
        viewModelScope.launch {
            val alarmList = repository.getAlarms()
            alarms.postValue(alarmList)
        }
    }

    //Called when add menu is pressed
    fun onAdd(){
        viewModelScope.launch {
            val id = repository.add(Alarm())
            addClicked.value = true
            _navigateToAlarmSettings.value = id
        }
    }

    fun onDelete(alarm: Alarm){
        viewModelScope.launch {
            repository.delete(alarm)
            getAlarms()
        }
    }


    fun onClear(){
        viewModelScope.launch {
            repository.clear()
            getAlarms()
        }
    }


    fun onAlarmClicked(id: Long){
        addClicked.value = false
        _navigateToAlarmSettings.value = id
    }

    fun onAlarmSettingsNavigated(){
        _navigateToAlarmSettings.value = null
    }

}