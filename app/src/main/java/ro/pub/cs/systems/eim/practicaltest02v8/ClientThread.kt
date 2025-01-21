package ro.pub.cs.systems.eim.practicaltest02v8

import android.app.Activity
import android.util.Log
import android.widget.TextView
import java.io.*
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    private val request: String,
    private val responseTextView: TextView
) : Thread() {

    private val TAG = "PracticalTest02v8"

    override fun run() {
        var socket: Socket? = null
        try {
            socket = Socket(address, port)
            Log.d(TAG, "Client connected to server at $address:$port")

            val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Trimitem cererea
            Log.d(TAG, "Sending request to server: $request")
            writer.println(request)

            // Primim răspunsul
            val response = reader.readLine()
            Log.d(TAG, "Response from server: $response")

            // Actualizăm UI-ul pe thread-ul principal
            if (response != null) {
                (responseTextView.context as Activity).runOnUiThread {
                    responseTextView.text = response
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
