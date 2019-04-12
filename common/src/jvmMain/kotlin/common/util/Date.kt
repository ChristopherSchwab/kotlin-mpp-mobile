package common.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

actual object Date {
    actual val currentTimestamp: Timestamp
        get() = Date().time
    actual val day: Timestamp
        get() = 86400000

    actual fun format(timestamp: Timestamp, format: String): String {
        return try {
            SimpleDateFormat(format, Locale.ENGLISH).format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }

    actual fun parse(date: String, format: String): Timestamp {
        return try {
            SimpleDateFormat(format, Locale.ENGLISH).parse(date).time
        } catch (e: Exception) {
            0
        }
    }
}