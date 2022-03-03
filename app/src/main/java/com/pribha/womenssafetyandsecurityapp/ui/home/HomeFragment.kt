package com.pribha.womenssafetyandsecurityapp.ui.home

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.ui.PermissionsDialogFragment
import com.pribha.womenssafetyandsecurityapp.viewmodel.DataStoreViewModel


class HomeFragment : Fragment() {


    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var helpButton: RelativeLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var firstTimeDialog: LinearLayout
    private lateinit var addNow: MaterialButton
    private  lateinit var helpText: TextView
    private var mobileNumberList = mutableListOf<String>()

    private var smsString: String = "Hello your friend needs your help \\n Here is the location:\"\n"
    private var simCardSlot1 : Boolean = true



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        helpButton = root.findViewById(R.id.buttonHelp)
        fab = root.findViewById(R.id.floating_action_button)
//        })
        firstTimeDialog = root.findViewById(R.id.linearLayout)
        addNow = root.findViewById(R.id.materialButton)
        helpText = root.findViewById(R.id.textView10)
        return root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val db = Firebase.firestore
        val docRef =db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .collection("contacts")


        getReadySMS()


        val intent = requireActivity().intent
        var value: String? = "no"
           value =  intent.extras?.getString("getHelp")

        if (value == "yes")
        {
            getLocation()
        }



        dataStoreViewModel = ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)
        dataStoreViewModel.readAllPermissionGrantedFromDataStore.observe(
            requireActivity(),
            { allPermissionGranted ->

                if (allPermissionGranted == true) {

//                    Toast.makeText(requireActivity(), "dhd sgfgdd hfhhgf", Toast.LENGTH_SHORT)
//                        .show()


                }

            })


        dataStoreViewModel.readAllPermissionGrantedFromDataStore.observe(
            requireActivity(),
            { allpermissionGranted ->

                if (allpermissionGranted == true) {
                    firstTimeDialog.visibility = View.GONE
                    helpButton.visibility = View.VISIBLE
                    helpText.visibility = View.VISIBLE

                } else {
                    firstTimeDialog.visibility = View.VISIBLE
                    helpButton.visibility = View.GONE
                    helpText.visibility = View.GONE

                }

            })


        addNow.setOnClickListener(View.OnClickListener {

            PermissionsDialogFragment.newInstance("dff", "fdvd").show(
                requireActivity().supportFragmentManager,
                PermissionsDialogFragment.TAG
            )


        })





        fab.setOnClickListener(View.OnClickListener {

            YoYo.with(Techniques.RotateIn)
                .duration(700)
                .repeat(0)
                .playOn(view.findViewById(R.id.floating_action_button));


//
//
//             val requestMultiplePermissions = requireActivity().registerForActivityResult(
//                ActivityResultContracts.RequestMultiplePermissions())
//            { permissions ->
//                permissions.entries.forEach {
//
//                    Toast.makeText(requireActivity(), "${it.key} = ${it.value}", Toast.LENGTH_SHORT).show()
////                    Log.e("DEBUG101", "fgffg"+"${it.key} = ${it.value}")
//
//                }
//            }
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                requestMultiplePermissions.launch(
//                    arrayOf(
//                        android.Manifest.permission.READ_CONTACTS,
//                        android.Manifest.permission.SEND_SMS,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
//
//
//                    )
//                )
//            }else{
//
//                requestMultiplePermissions.launch(
//                    arrayOf(
//                        android.Manifest.permission.READ_CONTACTS,
//                        android.Manifest.permission.SEND_SMS,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
//
//
//                    )
//                )
//
//            }


        })

        helpButton.setOnClickListener(View.OnClickListener {

// Source can be CACHE, SERVER, or DEFAULT.
            val source = Source.CACHE


            docRef.get(source)
                .addOnSuccessListener { documents ->

                    if (documents.isEmpty) {
                        Toast.makeText(
                            requireContext(),
                            "Please add contacts for emergency",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {


                        YoYo.with(Techniques.Tada)
                            .duration(700)
                            .repeat(2)
                            .playOn(view.findViewById(R.id.buttonHelp));

                        customVibratePatternNoRepeat()
//                        sendDirectSMS()
                        getLocation()


                    }




                }


        })



    }



    private fun getLocation() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.

                sendDirectSMS(location!!)
            }



    }

    private fun customVibratePatternNoRepeat() {

        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 0 : Start without a delay
        // 400 : Vibrate for 400 milliseconds
        // 200 : Pause for 200 milliseconds
        // 400 : Vibrate for 400 milliseconds
        val mVibratePattern = longArrayOf(300, 400, 300, 400, 300, 400, 300)

        // -1 : Do not repeat this pattern
        // pass 0 if you want to repeat this pattern from 0th index
//        vibrator.vibrate(mVibratePattern, -1)


        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern, -1))
        } else {
            vibrator.vibrate(mVibratePattern, -1)
        }

    }


    private fun getReadySMS(){

        val db = Firebase.firestore
        val docRef =db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .collection("contacts")


        val source = Source.CACHE



        docRef.get(source)
            .addOnSuccessListener { documents ->


                for (num in 1..documents.size())
                {
                    mobileNumberList.add(documents.documents[num - 1].get("phoneNo").toString())
                }


            }


//        Toast.makeText(requireContext(), mobileNumberList.get(1).toString(), Toast.LENGTH_SHORT).show()

    }


    private fun sendDirectSMS( currentLocation: Location) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(
            requireActivity(), 0, Intent(
                SENT
            ), 0
        )
        val deliveredPI = PendingIntent.getBroadcast(
            requireActivity(), 0,
            Intent(DELIVERED), 0
        )



        // SEND BroadcastReceiver
        val sendSMS: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(requireContext(), "SMS Delivered", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        Toast.makeText(requireContext(), "Sending SMS failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        Toast.makeText(requireContext(), "No Service", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        Toast.makeText(requireContext(), "No Service", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        Toast.makeText(requireContext(), "No Service", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // DELIVERY BroadcastReceiver
        val deliverSMS: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        requireContext(), "SMS Delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        requireContext(), "SMS not Delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        requireActivity().registerReceiver(sendSMS, IntentFilter(SENT))
        requireActivity().registerReceiver(deliverSMS, IntentFilter(DELIVERED))

        val smsBody = StringBuffer()
        smsBody.append("https://www.google.com/maps/search/?api=1&query=")
        smsBody.append(currentLocation.getLatitude())
        smsBody.append("%2C")
        smsBody.append(currentLocation.getLongitude())

        //https://www.google.com/maps/search/?api=1&query=47.5951518%2C-122.3316393
        smsString += smsBody.toString()

        val smsText: String = smsString

          val localSubscriptionManager = requireActivity().getSystemService(SubscriptionManager::class.java)
//        val localSubscriptionManager = requireActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        val localSubscriptionManager = SubscriptionManager.from(context)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
            val localList: List<*> = localSubscriptionManager.activeSubscriptionInfoList
            val simInfo1 = localList[0] as SubscriptionInfo
            val simInfo2 = localList[1] as SubscriptionInfo




            for (number in mobileNumberList)
            {


                if (simCardSlot1)
                {
                    //SendSMS From SIM One
                    SmsManager.getSmsManagerForSubscriptionId(simInfo1.subscriptionId)
                        .sendTextMessage(number, null, smsText, sentPI, deliveredPI)


                }
                else{


                    //SendSMS From SIM Two
                    SmsManager.getSmsManagerForSubscriptionId(simInfo2.subscriptionId)
                        .sendTextMessage(number, null, smsText, sentPI, deliveredPI)

                }
            }



        }
    }

}