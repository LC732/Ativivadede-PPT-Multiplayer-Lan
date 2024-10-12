package com.example.pptcliente.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MyClienteService : Service() {
    private lateinit var socket: Socket
    private lateinit var escritor: PrintWriter
    private lateinit var leitor: BufferedReader

    private val reJogada = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val num = intent?.getIntExtra("JOGADA", 0)
            CoroutineScope(Dispatchers.IO).launch {
                if (::escritor.isInitialized) {
                    escritor.println(num)
                    devolverResposta()
                }
            }
        }
    }

    private val reKill = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (::escritor.isInitialized) {
                    escritor.println(-1)
                    stopSelf()
                }
            }
        }
    }

    private val reEstado = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (::escritor.isInitialized) {
                    escritor.println(4)
                    devolverResposta()
                }
            }
        }
    }

    private fun devolverResposta() {
        if (::leitor.isInitialized) {
            val responder = Intent("Ganhador").apply {
                val ganhador = leitor.readLine().toInt()
                putExtra("GANHADOR", ganhador)
            }
            sendBroadcast(responder)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ipAddress = intent?.getStringExtra("IP_ADDRESS")!!
        val portaa = intent?.getStringExtra("PORTA")!!

        val porta = portaa.toInt()
        if (ipAddress != null && porta != 0) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    socket = Socket(ipAddress, porta)
                    escritor = PrintWriter(socket.getOutputStream(), true)
                    leitor = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val mensagem = Intent("ConfirmarConexaoCliente")
                    sendBroadcast(mensagem)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        registerReceiver(reJogada, IntentFilter("JogadaCliente"), Context.RECEIVER_EXPORTED)
        registerReceiver(reKill, IntentFilter("killService"), Context.RECEIVER_EXPORTED)
        registerReceiver(reEstado, IntentFilter("estadoCliente"), Context.RECEIVER_EXPORTED)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::socket.isInitialized) {
                socket.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}