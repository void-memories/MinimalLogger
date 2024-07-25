class RemoteConfigRepository {
    val remoteDataSource = RemoteDataSource()
    fun fetchRemoteConfig(idHash: String): RemoteConfig {
        //TODO: get remote config url
        val response = remoteDataSource.sendRequest("GET", "?idHash=$idHash")
    }
}