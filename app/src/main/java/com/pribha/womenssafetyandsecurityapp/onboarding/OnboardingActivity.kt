package com.pribha.womenssafetyandsecurityapp.onboarding

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.viewmodel.DataStoreViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class OnboardingActivity : AppCompatActivity() {


    private lateinit var viewModel: SharedViewModel


    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private var userName: String? = ""

    var phone: String? = null
    var phoneNumber: String? = null

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.WomensSafetyAndSecurityApp);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        supportActionBar?.hide()

        initComponent()
    }


    fun initComponent(){

        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        viewModel.setVerifyPhoneNo(false)


        viewModel.getYourName()
            ?.observe(this, object : Observer<String?> {
                override fun onChanged(t: String?) {
                    userName = t
                    saveDataToFirestore()
                }
            })



        viewModel.getResendCode()
            ?.observe(this, object : Observer<Boolean?> {
                override fun onChanged(t: Boolean?) {
                    if (t== true)
                    {
                        resendVerificationCode(phoneNumber.toString(), resendToken)
                    }
                }
            })



        viewModel.getOtpNumber()
            ?.observe(this, object : Observer<String?> {
                override fun onChanged(t: String?) {
                    phone = t
                }
            })





        viewModel.getVerifyPhoneNo()
            ?.observe(this, object : Observer<Boolean?> {
                override fun onChanged(t: Boolean?) {


                    Log.d(ContentValues.TAG, phone.toString())

                    if (t == true) {
                        Toast.makeText(applicationContext, viewModel.getVerificationIdNew(), Toast.LENGTH_SHORT).show()
                        verifyPhoneNumberWithCode(viewModel.getVerificationIdNew().toString(), phone.toString())
                    }

                }
            })


        viewModel.getPhoneNumber()
            ?.observe(this, object : Observer<String?> {
                override fun onChanged(t: String?) {

                    phoneNumber = t.toString()
                    startPhoneNumberVerification(t.toString())

                }
            })




        auth = Firebase.auth
        auth.useAppLanguage()


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.


                val code: String? = credential.smsCode

                if (code!=null)
                {

                }
                Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")


                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(ContentValues.TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(applicationContext, "Verification Failed", Toast.LENGTH_SHORT).show()

                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(applicationContext, "Too many requests", Toast.LENGTH_SHORT).show()

                }

                // Show a message and update the UI
                // ...

                Toast.makeText(applicationContext, "Please try after some time", Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                viewModel.setVerificationIdNew(verificationId)
                Log.d(ContentValues.TAG, "gfffffff")

                resendToken = token

                // ...
            }
        }


    }



    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {

        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]

        verificationInProgress = true
    }



    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")


                    //get firebase user

                    //get firebase user

                    val user = task.result?.user


                    viewModel.setSignInSuccessFul(true)


                    Toast.makeText(
                        applicationContext,
                        "Verification Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    // [START_EXCLUDE]
//                        updateUI(STATE_SIGNIN_SUCCESS, user)
                    // [END_EXCLUDE]
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
//                            binding.fieldVerificationCode.error = "Invalid code."

                        viewModel.setInvalidCode(true)
                        Toast.makeText(
                            applicationContext,
                            "Invalid Code",
                            Toast.LENGTH_SHORT
                        ).show()


                        // [END_EXCLUDE]
                    }
                    // [START_EXCLUDE silent]
                    // Update UI
//                        updateUI(STATE_SIGNIN_FAILED)
                    // [END_EXCLUDE]
                }
            }
    }
    // [END sign_in_with_phone]


    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        // [START_EXCLUDE]
        if (verificationInProgress ) {
            startPhoneNumberVerification(phoneNumber.toString())
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]


    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {

        Log.d(ContentValues.TAG, "dfvdjkslaldkfksdlfslf")

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]



    private fun saveDataToFirestore (){

        val db = Firebase.firestore

        // Create a new user with a first and last name
        val user = hashMapOf(
            "uid" to FirebaseAuth.getInstance().uid,
            "phoneNo" to phoneNumber.toString().trim(),
            "name" to userName
        )

        db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .set(user)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }



        val mobileNumber = hashMapOf(
            "uid" to FirebaseAuth.getInstance().uid,
        )


        db.collection("mobiles").document(phoneNumber.toString().trim())
            .set(mobileNumber)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }


    }


}