package com.ses.net

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

typealias HttpProgressHandler = (status: Int, progress: Float) -> Unit

class Http {
    enum class RequestMethod(val value: String) {
        GET("GET"),
        POST("POST")
    }


    var method: RequestMethod = RequestMethod.GET
    lateinit var request: String
    var params: Map<String, String>? = null
    var bufferSize: Int = 8 * 1024
    var timeout: Int = 5000

    private var errorCode = 0
    private var errorMessage: String? = null

    fun getBytes(progressHandler: HttpProgressHandler? = null): ByteArray? {
        try {
            ByteArrayOutputStream().use {
                request(it, progressHandler)
                return it.toByteArray()
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun getString(progressHandler: HttpProgressHandler? = null): String? {
        val bytes = getBytes(progressHandler)
        return if (bytes != null) String(bytes) else null
    }

    fun getFile(file: File, progressHandler: HttpProgressHandler? = null) {
        BufferedOutputStream(file.outputStream()).use {
            request(it, progressHandler)
        }
    }

    fun request(output: OutputStream, progressHandler: HttpProgressHandler? = null) {
        errorCode = -1

        var input: InputStream? = null
        var conn: HttpURLConnection? = null

        try {
            // crear query
            val queryString: String? = if (params != null) {
                val sb = StringBuilder()
                params?.forEach { (key, value) ->
                    if (sb.isNotEmpty()) sb.append('&')
                    val encodedValue = URLEncoder.encode(value, "UTF-8")
                    sb.append(key).append('=').append(encodedValue)
                }
                sb.toString()
            } else {
                null
            }

            val url = if (queryString != null && method == RequestMethod.GET) {
                URL("$request?$queryString")
            } else {
                URL(request)
            }

            // configurar la conexión y conectar
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method.value
                if (queryString != null && method == RequestMethod.POST) {
                    setPOSTParams(this, queryString)
                }
                instanceFollowRedirects = true
                connectTimeout = timeout
                readTimeout = timeout
                connect()
            }

            when (conn.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    // ok
                }

                HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_SEE_OTHER -> {
                    val newUrl: String = conn.getHeaderField("Location")
                    conn = URL(newUrl).openConnection() as HttpURLConnection
                    conn.requestMethod = method.value
                    if (queryString != null && method == RequestMethod.POST) {
                        setPOSTParams(conn, queryString)
                    }
                    conn.connect()
                }

                else -> error(conn.responseCode, conn.responseMessage)
            }

            input = conn.inputStream

            val contentLenght = conn.contentLengthLong

            val buffer = ByteArray(bufferSize)
            var total: Long = 0
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                total += count.toLong()
                output.write(buffer, 0, count)

                val progress: Float = if (contentLenght != -1L) (total.toFloat() / contentLenght) else -1f
                progressHandler?.invoke(0, progress)
            }

            errorCode = 0

            progressHandler?.invoke(0, 1f)

        } catch (e: Exception) {
            errorMessage = e.message

        } finally {
            input?.close()
            conn?.disconnect()
        }
    }

    private fun error(code: Int, message: String) {
        errorCode = code
        errorMessage = message
        println("Error $code: $message")
        throw RuntimeException(message)
    }

    companion object {
        private fun setPOSTParams(conn: HttpURLConnection, queryString: String) {
            with(conn) {
                doOutput = true
                //instanceFollowRedirects = false;
                //useCaches = false;
                //setRequestProperty("Charset", "UTF-8");

                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Content-Length", queryString.length.toString())

                try {
                    DataOutputStream(outputStream).use { stream ->
                        stream.writeBytes(queryString)
                        stream.flush()
                    }
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }
    }
}