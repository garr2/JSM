package com.pavelbobrovko.garr.data.net

import io.reactivex.Observable
import retrofit2.http.*

interface FileUploadApi {

    @POST("o")
    fun uploadFile(@Header("Content-Type") mediaType: String = "image/jpeg"
                    ,@Header("Authorization") idToken: String
                    , @Body bytes: ByteArray
                    //,@Query("alt")type:String = "media"
                   ,@Query("uploadType")uploadType: String = "media"
                    ,@Query("name") name: String

    ): Observable<String>
}