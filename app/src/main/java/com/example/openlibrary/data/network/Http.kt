package com.example.openlibrary.data.network

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import java.util.*

object Http {
    internal const val TAG = "Http"

    enum class RequestMethod {
        GET, POST, DELETE, PUT
    }

    internal var reqTimeStamp: Long = 0

    /**
     * prepare request class by builder pattern
     *
     * @param  method   request method
     * @return         request object prepared with builder pattern
     */
    class Request(internal val method: String) {
        private val query: MutableMap<String, String> = HashMap()
        internal val header: MutableMap<String, String> = HashMap()
        internal var uri: String? = null
        internal var body: ByteArray? = null
        internal var loggingEnabled = false

        fun enableLog(enableLogging: Boolean): Request {
            HttpLogger.isLogsRequired = enableLogging
            loggingEnabled = enableLogging
            return this
        }

        fun url(uri: String?): Request {
            this.uri = uri
            return this
        }

        fun query(queryMap: Map<String, String>?): Request {
            query.putAll(queryMap!!)
            return this
        }

        fun header(header: Map<String, String>?): Request {
            this.header.putAll(header!!)
            return this
        }

        /**
         * execute the prepared request
         *
         * @return         api response which is success or error
         */
        fun execute(): Response {
            reqTimeStamp = System.currentTimeMillis()
            return RequestTask(this).run()
        }

        /**
         * prepare query string to be appended to url
         *
         * @return         query string
         */
        internal fun getQueryString(): String {
            if (query.isEmpty()) return ""
            val result = StringBuilder("?")
            var index = 0;
            for ((key, value) in query) {
                try {
                    result.append(URLEncoder.encode(key, UTF_8))
                    result.append("=")
                    result.append(URLEncoder.encode(value, UTF_8))
                    if (index != query.size - 1) {
                        result.append("&")
                    }
                } catch (e: Exception) { /* This should never happen */
                    e.printStackTrace()
                }
                index++
            }
            return result.toString()
        }
    }

    internal class RequestTask(private val req: Request) {

        /**
         * run call
         * @return         api response which is success or error
         */
        fun run() : Response {
            return try {
                val conn = request()
                parseResponse(conn)
            } catch (e: IOException) {
                e.printStackTrace()
                Response(null, null, e.message ?: "", null, e)
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                Response(null, null, e.message ?: "", null, e)
            }
        }

        /**
         * prepare request connection using [HttpURLConnection]
         *
         * @return         [HttpURLConnection] request
         */
        @Throws(IOException::class, SocketTimeoutException::class)
        private fun request(): HttpURLConnection {
            val url = URL(req.uri + req.getQueryString())
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            conn.doInput = true
            conn.connectTimeout = 5000
            if (req.loggingEnabled) {
                HttpLogger.d(TAG, "Http : URL : $url")
                HttpLogger.d(TAG, "Http : Method : $method")
                HttpLogger.d(TAG, "Http : Headers : " + req.header.toString())
                if (req.body != null) HttpLogger.d(
                    TAG, "Http : Request Body : " + HttpUtils.asString(
                        req.body
                    )
                )
            }
            for ((key, value) in req.header) {
                conn.setRequestProperty(key, value)
            }
            if (req.body != null) {
                conn.doOutput = true
                val os = conn.outputStream
                os.write(req.body)
            }
            conn.connect()
            return conn
        }

        /**
         * get document by query type and value as flow
         *
         * @param  conn   connection to be parsed and get code, body ...etc
         * @return         response which is success or error
         */
        @Throws(IOException::class)
        private fun parseResponse(conn: HttpURLConnection) : Response {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode
                if (req.loggingEnabled) HttpLogger.d(
                    TAG,
                    "Http : Response Status Code : " + status + " for URL: " + conn.url
                )
                val message = conn.responseMessage
                val respHeaders =
                    TreeMap<String?, List<String>>(java.lang.String.CASE_INSENSITIVE_ORDER)
                val headerFields: MutableMap<String?, List<String>> = HashMap(conn.headerFields)
                headerFields.remove(null)
                respHeaders.putAll(headerFields)
                val validStatus = status in 200..399
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream

                var read: Int
                val buf = ByteArray(bufferSize)
                while (inpStream.read(buf).also { read = it } != -1) {
                    bos.write(buf, 0, read)
                }
                val resp = Response(bos.toByteArray(), status, message, respHeaders, null)
                if (req.loggingEnabled && !validStatus) HttpLogger.d(
                    TAG,
                    "Http : Response Body : " + resp.asString()
                )
                HttpLogger.d(
                    TAG,
                    "TIME TAKEN FOR API CALL(MILLIS) : " + (System.currentTimeMillis() - reqTimeStamp)
                )
                return resp
            } finally {
                conn.disconnect()
            }
        }
    }

    /**
     * gather connection response info to response object
     *
     * @return         response which is success or error
     */
    class Response(
        private val data: ByteArray?,
        public val status: Int?,
        public val message: String?,
        public val respHeaders: Map<String?, List<String>>?,
        public val exception: Exception?
    ) {
        @Throws(JSONException::class)
        fun asJSONObject(): JSONObject {
            val str = asString()
            return if (str.isEmpty()) JSONObject() else JSONObject(str)
        }

        fun asString(): String {
            return HttpUtils.asString(data)
        }
    }
}