package com.pavelbobrovko.garr.data.entity.oAuth

import com.google.gson.annotations.SerializedName

data class OAuthResponse(@SerializedName("access_token")
                         val accessToken: String = "",
                         @SerializedName("refresh_token")
                         val refreshToken: String = "",
                         @SerializedName("expires_in")
                         val expiresId: Int = 0,
                         @SerializedName("token_type")
                         val tokenType: String = "")