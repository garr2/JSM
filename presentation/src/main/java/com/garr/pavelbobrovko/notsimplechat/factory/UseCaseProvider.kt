package com.garr.pavelbobrovko.notsimplechat.factory

import com.garr.pavelbobrovko.notsimplechat.app.JSMApp
import com.garr.pavelbobrovko.notsimplechat.executor.UIThread
import com.pavelbobrovko.garr.data.net.AuthienticationService
import com.pavelbobrovko.garr.data.net.FileUploadService
import com.pavelbobrovko.garr.data.net.OAuth2Service
import com.pavelbobrovko.garr.data.net.RestApi
import com.pavelbobrovko.garr.data.repository.AuthienticationRepositoryImpl
import com.pavelbobrovko.garr.data.repository.FileUploadRepositoryImpl
import com.pavelbobrovko.garr.data.repository.OAuthRepositoryImpl
import com.pavelbobrovko.garr.domain.usecases.AuthenticateUseCase
import com.pavelbobrovko.garr.domain.usecases.FileUploadUseCase
import com.pavelbobrovko.garr.domain.usecases.OAuthUseCase
import io.reactivex.schedulers.Schedulers

public object UseCaseProvider {


    //private val authService = AuthienticationService(RestApi.apiKey)

    val uiThread = UIThread()

    fun provideAutenticationUseCase(): AuthenticateUseCase{
        return AuthenticateUseCase(uiThread
                ,AuthienticationRepositoryImpl(AuthienticationService(RestApi.restAuthUrl)))
    }

    fun provideFileUploadUseCase(): FileUploadUseCase{
        return FileUploadUseCase(uiThread
                ,FileUploadRepositoryImpl(FileUploadService(RestApi.googleCloudUrl)))
    }

    fun provideOAuthUseCase(): OAuthUseCase{
        return OAuthUseCase(uiThread,OAuthRepositoryImpl)
    }

}

