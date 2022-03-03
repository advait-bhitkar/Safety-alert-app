package com.pribha.womenssafetyandsecurityapp.onboarding.login

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chaos.view.PinView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hbb20.CountryCodePicker
import com.pribha.womenssafetyandsecurityapp.R
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var fieldPhoneNumber: EditText
    private lateinit var verifyNumber: MaterialButton
    private lateinit var ccp: CountryCodePicker

    private lateinit var screenMobileNo: ConstraintLayout
    private lateinit var screenOtp: ConstraintLayout
    private lateinit var screenGetStated:ConstraintLayout

    //    private lateinit var binding: ActivityPhoneAuthBinding
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var verifyButton: MaterialButton
    private lateinit var pinView: PinView
    private lateinit var goToApp: MaterialButton
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var yourName: EditText

    private lateinit var otpText: TextView
    private lateinit var invalidOtp: TextView

    private lateinit var waitingForOTP: TextView
    private lateinit var chronometer: Chronometer

    private lateinit var noCodeReceived: TextView
    private lateinit var resendCode: TextView
    private lateinit var changeNumber: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        fieldPhoneNumber = view.findViewById(R.id.editTextId)
        verifyNumber = view.findViewById(R.id.materialButton)
        ccp = view.findViewById(R.id.ccp)

        screenMobileNo = view.findViewById(R.id.mobileNoScreen)
        screenOtp = view.findViewById(R.id.otpScreen)
        screenGetStated = view.findViewById(R.id.getStarted)

        verifyButton = view.findViewById(R.id.verifyNow)

        pinView = view.findViewById(R.id.firstPinView)

        yourName = view.findViewById(R.id.editTextTextPersonName)


        goToApp = view.findViewById(R.id.gotoapp)
        otpText = view.findViewById(R.id.otpText)
        invalidOtp = view.findViewById(R.id.invalidOTP)
        chronometer = view.findViewById(R.id.chronometer)

        noCodeReceived = view.findViewById(R.id.noCodeRecieved)
        resendCode = view.findViewById(R.id.textView8)

        changeNumber = view.findViewById(R.id.changeNumber)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        changeNumber.setOnClickListener(View.OnClickListener {

            screenMobileNo.visibility = View.VISIBLE
            screenOtp.visibility = View.GONE
        })

        resendCode.setOnClickListener(View.OnClickListener {

            resendVerificationCode(ccp.fullNumberWithPlus.toString(), resendToken)

            resendCode.visibility = View.GONE
            noCodeReceived.text = "OTP sent again."
            invalidOtp.visibility = View.INVISIBLE
            pinView.text = null
            pinView.setSelection(0)


        })



        goToApp.setOnClickListener(View.OnClickListener {
            saveDataToFirestore()
        })

        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        ccp.registerCarrierNumberEditText(fieldPhoneNumber)
        ccp.setAutoDetectedCountry(true)
        ccp.setNumberAutoFormattingEnabled(true)



        verifyButton.setOnClickListener(View.OnClickListener {

            verifyPhoneNumberWithCode(storedVerificationId, pinView.text.toString())

        })



        ccp.setPhoneNumberValidityChangeListener {
            // your code

//            fieldPhoneNumber.setText(ccp.formattedFullNumber.toString())

        }

        verifyNumber.setOnClickListener(View.OnClickListener {

            Toast.makeText(requireContext(), ccp.fullNumberWithPlus.toString(), Toast.LENGTH_SHORT)
                .show()

            otpText.text =
                "Check your SMS messages. We have sent you a pin at " + ccp.formattedFullNumber


            startPhoneNumberVerification(ccp.fullNumberWithPlus.toString())

            screenOtp.visibility = View.VISIBLE
            screenMobileNo.visibility = View.GONE

            chronometer.isCountDown = true
            chronometer.base = SystemClock.elapsedRealtime() + 60000
            chronometer.format = "Waiting for OTP... %s"
            chronometer.start()

            chronometer.onChronometerTickListener =
                OnChronometerTickListener { chronometer ->

                    if (SystemClock.elapsedRealtime() - chronometer.base > 0) {
                        chronometer.stop()

                        resendCode.visibility = View.VISIBLE
                        noCodeReceived.visibility = View.VISIBLE
                        chronometer.visibility = View.GONE

                    }
                }
        }
        )

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]


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
                    pinView.setText(code)

                }
                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(context, "Verification Failed", Toast.LENGTH_SHORT).show()

                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(context, "Too many requests", Toast.LENGTH_SHORT).show()

                }

                // Show a message and update the UI
                // ...

                Toast.makeText(context, "Please try after some time", Toast.LENGTH_SHORT).show()

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

                // ...
            }
        }




    }



    public fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
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
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]

        verificationInProgress = true
    }



    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")


                        //get firebase user

                        //get firebase user

                        val user = task.result?.user

                        screenOtp.visibility = View.GONE
                        screenGetStated.visibility = View.VISIBLE


                        Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()
                        // [START_EXCLUDE]
//                        updateUI(STATE_SIGNIN_SUCCESS, user)
                        // [END_EXCLUDE]
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // [START_EXCLUDE silent]
//                            binding.fieldVerificationCode.error = "Invalid code."
                            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()

                            invalidOtp.visibility = View.VISIBLE
                            YoYo.with(Techniques.Shake)
                                .duration(700)
                                .repeat(1)
                                .playOn(invalidOtp);


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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)

        // [START_EXCLUDE]
        if (verificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(pinView.text.toString())
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]


    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = fieldPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.error = "Invalid phone number."
            return false
        }

        return true
    }



    //resend code

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
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
            "phoneNo" to ccp.fullNumberWithPlus,
            "name" to yourName.text.trim().toString()
        )

        db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.toString()}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }



}
