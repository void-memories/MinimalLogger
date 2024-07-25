import android.app.Application

class LogRepository(application: Application, private val remoteConfig: RemoteConfig) {
    private val logFileManager = LogFileManager(application)
    private val httpConnectionManager = HttpConnectionManager()

    fun shouldUploadLogs() = logFileManager.shouldUploadLogs(remoteConfig)

    fun uploadLogs(): Boolean {
        val body = logFileManager.combineFilesToByteArray().toString()
        return httpConnectionManager.sendRequest("POST", remoteConfig.logDumpUrl, body) != null
    }

    fun writeLogToFile(logLine: String) = logFileManager.writeLogToFile(logLine)

    fun deleteLoggerQueueFiles() = logFileManager.deleteLoggerQueueFiles()
}