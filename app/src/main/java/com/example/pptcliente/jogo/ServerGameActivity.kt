package com.example.pptcliente.jogo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.pptcliente.ClassesExtras.Jogada
import com.example.pptcliente.R

class ServerGameActivity : AppCompatActivity() {

    var jogada = Jogada.NADA
    lateinit var tVJogada: TextView
    lateinit var btPedra: Button
    lateinit var btPapel: Button
    lateinit var btTesoura: Button
    lateinit var btJogada: Button
    lateinit var btSair: Button
    var estado = true


    private val reGanhador = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val resultado = intent?.getIntExtra("GANHADOR", 0)!!

            tVJogada.text = when (resultado) {
                0 -> "Empate"
                1 -> "Ganhou!"
                2 -> "Perdeu!"
                else -> "Deu erro!"
            }

        }
    }

    private val reEstado = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val estad = intent?.getBooleanExtra("Bool", false)!!

            if (estad) {
                estado = estad
                jogada = Jogada.NADA
                tVJogada.text = ""
                btJogada.isClickable = true
                btJogada.text = "Jogar"
            } else {
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente_game)

        tVJogada = findViewById(R.id.tVJogada)
        btPedra = findViewById(R.id.btPedra)
        btPapel = findViewById(R.id.btPapel)
        btTesoura = findViewById(R.id.btTesoura)
        btJogada = findViewById(R.id.btJogada)
        btSair = findViewById(R.id.btSair)

        btPedra.setOnClickListener {
            tVJogada.text = btPedra.text
            jogada = Jogada.PEDRA
        }
        btPapel.setOnClickListener {
            tVJogada.text = btPapel.text
            jogada = Jogada.PAPEL
        }
        btTesoura.setOnClickListener {
            tVJogada.text = btTesoura.text
            jogada = Jogada.TESOURA
        }

        btJogada.setOnClickListener {
            if (estado) {
                if (jogada != Jogada.NADA) {
                    estado = false
                    val paraActServer = Intent("jogadaService").apply {
                        putExtra("JOGADA", jogada.valor)
                    }
                    sendBroadcast(paraActServer)
                    btJogada.text = "Novo Jogo"
                }
            } else {
                btJogada.isClickable = false
                val paraActServer = Intent("estadoService").apply {
                    putExtra("ESTADO", true)
                }
                sendBroadcast(paraActServer)
                tVJogada.text = ""
                btJogada.text = "Esperando..."
            }

        }

        btSair.setOnClickListener {
            val paraActServer = Intent("killService")
            sendBroadcast(paraActServer)
            finish()
        }

        registerReceiver(reGanhador, IntentFilter("Ganhador"), Context.RECEIVER_EXPORTED)
        registerReceiver(reEstado, IntentFilter("ESTADO"), Context.RECEIVER_EXPORTED)

    }
}