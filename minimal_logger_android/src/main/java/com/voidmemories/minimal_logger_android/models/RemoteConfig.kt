data class RemoteConfig(
    val logDumpUrl: String,
    val logsUploadThresholdInBytes: Long,
    val periodicLogUploadIntervalInHours: Long
)