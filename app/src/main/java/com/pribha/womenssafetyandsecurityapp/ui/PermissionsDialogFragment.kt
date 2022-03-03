package com.pribha.womenssafetyandsecurityapp.ui

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.viewmodel.DataStoreViewModel

class PermissionsDialogFragment : DialogFragment() {

    companion object {

        const val TAG = "SimpleDialog"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"

        private lateinit var givePermissionButton: MaterialButton
        private lateinit var dataStoreViewModel: DataStoreViewModel

        fun newInstance(title: String, subTitle: String): PermissionsDialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = PermissionsDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(R.layout.fragment_permissions_dialog, container, false)

        givePermissionButton = view.findViewById(R.id.materialButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)


        dataStoreViewModel = ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupView(view: View) {
//        view.tvTitle.text = arguments?.getString(KEY_TITLE)
//        view.tvSubTitle.text = arguments?.getString(KEY_SUBTITLE)
    }

    private fun setupClickListeners(view: View) {
        givePermissionButton.setOnClickListener {

            dataStoreViewModel.readAllPermissionGrantedFromDataStore.observe(this, { allPermissionGranted ->

                if (allPermissionGranted == true) {

                    Toast.makeText(requireActivity(), "dhd sgfgdd hfhhgf", Toast.LENGTH_SHORT).show()
                }
                else{



                    var count: Int = 0
                    val requestMultiplePermissions = requireActivity().registerForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions())
                    { permissions ->
                        permissions.entries.forEach {

                            if (it.value)
                            {
                                count++
                            }
                            if (count == 3)
                            {
                                dataStoreViewModel.saveAllPermissionGrantedToDataStore(true)
                            }
                            Toast.makeText(requireActivity(), "${it.key} = ${it.value}", Toast.LENGTH_SHORT).show()
//                    Log.e("DEBUG101", "fgffg"+"${it.key} = ${it.value}")

                        }
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        requestMultiplePermissions.launch(
                            arrayOf(
                                android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.SEND_SMS,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                android.Manifest.permission.READ_PHONE_STATE


                            )
                        )
                    }else{

                        requestMultiplePermissions.launch(
                            arrayOf(
                                android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.SEND_SMS,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.READ_PHONE_STATE



                            )
                        )

                    }


                }

            })





            dismiss()
        }
//        view.btnNegative.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
    }

}