package ro.pub.cs.systems.eim.practicaltest02v8

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent

class PracticalTest02v8MainActivity : AppCompatActivity() {

    // Referințe către elementele din layout
    private lateinit var serverAddressEditText: EditText
    private lateinit var serverPortEditText: EditText
    private lateinit var startServerButton: Button
    private lateinit var stopServerButton: Button
    private lateinit var usdButton: Button
    private lateinit var eurButton: Button
    private lateinit var responseTextView: TextView

    // Thread-ul de server
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v8_main)

        // Inițializăm referințele la View-uri
        serverAddressEditText = findViewById(R.id.serverAddressEditText)
        serverPortEditText = findViewById(R.id.serverPortEditText)
        startServerButton = findViewById(R.id.startServerButton)
        stopServerButton = findViewById(R.id.stopServerButton)
        usdButton = findViewById(R.id.usdButton)
        eurButton = findViewById(R.id.eurButton)
        responseTextView = findViewById(R.id.responseTextView)

        // Buton START SERVER
        startServerButton.setOnClickListener {
            val portString = serverPortEditText.text.toString()
            if (portString.isEmpty()) {
                Toast.makeText(this, "Port not specified!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val port = portString.toInt()

            serverThread = ServerThread(port)
            serverThread?.start()
            Toast.makeText(this, "Server started on port $port", Toast.LENGTH_SHORT).show()
        }

        // Buton STOP SERVER
        stopServerButton.setOnClickListener {
            if (serverThread != null) {
                serverThread?.stopThread()
                serverThread = null
                Toast.makeText(this, "Server stopped!", Toast.LENGTH_SHORT).show()
            }
        }

        // Buton cerere USD
        usdButton.setOnClickListener {
            val address = serverAddressEditText.text.toString()
            val portString = serverPortEditText.text.toString()
            if (address.isEmpty() || portString.isEmpty()) {
                Toast.makeText(this, "Address/Port not specified!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val port = portString.toInt()

            val clientThread = ClientThread(address, port, "USD", responseTextView)
            clientThread.start()
        }

        // Buton cerere EUR
        eurButton.setOnClickListener {
            val address = serverAddressEditText.text.toString()
            val portString = serverPortEditText.text.toString()
            if (address.isEmpty() || portString.isEmpty()) {
                Toast.makeText(this, "Address/Port not specified!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val port = portString.toInt()

            val clientThread = ClientThread(address, port, "EUR", responseTextView)
            clientThread.start()
        }

        val openCalculatorButton = findViewById<Button>(R.id.openCalculatorButton)
        openCalculatorButton.setOnClickListener {
            val intent = Intent(this, CalculatorClientActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Oprim serverul dacă e activ
        serverThread?.stopThread()
    }
}