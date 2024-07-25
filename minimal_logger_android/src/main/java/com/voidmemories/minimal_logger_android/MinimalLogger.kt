import android.app.Application
import android.util.Log
import com.voidmemories.minimal_logger_android.utils.LOG_TAG

class MinimalLogger {
    companion object {
        val mainController = MainController()

        fun initialize(idHash: String, applicationContext: Application): Boolean {
            return try {
                mainController.initialize(idHash, applicationContext)
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.toString())
                false
            }
        }

        fun log(logType: LogType, message: String) {
            try {
                mainController.log(logType, message)
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.toString())
            }
        }

        fun log(logType: LogType, payload: Map<String, Any>) {
            try {
                mainController.log(logType, payload)
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.toString())
            }
        }
    }
}