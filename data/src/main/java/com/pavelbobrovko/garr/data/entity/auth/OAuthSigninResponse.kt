package com.pavelbobrovko.garr.data.entity.auth

data class OAuthSigninResponse(val kind: String = "", val federatedId: String = ""
, val providerId: String = "", val localId: String = "", val emailVerified: Boolean = false
, val email: String = "", val oauthIdToken: String = "", val firstName: String = ""
, val lastName: String = "", val fullName: String = "", val displayName: String = ""
, val photoUrl: String = "", val idToken: String = "") {
}