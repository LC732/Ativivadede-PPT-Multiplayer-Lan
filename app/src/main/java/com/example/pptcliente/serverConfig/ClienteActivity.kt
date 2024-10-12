package com.example.pptcliente.serverConfig

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pptcliente.R
import com.example.pptcliente.jogo.ClienteGameActivity
import com.example.pptcliente.jogo.ServerGameActivity
import com.example.pptcliente.services.MyClienteService

class ClienteActivity : AppCompatActivity() {

    lateinit var iP: EditText
    lateinit var chave: EditText

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            GoToGame()
        }
    }

    private fun GoToGame() {
        val intent = Intent(this, ClienteGameActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iP = findViewById(R.id.eTIP)
        chave = findViewById(R.id.eTKey)

        registerReceiver(
            receiver,
            IntentFilter("ConfirmarConexaoCliente"),
            Context.RECEIVER_EXPORTED
        )

    }

    fun ClienteOnClick(v: View) {

        val servidorIP = iP.text.toString() // Endereço IP do servidor
        val porta = chave.text.toString() // Porta que o servidor está ouvindo
        try {
            // Conectar ao servidor
            val intent = Intent(this, MyClienteService::class.java).apply {
                putExtra("IP_ADDRESS", servidorIP)
                putExtra("PORTA", porta)
            }
            startService(intent)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}