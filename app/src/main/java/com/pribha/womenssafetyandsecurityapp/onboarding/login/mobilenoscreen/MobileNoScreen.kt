package com.pribha.womenssafetyandsecurityapp.onboarding.login.mobilenoscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.button.MaterialButton
import com.hbb20.CountryCodePicker
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.onboarding.SharedViewModel


class MobileNoScreen : Fragment() {

    companion object {
        fun newInstance() = MobileNoScreen()
    }

    private lateinit var viewModel: SharedViewModel
    private lateinit var ccp: CountryCodePicker
    private lateinit var fieldPhoneNumber: EditText
    private lateinit var verifyNumber: MaterialButton

    private var phoneNumber : String = ""
    private var phoneNumberFormatted : String = ""

    private var mSubscriptionManager: SubscriptionManager? = null
    private var PHONE_NUMBER_FETCH_REQUEST_CODE: Int = 1233
    var isMultiSimEnabled = false
    var defaultSimName: String? = null

    var subInfoList: List<SubscriptionInfo>? = null
    var Numbers: ArrayList<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view  = inflater.inflate(R.layout.mobile_no_screen_fragment, container, false)

        ccp = view.findViewById(R.id.ccp)
        fieldPhoneNumber = view.findViewById(R.id.editTextId)
        verifyNumber = view.findViewById(R.id.materialButton)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        ccp.registerCarrierNumberEditText(fieldPhoneNumber)
        ccp.setAutoDetectedCountry(true)
        ccp.setNumberAutoFormattingEnabled(true)


        verifyNumber.setOnClickListener(View.OnClickListener {


            if (validatePhoneNumber()) {
                viewModel.phoneNumberData(
                    ccp.fullNumberWithPlus.toString(),
                    ccp.formattedFullNumber.toString()
                )
                findNavController().navigate(R.id.action_mobileNoScreen3_to_otpScreenFragment)

            }
        })


        // TODO: Use the ViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(requireActivity()).getHintPickerIntent(hintRequest)

        startIntentSenderForResult(
            intent.intentSender,
            PHONE_NUMBER_FETCH_REQUEST_CODE,
            null,
            0,
            0,
            0,
            null
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PHONE_NUMBER_FETCH_REQUEST_CODE) {
            data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id?.let {
//                useFetchedPhoneNumber(it)
                ccp.fullNumber = it;
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun GetCarriorsInformation() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        subInfoList = mSubscriptionManager!!.activeSubscriptionInfoList
//        if ((subInfoList as MutableList<SubscriptionInfo>?)!!.size > 1) {
//            isMultiSimEnabled = true
//        }
//        for (subscriptionInfo in (subInfoList as MutableList<SubscriptionInfo>?)!!) {
//            Numbers!!.add(subscriptionInfo.number)
//            Toast.makeText(requireContext(), subscriptionInfo.number.toString(), Toast.LENGTH_SHORT).show()
//        }
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = fieldPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length != 10) {
            fieldPhoneNumber.error = "Invalid phone number."
            return false
        }

        return true
    }


}