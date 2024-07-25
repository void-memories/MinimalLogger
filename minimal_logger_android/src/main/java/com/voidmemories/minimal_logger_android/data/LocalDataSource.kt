import android.app.Application
import android.util.Log
import com.voidmemories.minimal_logger_android.utils.QUEUED_LOG_FILE_BASE_NAME
import com.voidmemories.minimal_logger_android.utils.SDK_FOLDER_NAME
import java.io.File
import java.io.FileWriter
import java.io.IOException

class LocalDataSource(private val application: Application) {
    private val latestLogFileName = "m_logger_latest.txt"
    private val logDir: File = File(application.filesDir, SDK_FOLDER_NAME)
    private val latestLogFile = File(logDir, latestLogFileName)

    init {
        if (!logDir.exists()) {
            logDir.mkdir()
        }
    }

    fun combineFilesToByteArray(): ByteArray {
        val combinedContent = StringBuilder()
        var index = 1

        while (true) {
            val fileName = "$QUEUED_LOG_FILE_BASE_NAME$index.txt"
            val file = File(logDir, fileName)

            if (!file.exists()) {
                break
            }

            file.bufferedReader().use { reader ->
                combinedContent.append(reader.readText())
            }
            index++
        }

        return combinedContent.toString().toByteArray()
    }

    fun writeLogToFile(log: String) {
        try {
            FileWriter(latestLogFile, true).buffered().use { writer ->
                writer.append(log)
                writer.newLine()  // Ensure logs are written on separate lines
            }
        } catch (e: IOException) {
            Log.e("LocalDataSource", "Failed to write log to file: ${e.message}", e)
        }
    }

    fun shouldUploadLogs(remoteConfig: RemoteConfig): Boolean {
        if (latestLogFile.length() > remoteConfig.logsUploadThresholdInBytes) {
            var queueFileIndex = 0
            var queueFile: File

            //converting to _queued file type
            do {
                val fileName =
                    "$QUEUED_LOG_FILE_BASE_NAME${if (queueFileIndex > 0) "_$queueFileIndex" else ""}.txt"
                queueFile = File(logDir, fileName)
                queueFileIndex++
            } while (queueFile.exists())

            return latestLogFile.renameTo(queueFile)
        }
        return false
    }

    fun deleteLoggerQueueFiles() {
        val files = logDir.listFiles()

        files?.filter {
            it.name.startsWith(QUEUED_LOG_FILE_BASE_NAME) && it.name.endsWith(".txt")
        }?.forEach { file ->
            if (file.delete()) {
                Log.d("LocalDataSource", "Deleted: ${file.name}")
            } else {
                Log.e("LocalDataSource", "Failed to delete: ${file.name}")
            }
        }
    }
}
