data class RemoteConfig(
    private val logDumpUrl:String,
    private val logsUploadThresholdInBytes:Long,
    private val periodicLogUploadIntervalInHours:Long
)