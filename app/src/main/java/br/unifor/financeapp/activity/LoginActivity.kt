package br.unifor.financeapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import br.unifor.financeapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail:EditText
    private lateinit var mLoginPswrd:EditText
    private lateinit var mLoginSignin:Button

    private lateinit var mRegister:TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth= FirebaseAuth.getInstance()

        mLoginEmail= findViewById(R.id.login_editText_email)
        mLoginPswrd= findViewById(R.id.login_editText_password)
        mLoginSignin= findViewById(R.id.login_button_signin)
        mLoginSignin.setOnClickListener(this)

        mRegister= findViewById(R.id.login_textView_register)
        mRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_textView_register ->{
                val it = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(it)
            }

            R.id.login_button_signin ->{
                val email= mLoginEmail.text.toString()
                val password= mLoginPswrd.text.toString()

                var isLoginFormFilled= true

                if(email.isEmpty()){
                    mLoginEmail.error= "please provide an email address to log-in"
                    isLoginFormFilled= false
                }
                if(password.isEmpty()){
                    mLoginPswrd.error= "please provide a password to log-in"
                    isLoginFormFilled= false
               }
               if(isLoginFormFilled){

                   val dialog= ProgressDialog(this)
                   dialog.setTitle("Finance Manager - Please Wait")
                   dialog.isIndeterminate= true
                   dialog.show()

                   mAuth.signInWithEmailAndPassword(email, password)
                       .addOnCompleteListener{
                           if(it.isSuccessful){
                               val it= Intent(applicationContext, MainActivity::class.java)
                               startActivity(it)
                               finish()
                           }
                           else{
                               showToastMessage()
                           }
                       }
               }
            }
        }
    }

    private fun showToastMessage(){
        val handler= Handler(Looper.getMainLooper())
        handler.post{
            Toast.makeText(applicationContext, "User or Password Invalid", Toast.LENGTH_SHORT).show()
        }
    }
}