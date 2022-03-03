package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var locationManager: LocationManager
private val locationPermissionCode = 2



class ScreenReceiver : BroadcastReceiver() {
    private val count1 = false
    private val count2 = false
    private val count3 = false
    private val milllisInFuture: Long = 1000
    private val tick1: Long = 0
    var doubleBackToExitPressedOnce = false

    private var mobileNumberList = mutableListOf<String>()

    private var smsString: String = "Hello your friend needs your help \n Here is the location:"
    private var simCardSlot1 : Boolean = true

    private lateinit var context: Context

    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onReceive(context: Context, intent: Intent) {
        Log.e("LOB", "onReceive")

        this.context = context
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // do whatever you need to do here
            wasScreenOn = false
            Log.d("LOB", "wasScreenOn" + wasScreenOn)
            if (doubleBackToExitPressedOnce) {
                Log.d("LOB", "HelpMe" + wasScreenOn)
                getReadySMS()
                getLocation()
                return
            }
            doubleBackToExitPressedOnce = true
            //            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            // and do whatever you need to do here
            wasScreenOn = true

//        } else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
//            Log.e("LOB","userpresent");
//            Log.e("LOB","wasScreenOn"+wasScreenOn);
//            String url = "http://www.stackoverflow.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setData(Uri.parse(url));
//            context.startActivity(i);
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


//        Toast.makeText(context, mobileNumberList.get(1).toString(), Toast.LENGTH_SHORT).show()

    }

    private fun getLocation() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
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



    private fun sendDirectSMS(currentLocation: Location) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(
            context, 0, Intent(
                SENT
            ), 0
        )
        val deliveredPI = PendingIntent.getBroadcast(
            context, 0,
            Intent(DELIVERED), 0
        )


        val smsBody = StringBuffer()
        smsBody.append("https://www.google.com/maps/search/?api=1&query=")
        smsBody.append(currentLocation.getLatitude())
        smsBody.append("%2C")
        smsBody.append(currentLocation.getLongitude())

        //https://www.google.com/maps/search/?api=1&query=47.5951518%2C-122.3316393
        smsString += smsBody.toString()



        // SEND BroadcastReceiver
        val sendSMS: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(context, "SMS Delivered", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        Toast.makeText(context, "Sending SMS failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // DELIVERY BroadcastReceiver
        val deliverSMS: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        context, "SMS Delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        context, "SMS not Delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        context.registerReceiver(sendSMS, IntentFilter(SENT))
        context.registerReceiver(deliverSMS, IntentFilter(DELIVERED))
        val smsText: String = smsString

        val localSubscriptionManager = context.getSystemService(SubscriptionManager::class.java)
//        val localSubscriptionManager = requireActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        val localSubscriptionManager = SubscriptionManager.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
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


    companion object {
        var wasScreenOn = true
    }
}
