package com.pavelbobrovko.garr.data.entity.auth

data class EmailAuthResponse(val kind: String = "", val idToken: String = "",
                             val email: String = "", val refreshToken: String = "",
                             val localId: String = "", val registred: Boolean = false) {
}