package com.pribha.womenssafetyandsecurityapp.onboarding.login.enternamescreen

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.pribha.womenssafetyandsecurityapp.MainActivity
import com.pribha.womenssafetyandsecurityapp.R
import com.pribha.womenssafetyandsecurityapp.onboarding.SharedViewModel
import com.pribha.womenssafetyandsecurityapp.viewmodel.DataStoreViewModel

class EnterNameScreen : Fragment() {

    companion object {
        fun newInstance() = EnterNameScreen()
    }

    private lateinit var viewModel: SharedViewModel
    private lateinit var yourName: TextView
    private lateinit var goToAppButton: MaterialButton

    private lateinit var dataStoreViewModel: DataStoreViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_enter_name, container, false)

        yourName = view.findViewById(R.id.editTextTextPersonName)
        goToAppButton = view.findViewById(R.id.gotoapp)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        dataStoreViewModel = ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)

        goToAppButton.setOnClickListener(View.OnClickListener {

            if (!yourName.text.isEmpty()) {
                viewModel.setYourName(yourName.text.toString())
                val intent = Intent (getActivity(), MainActivity::class.java)
                startActivity(intent)

                dataStoreViewModel.saveIsFirstLaunchToDataStore(false)

            }


        })

    }

}