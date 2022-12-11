package com.developer.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.developer.firebaseauth.databinding.ActivityMain2Binding
import com.developer.firebaseauth.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private val TAG = "Mainactivity"
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var RC_SIGN_IN = 1
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val number=intent.getStringExtra("key")
        binding.textPhone.text=number
        binding.edtCod.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                verifyCode()
                val view = currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }

//        binding.edtCod.addTextChangedListener {
//            if (it.toString().length==6){
//                verifyCode()
//            }
//        }


        binding.kodniQaytaOlish.setOnClickListener {
            val phoneNumber=binding.textPhone.text.toString()


            sendVerifecationCode(phoneNumber)

        }



    }


    private fun verifyCode() {
        val code = binding.edtCod.text.toString()
        if (code.length == 6){
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun sendVerifecationCode(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    Toast.makeText(this, "Muvaffaqiyatli", Toast.LENGTH_SHORT).show()




                    val user = task.result?.user


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Muvaffaqiyatsiz!!!", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Kod xato kiritildi", Toast.LENGTH_SHORT).show()
                    }

                    val intent=Intent(this,MainActivity3::class.java)
                    startActivity(intent)
                    // Update UI
                }
            }
    }

}