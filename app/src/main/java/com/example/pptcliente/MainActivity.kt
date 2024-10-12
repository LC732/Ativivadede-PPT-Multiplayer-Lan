package com.example.pptcliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.example.pptcliente.serverConfig.ClienteActivity
import com.example.pptcliente.serverConfig.ServerActivity


class MainActivity : ComponentActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun CriarOnClick(v: View) {
        val intent = Intent(this, ServerActivity::class.java)
        startActivity(intent)
    }

    fun EntrarOnClick(v: View) {
        val intent = Intent(this, ClienteActivity::class.java)
        startActivity(intent)
    }


}

