import android.util.Log
import com.voidmemories.minimal_logger_android.utils.LOG_TAG
import org.json.JSONObject

data class RemoteConfig(
    val logDumpUrl: String,
    val logsUploadThresholdInBytes: Long,
    val periodicLogUploadIntervalInHours: Long
) {
    companion object {
        fun parseRemoteConfig(jsonString: String): RemoteConfig? {
            try {
                val jsonObject = JSONObject(jsonString)
                return RemoteConfig(
                    jsonObject.getString("logDumpUrl"),
                    jsonObject.getLong("logsUploadThresholdInBytes"),
                    jsonObject.getLong("periodicLogUploadIntervalInHours")
                )
            } catch (e: java.lang.Exception) {
                Log.e(LOG_TAG, e.toString())
            }

            return null
        }
    }
}