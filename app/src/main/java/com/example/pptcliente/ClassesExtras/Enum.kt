package com.example.pptcliente.ClassesExtras

enum class Jogada(val valor: Int){
    NADA(0),
    PEDRA(1),
    PAPEL(2),
    TESOURA(3);

    companion object {
        fun fromInt(valor: Int): Jogada? {
            return values().find { it.valor == valor }
        }
    }
}