import android.app.Application
import android.util.Log
import com.voidmemories.minimal_logger_android.R
import com.voidmemories.minimal_logger_android.utils.LOG_TAG
import com.voidmemories.minimal_logger_android.utils.SDK_FOLDER_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class MainController {
    private var isInitialized = false
    private lateinit var remoteConfig: RemoteConfig
    private lateinit var logRepository: LogRepository
    private lateinit var application: Application
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(applicationContext: Application, idHash: String): Boolean {
        if (isInitialized) {
            Log.e(LOG_TAG, applicationContext.getString(R.string.error_initialized))
            return false
        }

        application = applicationContext
        if (!createInternalStorageDirectory()) return false

        val result = RemoteConfigRepository().fetchRemoteConfig(idHash)
        return when {
            result.isSuccess -> {
                remoteConfig = result.getOrThrow()
                logRepository = LogRepository(application, remoteConfig)
                isInitialized = true
                true
            }
            else -> {
                Log.e(
                    LOG_TAG,
                    "Failed to fetch remote configuration: ${result.exceptionOrNull()?.message}"
                )
                false
            }
        }
    }

    fun log(logType: LogType, message: String) {
        if (!isInitialized) {
            Log.e(LOG_TAG, application.getString(R.string.error_uninitialized))
            return
        }

        when (logType) {
            LogType.INFO -> Log.i(LOG_TAG, message)
            LogType.ERROR -> Log.e(LOG_TAG, message)
            LogType.DEBUG -> Log.d(LOG_TAG, message)
            LogType.VERBOSE -> Log.v(LOG_TAG, message)
            LogType.EVENT -> Log.i(
                LOG_TAG,
                "EVENT: $message"
            )
        }

        processLog(logType, message)
    }

    fun log(logType: LogType, payload: Map<String, Any>) =
        log(logType, JSONObject(payload).toString())

    private fun processLog(logType: LogType, message: String) {
        coroutineScope.launch {
            try {
                logRepository.writeLogToFile(createLogLine(logType, message))
                if (logRepository.shouldUploadLogs() && logRepository.uploadLogs()) {
                    logRepository.deleteLoggerQueueFiles()
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error processing log: ${e.message}", e)
            }
        }
    }

    private fun createLogLine(logType: LogType, message: String) =
        "${System.currentTimeMillis()} | $logType | $message"

    private fun createInternalStorageDirectory(): Boolean {
        val directory = File(application.filesDir, SDK_FOLDER_NAME)
        return if (!directory.exists() && directory.mkdir()) {
            Log.d(LOG_TAG, "Directory created successfully")
            true
        } else {
            Log.d(LOG_TAG, "Directory already exists or failed to create")
            false
        }
    }
}
