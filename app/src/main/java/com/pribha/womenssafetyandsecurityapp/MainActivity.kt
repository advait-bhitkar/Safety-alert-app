package com.pribha.womenssafetyandsecurityapp

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.pribha.womenssafetyandsecurityapp.helper.LockService
import com.pribha.womenssafetyandsecurityapp.helper.shakeDetector.ShakeDetector
import com.pribha.womenssafetyandsecurityapp.helper.shakeDetector.ShakeOptions
import com.pribha.womenssafetyandsecurityapp.onboarding.OnboardingActivity
import com.pribha.womenssafetyandsecurityapp.ui.contacts.ContactsFragment
import com.pribha.womenssafetyandsecurityapp.viewmodel.DataStoreViewModel


class MainActivity : AppCompatActivity() {

    // The following are used for the shake detection
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null
    private var shakeDetector: ShakeDetector? = null


    private lateinit var dataStoreViewModel: DataStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        // Make sure this is before calling super.onCreate




        setTheme(R.style.WomensSafetyAndSecurityApp);


        val intent = intent
        var value: String? = "no"
        value =  intent.extras?.getString("getHelp")

        if (value == "yes")
        {
            Log.d("LOB", "Activity started successfully")
        }


        dataStoreViewModel = ViewModelProvider(this).get(DataStoreViewModel::class.java)
        dataStoreViewModel.readIsFirstLaunchFromDataStore.observe(this, { isFirstTime ->

            if (isFirstTime == true) {

                val i = Intent(this, OnboardingActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)

            }

        })

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        enableOfflineData()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment2, R.id.contactsFragment2, R.id.notificationsFragment3
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.itemIconTintList = null;


        val options = ShakeOptions()
            .background(true)
            .interval(1000)
            .shakeCount(2)
            .sensibility(2.0f)





//        shakeDetector = ShakeDetector(options).start(this, object : ShakeCallback {
//            override fun onShake() {
//                Log.d("event", "onShake")
//            }
//        })

        shakeDetector = ShakeDetector(options).start(this)

        startService(Intent(applicationContext, LockService::class.java))


    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        shakeDetector?.destroy(baseContext)
        super.onDestroy()
    }


    //  Your activity's onActivityResult()
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val lsActiveFragments: List<Fragment> = supportFragmentManager.fragments
        for (fragmentActive in lsActiveFragments) {
            if (fragmentActive is NavHostFragment) {
                val lsActiveSubFragments: List<Fragment> =
                    fragmentActive.getChildFragmentManager().getFragments()
                for (fragmentActiveSub in lsActiveSubFragments) {
                    if (fragmentActiveSub is ContactsFragment) {
                        fragmentActiveSub.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
        }
    }


    fun enableOfflineData(){

        val db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings


    }


}