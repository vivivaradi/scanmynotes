package hu.bme.aut.android.scanmynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        loginButton.setOnClickListener {
            loginUser()
        }
        signupButton.setOnClickListener {
            signupUser()
        }
    }

    fun loginUser(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        Log.e("ERROR", task.result.toString())
                    }
                }
    }

    fun signupUser(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    Log.e("ERROR", task.exception.toString())
                }
            }
    }

}