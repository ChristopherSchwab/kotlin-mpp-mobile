package common.util

typealias Timestamp = Long

expect object Date {
    val currentTimestamp: Timestamp
    val day: Timestamp

    fun format(timestamp: Timestamp, format: String): String
    fun parse(date: String, format: String): Timestamp
}