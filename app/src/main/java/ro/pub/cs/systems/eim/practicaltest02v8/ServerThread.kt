package ro.pub.cs.systems.eim.practicaltest02v8

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerThread(private val port: Int) : Thread() {

    private var serverSocket: ServerSocket? = null
    private var isRunning = true

    // Variabile pentru stocare date (cache)
    // Timpul ultimei actualizări și valorile USD/EUR
    var lastUpdateTimestamp: Long = 0
    var cachedUsdRate: String? = null
    var cachedEurRate: String? = null

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            while (isRunning) {
                val socket: Socket = serverSocket!!.accept()
                // Pentru fiecare conexiune, lansăm un thread de comunicare
                CommunicationThread(this, socket).start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            stopThread()
        }
    }

    fun stopThread() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}