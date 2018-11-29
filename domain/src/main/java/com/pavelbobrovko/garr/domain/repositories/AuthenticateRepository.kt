package com.pavelbobrovko.garr.domain.repositories

import android.app.Activity
import android.content.Context
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User
import io.reactivex.Observable

interface AuthenticateRepository: BaseRepository {

    fun registerByEmail(email: String, pass: String): Observable<RegistrationUserData>

    fun signinByEmail(email: String, pass: String): Observable<RegistrationUserData>

    fun signinByGoogle(idToken: String, providerId: String,requestUri: String): Observable<RegistrationUserData>

    fun getOAuth(context: Context, displayName: String, accountType: String, activity: Activity): Observable<String>
}