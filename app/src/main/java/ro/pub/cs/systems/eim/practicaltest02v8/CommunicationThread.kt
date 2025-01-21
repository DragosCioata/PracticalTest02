package ro.pub.cs.systems.eim.practicaltest02v8

import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL

class CommunicationThread(private val serverThread: ServerThread, private val socket: Socket) : Thread() {

    // 1 minut în milisecunde
    private val ONE_MINUTE: Long = 60 * 1000

    // Definim un TAG pentru logcat
    private val TAG = "PracticalTest02v8"

    override fun run() {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)

            // Mesaj de la client: "USD" sau "EUR"
            val request = reader.readLine()
            Log.d(TAG, "Received request from client: $request")

            if (request != null) {
                // Verificăm dacă datele (cache-ul) sunt mai vechi de 1 minut
                val currentTime = System.currentTimeMillis()
                val timeSinceUpdate = currentTime - serverThread.lastUpdateTimestamp
                Log.d(TAG, "Time since last update: $timeSinceUpdate ms")

                if (timeSinceUpdate > ONE_MINUTE) {
                    // Facem cererea la Coindesk
                    Log.d(TAG, "Cache older than 1 minute. Updating rates from coindesk...")
                    updateRatesFromCoindesk()
                } else {
                    Log.d(TAG, "Cache is still valid. Using cached data.")
                }

                // Răspundem cu valoarea cerută
                val response = when (request) {
                    "USD" -> serverThread.cachedUsdRate ?: "N/A"
                    "EUR" -> serverThread.cachedEurRate ?: "N/A"
                    else -> "Unknown request!"
                }
                Log.d(TAG, "Sending response to client: $response")
                writer.println(response)
            }

            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateRatesFromCoindesk() {
        try {
            val url = URL("https://api.coindesk.com/v1/bpi/currentprice/EUR.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            Log.d(TAG, "Coindesk request response code: $responseCode")

            if (responseCode == 200) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Parse JSON
                val jsonObject = JSONObject(response)
                Log.d(TAG, "Coindesk raw JSON: $jsonObject")

                val bpiObject = jsonObject.getJSONObject("bpi")
                val usdObject = bpiObject.getJSONObject("USD")
                val eurObject = bpiObject.getJSONObject("EUR")

                // Actualizăm cache-ul
                serverThread.lastUpdateTimestamp = System.currentTimeMillis()
                serverThread.cachedUsdRate = usdObject.getString("rate")
                serverThread.cachedEurRate = eurObject.getString("rate")

                Log.d(TAG, "Updated cache: USD=${serverThread.cachedUsdRate}, EUR=${serverThread.cachedEurRate}")
            }

            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
