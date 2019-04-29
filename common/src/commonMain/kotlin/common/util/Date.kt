package common.util

import com.soywiz.klock.DateException
import com.soywiz.klock.days
import com.soywiz.klock.format
import com.soywiz.klock.parse

interface DateTime {
    val unixMillis: Double
    fun now(): DateTime
    fun plus(days: Int): DateTime
}

interface DateFormat {
    fun format(dateTime: DateTime): String
    fun parse(dateString: String): DateTime
}

class KlockDateTime(override val unixMillis: Double) : DateTime {
    constructor() : this(0.0)

    private val dateTime = com.soywiz.klock.DateTime(unixMillis)

    override fun now(): DateTime = KlockDateTime(com.soywiz.klock.DateTime.now().unixMillis)
    override fun plus(days: Int): DateTime = KlockDateTime((dateTime + days.days).unixMillis)
}

class KlockDateFormat(pattern: String) : DateFormat {
    private val dateFormat = com.soywiz.klock.DateFormat(pattern)

    override fun format(dateTime: DateTime): String = dateFormat.format(com.soywiz.klock.DateTime(dateTime.unixMillis))

    override fun parse(dateString: String): DateTime {
        try {
            return KlockDateTime(dateFormat.parse(dateString).utc.unixMillis)
        } catch (e: DateException) {
            throw IllegalArgumentException("No date provided")
        }
    }
}