package com.android.leandro.testesantander.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.leandro.testesantander.BuildConfig
import com.android.leandro.testesantander.Controller.Preferencias
import com.android.leandro.testesantander.Controller.ValidaCPF
import com.android.leandro.testesantander.R
import com.android.leandro.testesantander.model.UserAccount
import kotlinx.android.synthetic.main.login.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Long
import java.util.regex.Matcher
import java.util.regex.Pattern

class Login : AppCompatActivity() {

     var context: Context? = null

     var editLogin: EditText? = null
     var editSenha: EditText? = null
     var progressBar: ProgressBar? = null

     var UserSalvo: TextView? = null

     var msg: String? = null

     var preferencias: Preferencias? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        version.text = "v" + BuildConfig.VERSION_NAME

        inicializaAcoes()
        inicializaVariaves()
    }

    private fun inicializaVariaves() {

        context = baseContext
        editLogin = findViewById(R.id.activity_login_et_login)
        editSenha = findViewById(R.id.activity_login_et_password)
        progressBar = findViewById(R.id.activity_login_pb)

        UserSalvo = findViewById(R.id.user_salvo_tv_username)

        preferencias = Preferencias(context!!)

        verificaUsuarioSalvo()

    }

    private fun verificaUsuarioSalvo() {

        val userSave = preferencias!!.nomeUsuarioSalvo

        if (userSave != null) {
            activity_login_ll_userSalvo!!.visibility = View.VISIBLE
            UserSalvo!!.text = preferencias!!.nomeUsuarioSalvo
        }
    }

    private fun inicializaAcoes() {

        activity_login_btn_login!!.setOnClickListener {
            if (validUser()) {
                login()
            } else {
                showError()
            }
        }

        activity_login_ll_userSalvo!!.setOnClickListener { editLogin!!.setText(preferencias!!.nomeUsuarioSalvo) }
    }

    private fun login() {

        val user = editLogin!!.text.toString().trim()
        val password = editSenha!!.text.toString().trim()

        progressBar!!.visibility = View.VISIBLE


        val client = OkHttpClient()
        val mediaType = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, "user=$user&password=$password")
        val request = Request.Builder()
            .url("https://bank-app-test.herokuapp.com/api/login")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressBar!!.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@Login,
                    R.string.var_erro_conectar_tente_mais_tarde, Toast.LENGTH_SHORT).show()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    val myResponse = response.body()!!.string()
                    runOnUiThread {
                        try {
                            val jsonObject = JSONObject(myResponse)
                            val `object` = jsonObject.getJSONObject("userAccount")

                            val id = `object`.getString("userId")
                            val name = `object`.getString("name")
                            val bankAccount = `object`.getString("bankAccount")
                            val agency = `object`.getString("agency")
                            val balance = java.lang.Double.parseDouble(`object`.getString("balance"))

                            val userAccount1 = UserAccount(
                                Long.parseLong(id),
                                name,
                                bankAccount,
                                agency,
                                balance
                            )

                            progressBar!!.visibility = View.GONE

                            preferencias!!.salvarUsuarioPreferencias(user.toString())

                            callNextActivity(userAccount1)

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {

                    progressBar!!.visibility = View.GONE
                    Toast.makeText(this@Login, R.string.var_erro_conectar, Toast.LENGTH_SHORT).show()

                }
            }
        })


    }

    private fun showError() {
        Toast.makeText(this@Login, msg, Toast.LENGTH_SHORT).show()
    }

    private fun validUser(): Boolean {

        val user = editLogin!!.text.toString().trim()
        val password = editSenha!!.text.toString().trim()

        if (user.isEmpty()) {
            msg = getString(R.string.var_user_vazio)
            return false
        }

        if (password.isEmpty()) {
            msg = getString(R.string.var_senha_vazia)
            return false
        }

        if (isEmail(user) || ValidaCPF.isCPF(user)) {
            if (isValidPassword(password)) {
                return true
            } else {
                msg = getString(R.string.var_erro_senha)
                return false
            }
        } else {
            msg = getString(R.string.var_msg_erro_user)
            return false
        }


    }


    private fun isEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.find()
    }

    fun callNextActivity(userAccount: UserAccount) {
        val mIntent = Intent(context, MainActivity::class.java)
        mIntent.putExtra("id", userAccount.userid)
        mIntent.putExtra("name", userAccount.name)
        mIntent.putExtra("bankAccount", userAccount.bankaccount)
        mIntent.putExtra("agency", userAccount.agency)
        mIntent.putExtra("balance", userAccount.balance)

        startActivity(mIntent)


    }
}


