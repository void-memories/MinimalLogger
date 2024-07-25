import java.io.IOException

class RemoteConfigRepository {
    private val remoteDataSource = RemoteDataSource()

    fun fetchRemoteConfig(idHash: String): Result<RemoteConfig> {
        return runCatching {
            val response = remoteDataSource.sendRequest("GET", "?idHash=$idHash")
                ?: throw IOException("Response is null or invalid")

            RemoteConfig.parseRemoteConfig(response)
                ?: throw IOException("Error parsing JSON response")
        }
    }
}
