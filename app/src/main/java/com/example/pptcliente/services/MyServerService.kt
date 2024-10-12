package com.example.pptcliente.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.pptcliente.ClassesExtras.Jogada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.Writer
import java.net.ServerSocket
import java.net.Socket


class MyServerService : Service() {

    val serverSocket = ServerSocket(0)
    lateinit var clientSocket: Socket
    lateinit var reader: BufferedReader
    lateinit var writer: PrintWriter


    var chave = 0
    var respostasRecebidas: Int = 0 // Contador para respostas recebidas
    var intencoesRecebidas: Int = 0 // Contador para intenções recebidas
    lateinit var jogadaServer: Jogada
    lateinit var jogadaClient: Jogada

    var jogando = true
    var tempo = true

    private val reKill = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (!clientSocket.isClosed) {
                    writer.println(-1)
                }
                stopSelf()
            }
        }
    }
    private val reResposta = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val j: Int = intent?.getIntExtra("JOGADA", 0)!!
            CoroutineScope(Dispatchers.IO).launch {
                GetResposta()
                CriaVitoria()
            }
            jogadaServer = Jogada.fromInt(j)!!
            respostasRecebidas++
        }
    }
    private val reEstado = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            CoroutineScope(Dispatchers.IO).launch {
                GetIntencaoCLiente()

                while (intencoesRecebidas < 2) {
                    delay(200)
                    intencoesRecebidas = 0
                }
                /// colocar aqui o retorno para ambos os players

                val paraActServer = Intent("ESTADO").apply {
                    putExtra("Bool", true)
                }
                sendBroadcast(paraActServer)

                if (!clientSocket.isClosed) {
                    writer.println(4)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val estado: Boolean = intent?.getBooleanExtra("ESTADO", false)!!
                if (estado) {
                    intencoesRecebidas++
                } else {
                    jogando = false
                    if (!clientSocket.isClosed) {
                        writer.println(-1)
                    }
                    stopSelf()
                }
            }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        CoroutineScope(Dispatchers.IO).launch {
            StartServer()
        }

        registerReceiver(reKill, IntentFilter("killService"), Context.RECEIVER_EXPORTED)
        registerReceiver(reResposta, IntentFilter("jogadaService"), Context.RECEIVER_EXPORTED)
        registerReceiver(reEstado, IntentFilter("estadoService"), Context.RECEIVER_EXPORTED)

        return START_STICKY
    }

    private fun StartServer() {

        chave = serverSocket.localPort
        println("Servidor iniciado na porta $chave")

        val paraActServer = Intent("ServerActivity1").apply {
            putExtra("CHAVE", chave)
        }
        sendBroadcast(paraActServer)

        // Aceitar uma conexão de um cliente
        clientSocket = serverSocket.accept()

        val paraActServer2 = Intent("ServerActivity2")
        sendBroadcast(paraActServer2)
        // Criar leitor e escritor para a comunicação com o cliente

        writer = PrintWriter(clientSocket.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

    }

    private fun GetResposta() {

        // Ler o número enviado pelo cliente
        val numero = reader.readLine()

        jogadaClient = Jogada.fromInt(numero.toInt())!!
        respostasRecebidas++

    }

    private fun GetIntencaoCLiente() {
        // Ler o número enviado pelo cliente
        val numero = reader.readLine()
        if (numero.toInt() == -1) {
            jogando = false
            val paraActServer = Intent("ESTADO").apply {
                putExtra("Bool", false)
            }
            sendBroadcast(paraActServer)
            stopSelf()
        } else {
            intencoesRecebidas++;
        }
    }

    private suspend fun CriaVitoria() {
        while (respostasRecebidas < 2) {
            delay(200)
        }
        respostasRecebidas = 0
        /// encontra o ganhador
        var ganhador = if (jogadaServer == jogadaClient) {
            0
        } else if ((jogadaServer == Jogada.PAPEL && jogadaClient == Jogada.PEDRA)
            || (jogadaServer == Jogada.PEDRA && jogadaClient == Jogada.TESOURA)
            || (jogadaServer == Jogada.TESOURA && jogadaClient == Jogada.PAPEL)
        ) {
            1
        } else {
            2
        }

        val broadganhador = Intent("Ganhador").apply {

            putExtra("GANHADOR", ganhador)

        }
        sendBroadcast(broadganhador)

        //manda quem ganhou pro cliente
        if (!clientSocket.isClosed) {
            writer.println(ganhador)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::writer.isInitialized) {
            writer.close() // Fechar o writer se ele estiver inicializado
        }
        if (::reader.isInitialized) {
            reader.close() // Fechar o writer se ele estiver inicializado
        }
        if (::clientSocket.isInitialized) {
            clientSocket.close() // Fechar o writer se ele estiver inicializado
        }
        serverSocket.close()
    }

}