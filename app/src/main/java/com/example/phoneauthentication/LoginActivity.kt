package com.example.phoneauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        btn_GetOtp.setOnClickListener {

            var phoneno = et_Phone.text.toString().trim()

            if (phoneno.isNotEmpty()){
                sendVerificationcode("+254$phoneno")
            } else {
                Toast.makeText(applicationContext,"Please Enter Phone No",Toast.LENGTH_LONG).show()
            }
        }
        btn_Signup.setOnClickListener {
            var otp = et_Otp.text.toString().trim()
                verifyVerificationcode(otp)
            if (otp.isNotEmpty()){

            } else {
                Toast.makeText(applicationContext,"Please Enter Otp",Toast.LENGTH_LONG).show()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                val code = credential.smsCode
                if (code!=null){
                    et_Otp.setText(code)
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext,"Authentication Failed",Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                phone_layout.visibility = View.GONE
                otp_layout.visibility = View.VISIBLE
            }
        }
    }

    private fun sendVerificationcode(phoneNo: String){
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            phoneNo,
//            60,
//            TimeUnit.SECONDS,
//            this,
//            callbacks
//        )

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyVerificationcode(code: String){
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signUp(credential)
    }

    private fun signUp(credential: PhoneAuthCredential){
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(applicationContext,"Sign Up Successfully!",Toast.LENGTH_LONG).show()
                    var intent = Intent(applicationContext,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext,"Code entered is Incorrect",Toast.LENGTH_LONG).show()
                        et_Otp.setText("")
                    }
                }
            }
    }
}