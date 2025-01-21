package ro.pub.cs.systems.eim.practicaltest02v8

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class CalculatorClientActivity : AppCompatActivity() {

    private lateinit var t1EditText: EditText
    private lateinit var t2EditText: EditText
    private lateinit var operationEditText: EditText
    private lateinit var serverIpEditText: EditText
    private lateinit var serverPortEditText: EditText
    private lateinit var computeButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_client)

        // Legăm View-urile
        t1EditText = findViewById(R.id.t1EditText)
        t2EditText = findViewById(R.id.t2EditText)
        operationEditText = findViewById(R.id.operationEditText)
        serverIpEditText = findViewById(R.id.serverIpEditText)
        serverPortEditText = findViewById(R.id.serverPortEditText)
        computeButton = findViewById(R.id.computeButton)
        resultTextView = findViewById(R.id.resultTextView)

        computeButton.setOnClickListener {
            val t1 = t1EditText.text.toString()
            val t2 = t2EditText.text.toString()
            val operation = operationEditText.text.toString()
            val serverIp = serverIpEditText.text.toString()
            val port = serverPortEditText.text.toString()

            // Validare minimă
            if (t1.isEmpty() || t2.isEmpty() || operation.isEmpty()
                || serverIp.isEmpty() || port.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Facem cererea la server pe un thread separat
            Thread {
                try {
                    // URL: http://IP:PORT/?operation=plus&t1=9&t2=2
                    val urlStr = "http://$serverIp:${port}/?operation=$operation&t1=$t1&t2=$t2"
                    val url = URL(urlStr)

                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.connect()

                    val responseCode = conn.responseCode
                    if (responseCode == 200) {
                        // Citim răspunsul (un string care conține rezultatul)
                        val response = conn.inputStream.bufferedReader().use { it.readText() }

                        // Afișăm în UI pe thread-ul principal
                        runOnUiThread {
                            resultTextView.text = response
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "HTTP error code: $responseCode", Toast.LENGTH_SHORT).show()
                        }
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }
}
