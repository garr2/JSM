package com.pavelbobrovko.garr.data.entity.auth

data class OAuthSignRequest(val requestUri: String = "", val postBody: String = ""
                            , val returnSecureToken: Boolean = true
                            , val returnIdpCredential: Boolean = true) {
}