package com.android.example.mathalarm.screens.alarmsettings

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.timePicker
import com.android.example.mathalarm.*
import com.android.example.mathalarm.database.Alarm
import com.android.example.mathalarm.database.AlarmDatabase
import com.android.example.mathalarm.databinding.FragmentAlarmSettingsBinding
import com.android.example.mathalarm.screens.alarmmath.AlarmMathActivity
import java.util.*
import kotlin.collections.ArrayList


class AlarmSettingsFragment: Fragment() {

    private lateinit var  binding: FragmentAlarmSettingsBinding
    private lateinit var settingsViewModel: AlarmSettingsViewModel
    private lateinit var mAlarm: Alarm
    private lateinit var mTestAlarm: Alarm

    private var key: Long? = null
    private var isFromAdd: Boolean? = null
    private var mAlarmTones: Array<Uri?> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(this.activity).application
        val args: AlarmSettingsFragmentArgs by navArgs()
        isFromAdd = args.add
        key = args.alarmKey
        val dataSource = AlarmDatabase.getInstance(application).alarmDatabaseDao
        val viewModelFactory = AlarmSettingsViewFactory(args.alarmKey,dataSource)
        settingsViewModel = ViewModelProvider(
            this, viewModelFactory).get(AlarmSettingsViewModel::class.java)

        binding = FragmentAlarmSettingsBinding.inflate(inflater, container, false).apply {
            alarmSettingsViewModel = settingsViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.settingsTestButton.setOnClickListener {
            mTestAlarm = Alarm()
            mTestAlarm.difficulty = binding.settingsMathDifficultySpinner.selectedItemPosition
            if (mAlarmTones.isNotEmpty()) {
                mTestAlarm.alarmTone = (
                        mAlarmTones[binding.settingsToneSpinner.selectedItemPosition].toString())
            }
            mTestAlarm.vibrate = binding.settingsVibrateSwitch.isChecked
            mTestAlarm.snooze = 0
            settingsViewModel.onAdd(mTestAlarm)
        }
    }

