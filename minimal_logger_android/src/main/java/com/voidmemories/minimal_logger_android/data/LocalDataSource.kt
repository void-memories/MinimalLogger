import android.app.Application
import com.voidmemories.minimal_logger_android.utils.QUEUED_LOG_FILE_BASE_NAME
import com.voidmemories.minimal_logger_android.utils.SDK_FOLDER_NAME
import java.io.File
import java.io.FileWriter
import java.io.IOException

class LocalDataSource(private val application: Application) {
    val latestLogFileName = "m_logger_latest.txt"
    val logDir: File = File(application.filesDir, SDK_FOLDER_NAME)
    val latestLogFile = File(logDir, latestLogFileName)

    fun combineFilesToByteArray(): ByteArray {
        val combinedContent = StringBuilder()

        var index = 1
        while (true) {
            val fileName = "m_logger_latest$index"
            val file = File(application.filesDir, fileName)

            if (!file.exists()) {
                break
            }

            val content = file.readText()
            combinedContent.append(content)
            index++
        }

        return combinedContent.toString().toByteArray()
    }

    fun writeLogToFile(log: String) {

        // Get the directory for the minimalLogger folder
        val logDir: File = File(application.filesDir, SDK_FOLDER_NAME)
        if (!logDir.exists()) {
            logDir.mkdir()
        }

        // Create the latest log file if it doesn't exist
        val latestLogFile = File(logDir, latestLogFileName)
        if (!latestLogFile.exists()) {
            latestLogFile.createNewFile()
        }

        // Append text to the latest log file
        try {
            FileWriter(latestLogFile, true).use { writer ->
                writer.append(log)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    fun shouldUploadLogs(remoteConfig: RemoteConfig): Boolean {
        if (latestLogFile.length() > remoteConfig.logsUploadThresholdInBytes) {
            var queueFileIndex = 0
            var queueFile: File

            // Find the next available queue file name
            do {
                queueFile = File(
                    logDir,
                    "$QUEUED_LOG_FILE_BASE_NAME${if (queueFileIndex > 0) "_$queueFileIndex" else ""}.txt"
                )
                queueFileIndex++
            } while (queueFile.exists())

            latestLogFile.renameTo(queueFile)

            return true
        }

        return false
    }

    fun deleteLoggerQueueFiles() {
        val directory = application.filesDir

        val files = directory.listFiles()

        files?.forEach { file ->
            if (file.name.startsWith(QUEUED_LOG_FILE_BASE_NAME) && file.name.endsWith(".txt")) {
                val deleted = file.delete()
                if (deleted) {
                    println("Deleted: ${file.name}")
                } else {
                    println("Failed to delete: ${file.name}")
                }
            }
        }
    }
}