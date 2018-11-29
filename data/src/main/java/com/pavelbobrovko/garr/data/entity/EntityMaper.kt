package com.pavelbobrovko.garr.data.entity

import com.pavelbobrovko.garr.data.entity.auth.EmailAuthResponse
import com.pavelbobrovko.garr.data.entity.auth.OAuthSigninResponse
import com.pavelbobrovko.garr.data.entity.oAuth.OAuthResponse
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User

fun EmailAuthResponse.toUser(): RegistrationUserData{
    return RegistrationUserData(userId = localId,email = email,isEmailVerify = registred, user = null)
}

fun OAuthSigninResponse.toUser(): RegistrationUserData {
    return RegistrationUserData(userId = localId,email = email,isEmailVerify = emailVerified
    ,oauthIdToken = idToken,user = User(displayName,photoUrl,""))
}

fun OAuthResponse.toResultString(): String{
    return accessToken
}

