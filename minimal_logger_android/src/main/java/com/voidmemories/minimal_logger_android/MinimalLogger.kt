import android.app.Application
import android.util.Log
import com.voidmemories.minimal_logger_android.utils.LOG_TAG

/**
 * A singleton logger class for minimal logging operations.
 *
 * The `MinimalLogger` class provides static methods for initializing the logger
 * and logging messages with various types. It internally uses the `MainController`
 * class to manage logging operations.
 */
class MinimalLogger {
    companion object {
        // An instance of MainController that manages logging operations
        private val mainController = MainController()

        /**
         * Initializes the logger with the provided configuration.
         *
         * @param idHash A unique identifier used for fetching remote configurations.
         * @param applicationContext The application context required for initialization.
         * @return `true` if initialization was successful, `false` otherwise.
         */
        fun initialize(applicationContext: Application, idHash: String): Boolean {
            return try {
                // Initialize the MainController with application context and idHash
                mainController.initialize(applicationContext, idHash)
            } catch (e: Exception) {
                // Log any exceptions that occur during initialization
                Log.e(LOG_TAG, "Initialization failed: ${e.message}", e)
                false
            }
        }

        /**
         * Logs a message with the specified log type.
         *
         * @param logType The type of log (e.g., ERROR, DEBUG).
         * @param message The message to be logged.
         */
        fun log(logType: LogType, message: String) {
            try {
                // Delegate logging to MainController
                mainController.log(logType, message)
            } catch (e: Exception) {
                // Log any exceptions that occur during logging
                Log.e(LOG_TAG, "Logging failed: ${e.message}", e)
            }
        }

        /**
         * Logs a message represented as a JSON payload with the specified log type.
         *
         * @param logType The type of log (e.g., ERROR, DEBUG).
         * @param payload A map representing the log message as JSON.
         */
        fun log(logType: LogType, payload: Map<String, Any>) {
            try {
                // Convert the payload map to a JSON string and log it
                mainController.log(logType, payload)
            } catch (e: Exception) {
                // Log any exceptions that occur during logging
                Log.e(LOG_TAG, "Logging failed: ${e.message}", e)
            }
        }
    }
}
