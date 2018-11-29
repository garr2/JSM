package com.garr.pavelbobrovko.notsimplechat.presentation.screen.login

import android.app.Activity
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.garr.pavelbobrovko.notsimplechat.R
import com.garr.pavelbobrovko.notsimplechat.app.JSMApp
import com.garr.pavelbobrovko.notsimplechat.factory.UseCaseProvider
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pavelbobrovko.garr.data.net.OAuth2Api
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import io.reactivex.rxkotlin.subscribeBy

class LoginViewModel: BaseViewModel<LoginRouter>() {

    val etEmail = ObservableField<String>("")
    val etPass = ObservableField<String>("")
    val errorEmail = ObservableField<String>("")
    val errorPass = ObservableField<String>("")
    val errorEmailVisibility = ObservableInt(View.GONE)
    val errorPassVisibility = ObservableInt(View.GONE)
    //var etEmail = ""
   // var etPass = ""

    private val authenticateUseCase = UseCaseProvider.provideAutenticationUseCase()
    private val oAuthUseCase = UseCaseProvider.provideOAuthUseCase()

    fun onOkButtonClick() {
        Log.d(ConstantInterface.LOG_TAG, "onOkButtonClick")
        signInByEmail()
    }

    fun onCancelButtonClick(){
        Log.d(ConstantInterface.LOG_TAG, "onCancelButtonClick")
        router?.setResultAndFinish(RegistrationUserData(),Activity.RESULT_CANCELED)
    }

    fun onGoogleButtonClick(){
        Log.d(ConstantInterface.LOG_TAG, "onGoogleButtonClick")
        router?.startGoogleRegistrationActivity()
    }

    fun signInByEmail(){
        var email: String = ""
        var pass: String = ""

        if (!TextUtils.isEmpty(etEmail.get().toString()) && etEmail.get().toString().contains("@", true)) {
            email = etEmail.get().toString()
        }else if (TextUtils.isEmpty(etEmail.get().toString())){
            errorEmail.set("Email field cannot be empty!")
            errorEmailVisibility.set(View.VISIBLE)
        }else {
            errorEmail.set("Invalid email!")
            errorEmailVisibility.set(View.VISIBLE)
        }

        if (!TextUtils.isEmpty(etEmail.get().toString())){
            pass = etPass.get().toString()
        }else{
            errorPass.set("Password field cannot be empty!")
            errorPassVisibility.set(View.VISIBLE)
        }

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            addToDisposable(authenticateUseCase.signinByEmail(email, pass).subscribeBy(
                    onNext = {
                        if (it.userId != "") {
                            getOAuthToken(it)
                        } else {
                            registerByEmail()
                        }
                    }, onError = {
                router?.showError(it)
                registerByEmail()
            }
            ))
        }
    }

    fun registerByEmail(){
       val disposable = authenticateUseCase.registrationByEmail(etEmail.get().toString(),etPass.get().toString())
                .subscribeBy (
                        onNext = {
                            if (it.userId != ""){
                                getOAuthToken(it)
                            }else{
                                router?.showError(Throwable("Registration error"))
                            }
                        },
                        onError = {
                            router?.showError(it)
                        }
                )
        addToDisposable(disposable)
    }

    fun googleRegistrationResult(res: GoogleSignInAccount){
        val disposable = authenticateUseCase.signinByGoogle(res.idToken.toString()
                ,"google.com","http://notsimplechat-5a970.firebaseapp.com")
                .subscribeBy (
                        onNext = {
                            if (it.userId != ""){
                                getOAuthToken(it)
                                //router?.setResultAndFinish(it,Activity.RESULT_OK)
                            }else{
                                router?.showError(Throwable("Registration error"))
                            }
                        },
                        onError = {
                            router?.showError(it)
                        }
                )
        addToDisposable(disposable)
    }

    fun getOAuthToken(registrationUserData: RegistrationUserData){
        //FIXME  Перенести в NDK
        val secret = "pAZGe3ZdB9Ev0D9qox2Ae2Bpea4cndy11XqrNdTx"
        val disposable = oAuthUseCase
                .getOAuth(JSMApp.instance.getString(R.string.debug_client_id),
                        secret)
                .subscribeBy(
                        onNext = {
                            registrationUserData.oauthIdToken = it
                            router?.setResultAndFinish(registrationUserData,Activity.RESULT_OK)
                        },
                        onError = {
                            router?.showError(it)
                        }

                )
        addToDisposable(disposable)
    }
}