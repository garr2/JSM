package com.pavelbobrovko.garr.domain.usecases

import com.pavelbobrovko.garr.domain.executor.PostExecutorThread
import com.pavelbobrovko.garr.domain.repositories.OAuthRepository
import io.reactivex.Observable

class OAuthUseCase(postExecutorThread: PostExecutorThread,
                   private val oAuthRepository: OAuthRepository): BaseUseCase(postExecutorThread) {

    fun getOAuth(clientId: String, redirectUri: String): Observable<String>{
       return oAuthRepository.getOAuth2Token(clientId,redirectUri)
               .observeOn(postExecutorThread)
               .subscribeOn(workExecutorThread)
    }
}