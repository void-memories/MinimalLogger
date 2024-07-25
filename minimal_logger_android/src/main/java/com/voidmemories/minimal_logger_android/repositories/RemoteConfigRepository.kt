import android.app.Application
import com.voidmemories.minimal_logger_android.data.PreferencesManager
import com.voidmemories.minimal_logger_android.utils.REMOTE_CONFIG_PREF_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RemoteConfigRepository(private val application: Application) {

    private val httpConnectionManager = HttpConnectionManager()
    private val preferencesManager = PreferencesManager(application)

    suspend fun fetchRemoteConfig(idHash: String): Result<RemoteConfig> {
        return try {
            val cachedRemoteConfig = preferencesManager.getString(REMOTE_CONFIG_PREF_KEY)

            if (cachedRemoteConfig == null) {
                val latestConfigString = getLatestRemoteConfig(idHash)
                    ?: throw IOException("Unable to fetch config from remote")
                preferencesManager.saveString(REMOTE_CONFIG_PREF_KEY, latestConfigString)
                RemoteConfig.parseRemoteConfig(latestConfigString)
            } else {
                updateCacheInBackground(idHash)
                RemoteConfig.parseRemoteConfig(cachedRemoteConfig)
            }?.let {
                Result.success(it)
            } ?: Result.failure(IOException("Config parsing failed"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getLatestRemoteConfig(idHash: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                httpConnectionManager.sendRequest("GET", "?idHash=$idHash")
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun updateCacheInBackground(idHash: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val latestConfigString = getLatestRemoteConfig(idHash)
                if (latestConfigString != null) {
                    preferencesManager.saveString(REMOTE_CONFIG_PREF_KEY, latestConfigString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
