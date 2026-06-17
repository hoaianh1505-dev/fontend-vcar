package activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.model.LoginRequest
import com.example.vcar.model.LoginResponse
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {

        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                this,
                "Nhập đầy đủ thông tin",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val request = LoginRequest(
            email,
            password
        )

        RetrofitClient.instance.login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    if (response.isSuccessful &&
                        response.body() != null &&
                        response.body()!!.success
                    ) {

                        val token =
                            response.body()!!.token

                        SharedPrefManager(this@LoginActivity)
                            .saveToken(token)

                        Toast.makeText(
                            this@LoginActivity,
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@LoginActivity,
                                HomeActivity::class.java
                            )
                        )

                        finish()

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Sai tài khoản hoặc mật khẩu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@LoginActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}