package com.garr.pavelbobrovko.notsimplechat.presentation.screen.login

import android.accounts.Account
import android.content.Intent
import android.util.Log
import com.garr.pavelbobrovko.notsimplechat.app.JSMApp
import com.garr.pavelbobrovko.notsimplechat.factory.UseCaseProvider
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseRouter
import com.google.android.gms.auth.GoogleAuthUtil
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import io.reactivex.rxkotlin.subscribeBy

class LoginRouter(activity: LoginActivity): BaseRouter<LoginActivity>(activity) {

    fun finishApp(){
        activity.finish()
    }

    fun setResultAndFinish(user: RegistrationUserData, result: Int){
        activity.setResult(result, Intent().putExtra((ConstantInterface.RESULT), user))
        finishApp()
    }

    fun startGoogleRegistrationActivity(){
        activity.startGoogleRegistrationActivity()
    }

    fun getOAuthToken(displayName: String){
        val useCase = UseCaseProvider.provideAutenticationUseCase()
        /*useCase.getOAuth2Token(displayName,"google.com",activity)
                .subscribeBy (
                        onNext = {
                          Log.d(ConstantInterface.LOG_TAG,it)
                        },
                        onError = {

                        }
                )*/
        /*useCase.getOAuth2Token(JSMApp.instance,displayName,"google.com",activity)
                .subscribeBy(
                        onNext = {
                            Log.d(ConstantInterface.LOG_TAG,it)
                        },
                        onError = {

                        }
                )*/
        this.displayName = displayName
        Thread(runnable).start()

    }

    var displayName: String = ""
    val runnable = object : Runnable{

        override fun run() {
            val account = Account(displayName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
            Log.d(ConstantInterface.LOG_TAG,"oAuth: " + GoogleAuthUtil.getToken(activity,account,"oauth2:" + "https://www.googleapis.com/auth/cloud-platform"))
        }

    }
}