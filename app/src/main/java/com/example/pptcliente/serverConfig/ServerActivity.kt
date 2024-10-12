package com.example.pptcliente.serverConfig

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pptcliente.R
import com.example.pptcliente.jogo.ServerGameActivity
import com.example.pptcliente.services.MyServerService
import java.net.Inet4Address
import java.net.NetworkInterface


class ServerActivity : AppCompatActivity() {


    lateinit var serverStatus: TextView
    lateinit var tvIp: TextView
    lateinit var tvKey: TextView

    private val receivera = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val serverKey = intent?.getIntExtra("CHAVE", 0)
            tvKey.text = "Chave: $serverKey"
        }
    }
    private val receiverb = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            GoToGame()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_server)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = Intent(this, MyServerService::class.java)
        startService(intent)

        registerReceiver(receivera, IntentFilter("ServerActivity1"), Context.RECEIVER_EXPORTED)
        registerReceiver(receiverb, IntentFilter("ServerActivity2"), Context.RECEIVER_EXPORTED)


        serverStatus = findViewById(R.id.tvEstado)
        tvIp = findViewById(R.id.tvIP)
        tvKey = findViewById(R.id.tvChave)

        val serverIp = getLocalIpAddress()
        // Exibir IP
        tvIp.text = "IP: $serverIp"

    }


    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress ?: "IP não encontrado"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "IP não encontrado"
    }


    private fun GoToGame() {
        val intent = Intent(this, ServerGameActivity::class.java)
        startActivity(intent)
        finish()
    }
}