    private fun setupObservers() {
        settingsViewModel.alarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let{
                mAlarm = alarm
                var mRepeat = alarm.repeatDays

                binding.settingsTime.text = getFormatTime(mAlarm)
                binding.settingsTime.setOnClickListener {
                    val timeCal = Calendar.getInstance()
                    if (!isFromAdd!!) {
                        timeCal.set(0, 0, 0, alarm.hour, alarm.minute)
                    }
                    MaterialDialog(requireContext()).show {
                        timePicker(currentTime = timeCal, show24HoursView = false) { _, datetime ->
                            alarm.hour = datetime.get(Calendar.HOUR_OF_DAY)
                            alarm.minute = datetime.get(Calendar.MINUTE)
                            settingsViewModel.onUpdate(alarm)
                            binding.settingsTime.text = getFormatTime(alarm)
                            mAlarm = alarm
                        }
                    }
                }

                binding.setRepeatSun.isChecked = mRepeat[SUN] == 'T'
                binding.setRepeatSun.setOnClickListener {
                    binding.setRepeatSun.isChecked = mRepeat[SUN] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[SUN] == 'T') {
                        sb.setCharAt(SUN, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(SUN, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatMon.isChecked = mRepeat[MON] == 'T'
                binding.setRepeatMon.setOnClickListener {
                    binding.setRepeatMon.isChecked = mRepeat[MON] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[MON] == 'T') {
                        sb.setCharAt(MON, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(MON, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatTue.isChecked = mRepeat[TUE] == 'T'
                binding.setRepeatTue.setOnClickListener {
                    binding.setRepeatTue.isChecked = mRepeat[TUE] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[TUE] == 'T') {
                        sb.setCharAt(TUE, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(MON, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatWed.isChecked = mRepeat[WED] == 'T'
                binding.setRepeatWed.setOnClickListener {
                    binding.setRepeatWed.isChecked = mRepeat[WED] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[WED] == 'T') {
                        sb.setCharAt(WED, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(WED, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatThu.isChecked = mRepeat[THU] == 'T'
                binding.setRepeatThu.setOnClickListener {
                    binding.setRepeatMon.isChecked = mRepeat[THU] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[THU] == 'T') {
                        sb.setCharAt(THU, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(THU, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatFri.isChecked = mRepeat[FRI] == 'T'
                binding.setRepeatFri.setOnClickListener {
                    binding.setRepeatMon.isChecked = mRepeat[FRI] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[FRI] == 'T') {
                        sb.setCharAt(FRI, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(FRI, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.setRepeatSat.isChecked = mRepeat[SAT] == 'T'
                binding.setRepeatSat.setOnClickListener {
                    binding.setRepeatMon.isChecked = mRepeat[SAT] != 'T'
                    val sb = StringBuilder(mRepeat)
                    if (mRepeat[SAT] == 'T') {
                        sb.setCharAt(SAT, 'F')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    } else {
                        sb.setCharAt(SAT, 'T')
                        mRepeat = sb.toString()
                        mAlarm.repeatDays = mRepeat
                    }
                }

                binding.settingsRepeatSwitch.isChecked = mAlarm.repeat
                binding.settingsRepeatSwitch.setOnClickListener {
                    binding.settingsRepeatSwitch.isChecked = !mAlarm.repeat
                    mAlarm.repeat = (!mAlarm.repeat)
                }

                //Getting system sound files for tone and displaying in spinner
                val toneItems: MutableList<String> =
                    ArrayList()
                val ringtoneMgr = RingtoneManager(activity)
                ringtoneMgr.setType(RingtoneManager.TYPE_ALARM)
                var alarmsCursor = ringtoneMgr.cursor
                var alarmsCount = alarmsCursor.count

                if (alarmsCount == 0) { //if there are no alarms, use notification sounds
                    ringtoneMgr.setType(RingtoneManager.TYPE_NOTIFICATION)
                    alarmsCursor = ringtoneMgr.cursor
                    alarmsCount = alarmsCursor.count
                    if (alarmsCount == 0) { //if no alarms and notification sounds, finally use ringtones
                        ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE)
                        alarmsCursor = ringtoneMgr.cursor
                        alarmsCount = alarmsCursor.count
                    }
                }

                if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                    Toast.makeText(activity, "No sound files available", Toast.LENGTH_SHORT).show()
                }

                var previousPosition = 0

                //If there are sound files, add them
                if (alarmsCount != 0) {
                    mAlarmTones = arrayOfNulls(alarmsCount)
                    val currentTone: String = mAlarm.alarmTone
                    while (!alarmsCursor.isAfterLast && alarmsCursor.moveToNext()) {
                        val currentPosition = alarmsCursor.position
                        mAlarmTones[currentPosition] = ringtoneMgr.getRingtoneUri(currentPosition)
                        toneItems.add(
                            ringtoneMgr.getRingtone(currentPosition)
                                .getTitle(activity)
                        )
                        if (currentTone == mAlarmTones[currentPosition].toString()) {
                            previousPosition = currentPosition
                        }
                    }
                }

                if (toneItems.isEmpty()) {
                    toneItems.add("Empty")
                }


                val toneAdapter = ArrayAdapter(
                    requireActivity(),
                    android.R.layout.simple_spinner_dropdown_item, toneItems
                )
                binding.settingsToneSpinner.adapter = toneAdapter
                binding.settingsToneSpinner.setSelection(previousPosition)

                val difficultyItems =
                    arrayOf("Easy", "Medium", "Hard")
                val difficultyAdapter = ArrayAdapter(
                    requireActivity(),
                    android.R.layout.simple_spinner_dropdown_item, difficultyItems
                )
                binding.settingsMathDifficultySpinner.adapter = difficultyAdapter
                binding.settingsMathDifficultySpinner.setSelection(mAlarm.difficulty)

                binding.settingsSnoozeText.setText(java.lang.String.format(Locale.US, "%d", mAlarm.snooze))
                binding.settingsSnoozeText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s.isNotEmpty()) {
                            mAlarm.snooze = (s.toString().toInt())
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                })

                binding.settingsVibrateSwitch.isChecked = mAlarm.vibrate
                binding.settingsVibrateSwitch.setOnClickListener {
                    binding.settingsVibrateSwitch.isChecked = !mAlarm.vibrate
                    mAlarm.vibrate = (!mAlarm.vibrate)
                }


            }
        })

        settingsViewModel.navigateToAlarmMath.observe(viewLifecycleOwner, { alarmId ->
            alarmId?.let {
                val test = Intent(requireContext(), AlarmMathActivity::class.java)
                test.putExtra(ALARM_EXTRA, alarmId.toString())
                startActivity(test)
                settingsViewModel.onDelete(settingsViewModel.latestAlarm.value!!)
                settingsViewModel.onAlarmMathNavigated()
            }
        })
    }

    private fun scheduleAndMessage() { //schedule it and create a toast
        if (scheduleAlarm(requireActivity(), mAlarm)) {
            Toast.makeText(
                activity, getTimeLeftMessage(requireContext(), mAlarm),
                Toast.LENGTH_SHORT
            ).show()
            mAlarm.isOn = true
        } else {
            mAlarm.isOn = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.alarm_settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fragment_settings_done -> {
                //Setting difficulty + alarm tone
                mAlarm.difficulty =(binding.settingsMathDifficultySpinner.selectedItemPosition)
                if (mAlarmTones.isNotEmpty()) {
                    mAlarm.alarmTone = (
                        mAlarmTones[binding.settingsToneSpinner
                            .selectedItemPosition].toString()
                    )
                }
                //schedule alarm, update to database and close settings
                if (isFromAdd!!) {
                    scheduleAndMessage()
                    settingsViewModel.onUpdate(mAlarm)
                } else {
                    if (mAlarm.isOn) {
                        cancelAlarm(requireContext(), mAlarm)
                    }
                    scheduleAndMessage()
                    settingsViewModel.onUpdate(mAlarm)
                }
                findNavController().popBackStack()
                true
            }
            R.id.fragment_settings_delete -> {
                if (mAlarm.isOn) {
                    cancelAlarm(requireContext(), mAlarm)
                }
                settingsViewModel.onDelete(mAlarm)

                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}