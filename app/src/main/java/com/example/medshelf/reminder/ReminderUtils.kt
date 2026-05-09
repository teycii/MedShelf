package com.example.medshelf.reminder

import com.example.medshelf.model.ReminderEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

object ReminderUtils {

    const val SCHEDULE_DATE_TIME = "DATE_TIME"
    const val SCHEDULE_INTERVAL = "INTERVAL"

    const val STATUS_SCHEDULED = "Scheduled"
    const val STATUS_COMPLETED = "Completed"

    fun calculateNextTriggerMillis(reminder: ReminderEntity): Long {
        return calculateNextTriggerMillis(
            date = reminder.date,
            time = reminder.time,
            scheduleType = reminder.scheduleType,
            intervalHours = reminder.intervalHours,
            repeat = reminder.repeat
        )
    }

    fun calculateNextTriggerMillis(
        date: String,
        time: String,
        scheduleType: String,
        intervalHours: Int,
        repeat: String
    ): Long {
        val baseMillis = parseDateTimeMillis(date, time)
        val now = System.currentTimeMillis()

        if (baseMillis <= 0L) return 0L

        return if (scheduleType == SCHEDULE_INTERVAL) {
            calculateIntervalNextTrigger(
                baseMillis = baseMillis,
                intervalHours = intervalHours,
                now = now
            )
        } else {
            calculateDateTimeNextTrigger(
                baseMillis = baseMillis,
                repeat = repeat,
                now = now
            )
        }
    }

    private fun calculateIntervalNextTrigger(
        baseMillis: Long,
        intervalHours: Int,
        now: Long
    ): Long {
        if (intervalHours <= 0) return 0L
        if (baseMillis > now) return baseMillis

        val intervalMillis = intervalHours * 60L * 60L * 1000L
        val passedIntervals = max(0L, (now - baseMillis) / intervalMillis)

        return baseMillis + ((passedIntervals + 1) * intervalMillis)
    }

    private fun calculateDateTimeNextTrigger(
        baseMillis: Long,
        repeat: String,
        now: Long
    ): Long {
        if (baseMillis > now) return baseMillis

        if (repeat == "Once") return 0L

        val calendar = Calendar.getInstance().apply {
            timeInMillis = baseMillis
        }

        while (calendar.timeInMillis <= now) {
            when (repeat) {
                "Daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "Monthly" -> calendar.add(Calendar.MONTH, 1)
                else -> return 0L
            }
        }

        return calendar.timeInMillis
    }

    fun parseDateTimeMillis(
        date: String,
        time: String
    ): Long {
        return try {
            val formatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
            formatter.parse("$date $time")?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    fun isCompletedForCurrentCycle(reminder: ReminderEntity): Boolean {
        if (reminder.lastCompletedAtMillis <= 0L) return false

        val now = System.currentTimeMillis()
        val lastDone = reminder.lastCompletedAtMillis

        return when {
            reminder.scheduleType == SCHEDULE_INTERVAL && reminder.intervalHours > 0 -> {
                val intervalMillis = reminder.intervalHours * 60L * 60L * 1000L
                now - lastDone < intervalMillis
            }

            reminder.repeat == "Daily" -> isSameDay(lastDone, now)

            reminder.repeat == "Weekly" -> isSameWeek(lastDone, now)

            reminder.repeat == "Monthly" -> isSameMonth(lastDone, now)

            else -> false
        }
    }

    private fun isSameDay(firstMillis: Long, secondMillis: Long): Boolean {
        val first = Calendar.getInstance().apply { timeInMillis = firstMillis }
        val second = Calendar.getInstance().apply { timeInMillis = secondMillis }

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
    }

    private fun isSameWeek(firstMillis: Long, secondMillis: Long): Boolean {
        val first = Calendar.getInstance().apply { timeInMillis = firstMillis }
        val second = Calendar.getInstance().apply { timeInMillis = secondMillis }

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.WEEK_OF_YEAR) == second.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isSameMonth(firstMillis: Long, secondMillis: Long): Boolean {
        val first = Calendar.getInstance().apply { timeInMillis = firstMillis }
        val second = Calendar.getInstance().apply { timeInMillis = secondMillis }

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.MONTH) == second.get(Calendar.MONTH)
    }
}