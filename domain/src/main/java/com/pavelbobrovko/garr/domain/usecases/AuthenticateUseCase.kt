package com.pavelbobrovko.garr.domain.usecases

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User
import com.pavelbobrovko.garr.domain.executor.PostExecutorThread
import com.pavelbobrovko.garr.domain.repositories.AuthenticateRepository
import io.reactivex.Observable

class AuthenticateUseCase(postExecutorThread: PostExecutorThread
, private val authenticateRepository: AuthenticateRepository): BaseUseCase(postExecutorThread) {

    fun registrationByEmail(email: String, pass: String): Observable<RegistrationUserData>{
        return authenticateRepository.registerByEmail(email,pass)
                .observeOn(postExecutorThread)
                .subscribeOn(workExecutorThread)
    }

    fun signinByEmail(email: String, pass: String): Observable<RegistrationUserData>{
        return authenticateRepository.signinByEmail(email,pass)
                .observeOn(postExecutorThread)
                .subscribeOn(workExecutorThread)
    }

    fun  signinByGoogle(idToken: String, providerId: String,requestUri: String): Observable<RegistrationUserData>{
        return authenticateRepository.signinByGoogle(idToken, providerId,requestUri)
                .observeOn(postExecutorThread)
                .subscribeOn(workExecutorThread)
    }

    fun getOAuth(context: Context,displayName: String, accountType: String, activity: Activity): Observable<String>{
        return authenticateRepository.getOAuth(context,displayName,accountType,activity)
    }
}