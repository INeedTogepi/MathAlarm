package com.timilehinaregbesola.mathalarm.interactors

import com.timilehinaregbesola.mathalarm.data.AlarmRepository

class GetLatestAlarm(private val alarmRepository: AlarmRepository) {
    suspend operator fun invoke() = alarmRepository.getLatestAlarmFromDatabase()
}
