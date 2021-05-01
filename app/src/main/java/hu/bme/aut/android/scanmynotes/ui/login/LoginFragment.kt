package hu.bme.aut.android.scanmynotes.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.MainActivity
import hu.bme.aut.android.scanmynotes.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.noteListFragment)
        }

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
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.noteListFragment)
                } else {
                    Toast.makeText(context, task.exception.toString(),
                        Toast.LENGTH_LONG).show()
                    Log.e("ERROR", task.exception.toString())
                }
            }
    }

    fun signupUser(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.noteListFragment)
                } else {
                    Toast.makeText(context, task.exception.toString(),
                        Toast.LENGTH_LONG).show()
                    Log.e("ERROR", task.exception.toString())
                }
            }
    }


}