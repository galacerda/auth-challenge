package com.example.auth_teste

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.auth_teste.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        emailFocusListener()

        passwordFocusListener()

        binding.submitButton.setOnClickListener{submitForm()}

        binding.createAccountButton.setOnClickListener{sendToCreateAccountActivity()}

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        Handler().postDelayed({
            if(currentUser != null){
                sendToMainActivity()
            }else{
                binding.createAccountButton.visibility = View.VISIBLE
                binding.submitButton.visibility = View.VISIBLE
                binding.emailContainer.visibility = View.VISIBLE
                binding.passwordContainer.visibility = View.VISIBLE
            }
        }, 2000)

    }

    private fun emailFocusListener(){
        binding.emailEditText.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.emailContainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailEditText.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }


    private fun passwordFocusListener(){
        binding.passwordEditText.setOnFocusChangeListener{_,focused ->
            if(!focused){
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordEditText.text.toString()
        if(passwordText.length < 8) {
            return "Minimo de 8 caracteres"
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Deve conter pelo menos uma letra maiuscula"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Deve conter pelo menos uma letra minuscula"
        }
        return null
    }

    private fun submitForm(){
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()

        val validatedEmail = binding.emailContainer.helperText == null
        val validatedPassowrd = binding.passwordContainer.helperText == null


        if(validatedEmail && validatedPassowrd  ){
            signIn(binding.emailEditText.text.toString() , binding.passwordEditText.text.toString())
        }else{
            AlertDialog.Builder(this).setTitle("Dados invalidos").setMessage("Corriga os dados incorretos para prosseguir").setPositiveButton("Okay"){ _, _->
                //vai faze nada n
            }.show()
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI()
                }else{
                    AlertDialog.Builder(this).setTitle("Erro ao logar").setMessage("Ocorreu um erro, tente novamente.").setPositiveButton("Okay"){_,_->
                        //vai faze nada n
                    }.show()
                }
            }

    }

    private fun updateUI() {
        sendToMainActivity()
    }

    private fun sendToMainActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun sendToCreateAccountActivity(){
        val intent = Intent(this,CreateAccount::class.java)
        startActivity(intent)
    }
}