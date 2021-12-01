package br.unifor.financeapp.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import at.favre.lib.crypto.bcrypt.BCrypt
import br.unifor.financeapp.R
import br.unifor.financeapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mFirstName:EditText
    private lateinit var mLastName:EditText
    private lateinit var mEmail:EditText
    private lateinit var  mPhone:EditText
    private  lateinit var mPassword:EditText
    private lateinit var mPasswordConfirm:EditText
    private lateinit var mSignUp:Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth= FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()

        mFirstName = findViewById(R.id.register_editText_firstname)
        mLastName = findViewById(R.id.register_editText_lastname)
        mEmail = findViewById(R.id.register_editText_email)
        mPhone = findViewById(R.id.register_editText_phone)
        mPassword = findViewById(R.id.register_editText_password)
        mPasswordConfirm = findViewById(R.id.register_editText_password_confirm)

        mSignUp = findViewById(R.id.register_button_signup)
        mSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id){

            R.id.register_button_signup ->{

                val firstName= mFirstName.text.toString()
                val lastName= mLastName.text.toString()
                val email= mEmail.text.toString()
                val phone= mPhone.text.toString()
                val password= mPassword.text.toString()
                val passwordConf= mPasswordConfirm.text.toString()

                var isFormFilled= true

                if(firstName.isEmpty()){
                    mFirstName.error="This box cannot be blank"
                    isFormFilled= false
                }
                if(lastName.isEmpty()){
                    mLastName.error="This box cannot be blank"
                    isFormFilled= false
                }
                if(email.isEmpty()){
                    mEmail.error="This box cannot be blank"
                    isFormFilled= false
                }
                if(phone.isEmpty()){
                    mPhone.error="This box cannot be blank"
                    isFormFilled= false
                }
                if(password.isEmpty()){
                    mPassword.error="This box cannot be blank"
                    isFormFilled= false
                }
                if(passwordConf.isEmpty()){
                    mPasswordConfirm.error="This box cannot be blank"
                    isFormFilled= false
                }

                if(isFormFilled){

                    if(password!=passwordConf){
                        mPasswordConfirm.error="Given passwords are not the same"
                        return
                    }

                    val dialog= ProgressDialog(this)
                    dialog.setTitle("Finance Manager - Please Wait")
                    dialog.isIndeterminate= true
                    dialog.show()

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener{
                            dialog.dismiss()
                            val handler= Handler(Looper.getMainLooper())
                            if(it.isSuccessful){

                                val user= User( email, firstName, lastName, phone)

                                val ref = mDatabase.getReference("users/${mAuth.uid!!}")
                                ref.setValue(user)

                                handler.post{
                                    Toast.makeText(applicationContext, "User Registered Succesfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                            else{
                                handler.post{
                                    Toast.makeText(applicationContext, it.exception?.message, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }

 /*                   GlobalScope.launch {

                        val handler= Handler(Looper.getMainLooper())

                        if(user==null && user2==null){
                            val newUser= User(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phone = phone,
                                pswrd = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                            )

                            handler.post{
                                Toast.makeText(applicationContext, "User Registered Succesfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        else{
                            handler.post{
                                mEmail.error= "There might already be a user registered with these details"
                                mPhone.error= "There might already be a user registered with these details"
                            }
                        }


                    }

  */
                }
            }
        }
    }
}