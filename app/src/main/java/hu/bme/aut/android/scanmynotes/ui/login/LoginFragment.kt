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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.MainActivity
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentLoginBinding
import hu.bme.aut.android.scanmynotes.util.validateEmailContent
import hu.bme.aut.android.scanmynotes.util.validatePasswordContent

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.noteListFragment)
        }

        binding.loginButton.setOnClickListener {
            loginUser()
        }
        binding.signupButton.setOnClickListener {
            signupUser()
        }
    }

    fun loginUser(){
        if (!validateForm())
            return
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    emptyFields()
                    findNavController().navigate(R.id.noteListFragment)
                } else {
                    Toast.makeText(context, task.exception.toString(),
                        Toast.LENGTH_LONG).show()
                    Log.e(getString(R.string.error_tag), task.exception.toString())
                }
            }
    }

    fun signupUser(){
        if (!validateForm())
            return
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    val bundle = Bundle()
                    val method = getString(R.string.analytics_signup_method_text)
                    bundle.putString(FirebaseAnalytics.Param.METHOD, method)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                    emptyFields()
                    findNavController().navigate(R.id.noteListFragment)
                } else {
                    Toast.makeText(context, task.exception.toString(),
                        Toast.LENGTH_LONG).show()
                    Log.e(getString(R.string.error_tag), task.exception.toString())
                }
            }
    }

    fun emptyFields() {
        binding.email.setText("")
        binding.password.setText("")

    }

    fun validateForm() = binding.email.validateEmailContent() && binding.password.validatePasswordContent()
}