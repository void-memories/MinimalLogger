import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpConnectionManager {
    fun sendRequest(method: String, urlString: String, jsonBody: String? = null): String? {
        var connection: HttpURLConnection? = null

        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.setRequestProperty("Content-Type", "application/json")

            if (method == "POST" && jsonBody != null) {
                connection.doOutput = true
                val outputStream: OutputStream = connection.outputStream
                outputStream.write(jsonBody.toByteArray())
                outputStream.flush()
                outputStream.close()
            }

            // Get the response code
            val responseCode = connection.responseCode
            Log.d("HTTP Response", "Response Code: $responseCode")

            // Read the response
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.use { it.readText() }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("HTTP Request Error", "Error: ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }
}