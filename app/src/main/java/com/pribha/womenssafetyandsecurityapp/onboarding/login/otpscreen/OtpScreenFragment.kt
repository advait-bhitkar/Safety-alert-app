package com.pribha.womenssafetyandsecurityapp.onboarding.login.otpscreen

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chaos.view.PinView
import com.google.android.material.button.MaterialButton
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.onboarding.SharedViewModel
import org.w3c.dom.Text

class OtpScreenFragment : Fragment() {

    companion object {
        fun newInstance() = OtpScreenFragment()
    }

    private lateinit var viewModel: SharedViewModel
    private lateinit var otpText: TextView
    private lateinit var pinView: PinView
    private lateinit var verifyNow: MaterialButton
    private lateinit var chronometer: Chronometer
    private lateinit var resendCode: TextView
    private lateinit var noCodeReceived: TextView
    private lateinit var invalidOTP: TextView

    private var phoneNumber: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.otp_screen_fragment, container, false)

        otpText = view.findViewById(R.id.otpText)
        pinView = view.findViewById(R.id.firstPinView)
        verifyNow = view.findViewById(R.id.verifyNow)
        chronometer = view.findViewById(R.id.chronometer)
        resendCode = view.findViewById(R.id.textView8)
        noCodeReceived = view.findViewById(R.id.noCodeRecieved)
        invalidOTP = view.findViewById(R.id.invalidOTP)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

//        otpText.text =  viewModel.getPhoneNumberFormatted().value.toString()


        chronometer.isCountDown = true
        chronometer.base = SystemClock.elapsedRealtime() + 60000
        chronometer.format = "Waiting for OTP... %s"
        chronometer.start()

        chronometer.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { chronometer ->

                if (SystemClock.elapsedRealtime() - chronometer.base > 0) {
                    chronometer.stop()

                    resendCode.visibility = View.VISIBLE
                    noCodeReceived.visibility = View.VISIBLE
                    chronometer.visibility = View.GONE

                }

            }


        resendCode.setOnClickListener(View.OnClickListener {

            viewModel.setResendCode(true)
            resendCode.visibility = View.GONE
            noCodeReceived.text = "Code sent again!"
            pinView.text = null
            pinView.setSelection(0)

        })




    verifyNow.setOnClickListener(View.OnClickListener {


            if (pinView.text.toString().length == 6) {
                viewModel.setOtpNumber(pinView.text.toString())
                viewModel.setVerifyPhoneNo(true)
            }
            else{
                Toast.makeText(requireContext(),"OTP must be 6 digits", Toast.LENGTH_SHORT ).show()
            }



        })


        viewModel.getInvalidCode()
            ?.observe(requireActivity(), object : Observer<Boolean?> {
                override fun onChanged(t: Boolean?) {

                    if (t == true) {
                        invalidOTP.visibility = View.VISIBLE

                    }
                }
            })



        viewModel.getSignInSuccessFul()
            ?.observe(requireActivity(), object : Observer<Boolean?> {
                override fun onChanged(t: Boolean?) {

                    if (t == true) {
                        findNavController().navigate(R.id.action_otpScreenFragment_to_enterNameScreen)

                    }
                }
            })




        viewModel.getPhoneNumberFormatted()
            ?.observe(viewLifecycleOwner, object : Observer<String?> {
                override fun onChanged(t: String?) {
                    otpText.text =
                        "Check your SMS messages. We have sent you a pin at " + t
                }
            })


        viewModel.getOtpNumber()
            ?.observe(viewLifecycleOwner, object : Observer<String?> {
                override fun onChanged(t: String?) {
                    pinView.setText(t)
                }
            })



    }

}