package net.halawata.gone.service

import android.content.Context
import android.net.Uri
import androidx.loader.content.AsyncTaskLoader
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class AsyncNetworkTaskLoader(context: Context, private val urlString: String, private val method: String, private val params: Map<String, String>? = null) : AsyncTaskLoader<AsyncNetworkTaskLoader.Response>(context) {

    private var isLoading = false
    private var result: Response? = null

    override fun onStartLoading() {
        result?.let {
            deliverResult(it)
            return
        }

        if (!isLoading || takeContentChanged()) {
            forceLoad()
        }
    }

    override fun deliverResult(data: Response?) {
        result = data
        super.deliverResult(data)
    }

    override fun onForceLoad() {
        super.onForceLoad()
        isLoading = true
    }

    override fun loadInBackground(): Response {
        var connection: HttpURLConnection? = null
        var responseCode: Int? = null
        var content: String? = ""

        try {
            val request = buildRequest()

            val url = URL(request.urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method

            // POST の場合のパラメータ
            if (method == "POST") {
                val printWriter = PrintWriter(connection.outputStream)
                printWriter.print(request.params)
                printWriter.close()
            }

            responseCode = connection.responseCode

            val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))

            var line = reader.readLine()
            while (line != null) {
                content += line
                line = reader.readLine()
            }

            reader.close()

        } catch (ex: FileNotFoundException) {
            // 400, 500 系はこの例外で飛んでくる
            val reader = BufferedReader(InputStreamReader(connection?.errorStream, "UTF-8"))

            var line = reader.readLine()
            while (line != null) {
                content += line
                line = reader.readLine()
            }

            reader.close()
            ex.printStackTrace()

        } catch (ex: IOException) {
            content = null
            ex.printStackTrace()

        } finally {
            connection?.disconnect()
        }

        return Response(responseCode, content)
    }

    private fun buildRequest(): Request {
        val builder = Uri.Builder()

        params?.forEach { (k, v) ->
            builder.appendQueryParameter(k, v)
        }

        val paramsString = builder.build().toString()

        return if (method == "POST") {
            Request(urlString, "POST", paramsString.drop(1))

        } else {
            Request(urlString + paramsString, "GET", null)
        }
    }

    data class Request(val urlString: String, val method: String, val params: String?)

    data class Response(val responseCode: Int?, val content: String?)
}
