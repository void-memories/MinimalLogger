import android.app.Application
import android.util.Log
import com.voidmemories.minimal_logger_android.R
import com.voidmemories.minimal_logger_android.utils.LOG_TAG
import com.voidmemories.minimal_logger_android.utils.SDK_FOLDER_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class MainController {
    private var isInitialized = false
    private lateinit var remoteConfig: RemoteConfig
    private lateinit var application: Application
    private lateinit var logRepository: LogRepository

    fun initialize(idHash: String, applicationContext: Application): Boolean {
        if (isInitialized) {
            Log.e(
                LOG_TAG,
                application.getString(R.string.error_initialized)
            )
        } else {
            application = applicationContext
            if (!createInternalStorageDirectory()) {
                return false
            }

            val remoteConfigRepository = RemoteConfigRepository()
            remoteConfig = remoteConfigRepository.fetchRemoteConfig(idHash)
            logRepository = LogRepository(application, remoteConfig)

            //make a static log with metadata
            return true
        }

        return false
    }

    fun log(logType: LogType, message: String) {
        if (!isInitialized) {
            Log.e(
                LOG_TAG,
                application.getString(R.string.error_uninitialized)
            )
            return
        }

        when (logType) {
            LogType.ERROR -> Log.e(LOG_TAG, message)
            LogType.DEBUG -> Log.d(LOG_TAG, message)
            LogType.VERBOSE -> Log.v(LOG_TAG, message)
            else -> Log.i(LOG_TAG, message)
        }

        processLog(logType, message)
    }

    fun log(logType: LogType, payload: Map<String, Any>) {
        log(logType, JSONObject(payload).toString())
    }

    private fun processLog(logType: LogType, message: String) {
        //TODO: handle scope life
        CoroutineScope(Dispatchers.IO).launch {
            logRepository.writeLogToFile(createLogLine(logType, message))

            if (logRepository.shouldUploadLogs() && logRepository.uploadLogs()) {
                logRepository.deleteLoggerQueueFiles()
            }
        }
    }

    private fun createLogLine(logType: LogType, message: String): String {
        return "${System.currentTimeMillis()} | $logType | $message"
    }

    private fun createInternalStorageDirectory(): Boolean {
        val directory = File(application.filesDir, SDK_FOLDER_NAME)

        if (!directory.exists()) {
            val success = directory.mkdir()
            if (!success) {
                Log.e(LOG_TAG, "Failed to create directory")
                return false
            }
        } else {
            println("Directory already exists")
        }

        return true
    }
}