package common.util

import com.soywiz.klock.DateException
import com.soywiz.klock.days
import com.soywiz.klock.format
import com.soywiz.klock.parse

/**
 * An abstract DateTime containing a unix timestamp.
 */
interface DateTime {
    val unixMillis: Double
    fun now(): DateTime
    fun plus(days: Int): DateTime
}

/**
 * An abstract DateFormat for formatting and parsing DateTime objects.
 */
interface DateFormat {
    fun format(dateTime: DateTime): String
    fun parse(dateString: String): DateTime
}

/**
 * An implementation of DateTime by using the Klock library.
 */
class KlockDateTime(override val unixMillis: Double) : DateTime {
    constructor() : this(0.0)

    private val dateTime = com.soywiz.klock.DateTime(unixMillis)

    override fun now(): DateTime = KlockDateTime(com.soywiz.klock.DateTime.now().unixMillis)
    override fun plus(days: Int): DateTime = KlockDateTime((dateTime + days.days).unixMillis)
}

/**
 * An implementation of DateFormat by using the Klock library.
 */
class KlockDateFormat(pattern: String) : DateFormat {
    private val dateFormat = com.soywiz.klock.DateFormat(pattern)

    override fun format(dateTime: DateTime): String = dateFormat.format(com.soywiz.klock.DateTime(dateTime.unixMillis))

    override fun parse(dateString: String): DateTime {
        try {
            return KlockDateTime(dateFormat.parse(dateString).utc.unixMillis)
        } catch (e: DateException) {
            throw IllegalArgumentException("Date could not be parsed")
        }
    }
}