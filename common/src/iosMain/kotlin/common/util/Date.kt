package common.util

actual object Date {
    actual val currentTimestamp: Timestamp
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val day: Timestamp
        get() = 86400

    actual fun format(timestamp: Timestamp, format: String): String {
        TODO("not implemented")
    }

    actual fun parse(date: String, format: String): Timestamp {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}