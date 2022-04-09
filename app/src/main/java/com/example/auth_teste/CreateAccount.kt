package com.example.auth_teste

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.example.auth_teste.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseUser


class CreateAccount : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailFocusListener()
        passwordFocusListener()

        binding.submitButton.setOnClickListener{submitForm()}

        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
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
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
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
        if(passwordText.length < 8)
        {
            return "Minimo de 8 caracteres"
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex()))
        {
            return "Deve conter pelo menos uma letra maiuscula"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex()))
        {
            return "Deve conter pelo menos uma letra minuscula"
        }
        return null
    }

    private fun validatePasswordMatch():String?{
        val validatePassowordText = binding.validatePasswordEditText.text.toString();
        val passwordText = binding.passwordEditText.text.toString();
        if(validatePassowordText != passwordText){
            return "Senhas não coincidem"
        }
        return null
    }


    private fun submitForm(){
        binding.validatePasswordContainer.helperText = validatePasswordMatch()
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()

        val validatedEmail = binding.emailContainer.helperText == null
        val validatedPassowrd = binding.passwordContainer.helperText == null
        val validatedPasswordMatch = binding.validatePasswordContainer.helperText == null

        if(validatedEmail && validatedPassowrd && validatedPasswordMatch ){
            createAccount(binding.emailEditText.text.toString() , binding.passwordEditText.text.toString())
        }else{
            AlertDialog.Builder(this).setTitle("Dados invalidos").setMessage("Corriga os dados incorretos para prosseguir").setPositiveButton("Okay"){_,_->
                //vai faze nada n
            }.show()
        }
    }


    private fun createAccount(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        updateUI(null)
                    }
                }
        }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            AlertDialog.Builder(this).setTitle("Erro ao cadastrar").setMessage("Ocorreu um erro, tente novamente.").setPositiveButton("Okay"){_,_->
                //vai faze nada n
            }.show()
            return;
        }
        val intent = Intent(this,Login::class.java)
        startActivity(intent)
    }

    }





