package com.pavelbobrovko.garr.data.net

import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FileUploadService(apiUrl: String) {

    private val restApi: FileUploadApi

    init {
        val okHttpBuilder = OkHttpClient.Builder()


        okHttpBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        val gson = Gson()

        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpBuilder.build())
                .build()

        restApi = retrofit.create(FileUploadApi::class.java)
    }

    fun uploadFile(path: String,idToken: String,name: String, file: ByteArray): Observable<String>{
       return restApi.uploadFile( bytes = file, idToken = "Bearer ya29.GltUBjY6kfvwSBsClsU1R47BfqCAzix1zVVeOurI5UP8rUc0ogSVniC5cYZedtYvuaNLcUWIljG6K9viDNaIRimRciA58X528e93XgXKe-Mb-QV1znGVYIfDrYPJ", name = path)
    }
}