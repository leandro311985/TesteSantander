package com.android.leandro.testesantander.Controller

import android.content.Context
import android.content.SharedPreferences

class Preferencias(private val context: Context) {
    var preferences: SharedPreferences
    private val NOME_ARQUIVO = "testesantander.preferencias"
    private val MODE = 0
    private val editor: SharedPreferences.Editor

    private val CHAVE_USUARIO = "NomeUsuarioSalvo"

    val nomeUsuarioSalvo: String?
        get() = preferences.getString(CHAVE_USUARIO, null)


    init {
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE)

        editor = preferences.edit()
    }

    fun salvarUsuarioPreferencias(nomeUsuarioSalvo: String) {
        editor.putString(CHAVE_USUARIO, nomeUsuarioSalvo)
        editor.commit()
    }
}