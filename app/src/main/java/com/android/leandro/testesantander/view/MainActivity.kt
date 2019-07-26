package com.android.leandro.testesantander.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.leandro.testesantander.model.AdapterStatements
import com.android.leandro.testesantander.Controller.Preferencias
import com.android.leandro.testesantander.R
import com.android.leandro.testesantander.model.Statement
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {


    var context: Context? = null
    var txtUsuario: TextView? = null
    var image: ImageView? = null
    var txtConta: TextView? = null
    var txtSaldo: TextView? = null
    var recyclerView: RecyclerView? = null
    var adapterStatement: AdapterStatements? = null
    var listStatements: MutableList<Statement>? = null

    var userId: Double = 0.toDouble()

    var preferencias: Preferencias? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializaVariaves()
    }

    override fun onBackPressed() {
        confirmacaSaida()
    }

    private fun inicializaVariaves() {
        context = baseContext
        preferencias = Preferencias(context!!)

        txtUsuario = findViewById(R.id.main_activity_tb_username)
        image = findViewById(R.id.main_activity_iv_sair)
        txtConta = findViewById(R.id.main_activity_tv_conta)
        txtSaldo = findViewById(R.id.main_activity_tv_saldo)

        recyclerView = findViewById(R.id.main_activity_rv)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        listStatements = ArrayList()

        adapterStatement = AdapterStatements(
            listStatements as ArrayList<Statement>,
            context!!
        )
        recyclerView!!.adapter = adapterStatement


        loadUserInformations()
        loadListStatements()
        inicializaAcoes()

    }

    private fun inicializaAcoes() {

        image!!.setOnClickListener {
            confirmacaSaida()

            Toast.makeText(context, " Deseja realmente sair", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmacaSaida() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle(R.string.var_title_getout)
        alerta.setMessage(R.string.var_msg_getout)
        alerta.setCancelable(false)
        alerta.setPositiveButton(R.string.var_yes) { dialog, which ->
            val mIntent = Intent(context, Login::class.java)
            startActivity(mIntent)
            finish()
        }
        alerta.setNegativeButton(R.string.var_no, null)
        alerta.show()
    }

    private fun loadUserInformations() {

        userId = intent.getLongExtra("id", 0).toDouble()
        val name = intent.getStringExtra("name")
        val bankaccount = intent.getStringExtra("bankAccount")
        val agency = intent.getStringExtra("agency")
        val saldo = intent.getDoubleExtra("balance", 0.0)

        txtUsuario!!.text = name

        txtConta!!.text = bankaccount + " / " + formataAgencia(agency)


        txtSaldo!!.text = "R$$saldo"
    }

    private fun loadListStatements() {
        val url = "https://bank-app-test.herokuapp.com/api/statements/$userId"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Toast.makeText(context,
                    R.string.var_erro_conectar_tente_mais_tarde, Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body()!!.string()
                    runOnUiThread {
                        try {
                            val jsonObject = JSONObject(myResponse)
                            val jsonElements = jsonObject.getJSONArray("statementList")

                            for (i in 0 until jsonElements.length()) {

                                val jsonObject1 = jsonElements.getJSONObject(i)
                                val statement = Statement(
                                    jsonObject1.getString("title").toString(),
                                    jsonObject1.getString("desc"),
                                    jsonObject1.getString("date"),
                                    jsonObject1.getDouble("value")
                                )
                                listStatements!!.add(statement)
                            }
                            adapterStatement!!.notifyDataSetChanged()


                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                } else {
                    Toast.makeText(this@MainActivity,
                        R.string.var_erro_conectar, Toast.LENGTH_SHORT).show()
                }

            }
        })

    }

    private fun formataAgencia(agency: String): String {
        return(agency.substring(0, 2) + "."+
                agency.substring(2, 8) + "-" +
                agency.substring(8, 9))
    }
}
