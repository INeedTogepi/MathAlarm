package com.timilehinaregbesola.mathalarm.framework.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.timilehinaregbesola.mathalarm.data.AlarmRepository
import com.timilehinaregbesola.mathalarm.framework.RoomAlarmDataSource
import com.timilehinaregbesola.mathalarm.framework.Usecases
import com.timilehinaregbesola.mathalarm.framework.database.AlarmDao
import com.timilehinaregbesola.mathalarm.framework.database.AlarmDatabase
import com.timilehinaregbesola.mathalarm.framework.database.AlarmMapper
import com.timilehinaregbesola.mathalarm.framework.database.MIGRATION_2_3
import com.timilehinaregbesola.mathalarm.interactors.AlarmInteractor
import com.timilehinaregbesola.mathalarm.interactors.AlarmInteractorImpl
import com.timilehinaregbesola.mathalarm.notification.AlarmNotificationScheduler
import com.timilehinaregbesola.mathalarm.presentation.alarmlist.AlarmListViewModel
import com.timilehinaregbesola.mathalarm.presentation.alarmmath.AlarmMathViewModel
import com.timilehinaregbesola.mathalarm.presentation.alarmsettings.AlarmSettingsViewModel
import com.timilehinaregbesola.mathalarm.usecases.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AlarmListViewModel(get()) }
    viewModel { AlarmSettingsViewModel(get()) }
    viewModel { AlarmMathViewModel(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): AlarmDatabase {
        return Room.databaseBuilder(
            application,
            AlarmDatabase::class.java,
            "alarm_history_database"
        ).addMigrations(MIGRATION_2_3).build()
    }

    fun provideAlarmDao(database: AlarmDatabase): AlarmDao {
        return database.alarmDatabaseDao
    }

    fun provideInteractors(
        addAlarm: AddAlarm,
        clearAlarms: ClearAlarms,
        deleteAlarm: DeleteAlarm,
        deleteAlarmWithId: DeleteAlarmWithId,
        findAlarm: FindAlarm,
        getAlarms: GetAlarms,
        getLatestAlarm: GetLatestAlarm,
        updateAlarm: UpdateAlarm,
        scheduleAlarm: ScheduleAlarm
    ): Usecases {
        return Usecases(
            addAlarm,
            clearAlarms,
            deleteAlarm,
            deleteAlarmWithId,
            findAlarm,
            getAlarms,
            getLatestAlarm,
            updateAlarm, scheduleAlarm
        )
    }

    fun provideAlarmMapper(): AlarmMapper {
        return AlarmMapper()
    }

    single { provideDatabase(androidApplication()) }
    single { provideAlarmDao(get()) }
    single { provideAlarmMapper() }
    single {
        provideInteractors(
            AddAlarm(get()),
            ClearAlarms(get()),
            DeleteAlarm(get()),
            DeleteAlarmWithId(get()),
            FindAlarm(get()),
            GetAlarms(get()),
            GetLatestAlarm(get()),
            UpdateAlarm(get()),
            ScheduleAlarm(get(), get())
        )
    }
}

val repositoryModule = module {
    fun provideDataSource(alarmDao: AlarmDao, mapper: AlarmMapper): RoomAlarmDataSource {
        return RoomAlarmDataSource(alarmDao, mapper)
    }

    fun provideNotificationScheduler(context: Context): AlarmNotificationScheduler {
        return AlarmNotificationScheduler(context)
    }

    fun provideRepository(alarmDataSource: RoomAlarmDataSource): AlarmRepository {
        return AlarmRepository(alarmDataSource)
    }

    fun provideAlarmInteractor(alarmManager: AlarmNotificationScheduler): AlarmInteractor {
        return AlarmInteractorImpl(alarmManager)
    }

    single { provideDataSource(get(), get()) }
    single { provideNotificationScheduler(androidContext()) }
    single { provideRepository(get()) }
    single { provideAlarmInteractor(get()) }
}
