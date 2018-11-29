package com.pavelbobrovko.garr.data.entity.auth

data class EmailAuthRequest(val email: String, val password: String
                            , val returnSecureToken: Boolean = true) {
}