package com.pribha.womenssafetyandsecurityapp.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    // TODO: Implement the ViewModel


     var phoneNumberggg: String = ""


    private val phoneNumber = MutableLiveData<String>()
    private val phoneNumberFormatted = MutableLiveData<String>()
    private val otpNumber = MutableLiveData<String>()
    private val verificationId = MutableLiveData<String>()
    private val yourName = MutableLiveData<String>()

    private var verificationIdNew: String = ""

    private val verifyPhoneNo = MutableLiveData<Boolean>()
    private val signInSuccessFul = MutableLiveData<Boolean>()
    private val invalidCode = MutableLiveData<Boolean>()
    private val resendCode = MutableLiveData<Boolean>()


    fun phoneNumberData(phoneNumber: String, phoneNumberFormatted: String){

        this.phoneNumber.value = phoneNumber
        this.phoneNumberFormatted.value =  phoneNumberFormatted
        phoneNumberggg = phoneNumberFormatted
    }


    fun getPhoneNumberFormatted(): LiveData<String?>? {
        return phoneNumberFormatted
    }


    fun getPhoneNumber(): LiveData<String?>? {
        return phoneNumber
    }




    fun setOtpNumber(otpNumber: String){

        this.otpNumber.value = otpNumber
    }


    fun getOtpNumber(): LiveData<String?>? {
        return otpNumber
    }


    fun setVerifyPhoneNo(verifyPhoneNo: Boolean){

        this.verifyPhoneNo.value = verifyPhoneNo
    }


    fun getVerifyPhoneNo(): LiveData<Boolean?>? {
        return verifyPhoneNo
    }


    fun setSignInSuccessFul(signInSuccessful: Boolean){
        this.signInSuccessFul.value = signInSuccessful
    }


    fun getSignInSuccessFul(): LiveData<Boolean?>? {
        return signInSuccessFul
    }


    fun setVerificationId(verificationId: String){
        this.verificationId.value = verificationId
    }


    fun getVerificationId(): LiveData<String?>? {
        return verificationId
    }



    fun setVerificationIdNew(verificationId: String){
        this.verificationIdNew = verificationId
    }

    fun getVerificationIdNew(): String? {
        return verificationIdNew
    }


    fun setInvalidCode(invalidCode: Boolean){
        this.invalidCode.value = invalidCode
    }


    fun getInvalidCode(): LiveData<Boolean?>? {
        return invalidCode
    }


    fun setResendCode(resendCode: Boolean){
        this.resendCode.value = resendCode
    }


    fun getResendCode(): LiveData<Boolean?>? {
        return resendCode
    }


    fun setYourName(yourName: String){
        this.yourName.value = yourName
    }


    fun getYourName(): LiveData<String?>? {
        return yourName
    }


}