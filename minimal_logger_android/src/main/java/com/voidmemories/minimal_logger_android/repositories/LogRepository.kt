import android.app.Application

class LogRepository(application: Application, private val remoteConfig: RemoteConfig) {
    private val localDataSource = LocalDataSource(application)
    private val remoteDataSource = RemoteDataSource()

    fun shouldUploadLogs() = localDataSource.shouldUploadLogs(remoteConfig)

    fun uploadLogs(): Boolean {
        val body = localDataSource.combineFilesToByteArray().toString()
        return remoteDataSource.sendRequest("POST", remoteConfig.logDumpUrl, body) != null
    }

    fun writeLogToFile(logLine: String) = localDataSource.writeLogToFile(logLine)

    fun deleteLoggerQueueFiles() = localDataSource.deleteLoggerQueueFiles()
}