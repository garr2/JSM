package com.pavelbobrovko.garr.domain.usecases

import android.content.Context
import android.net.Uri
import com.pavelbobrovko.garr.domain.executor.PostExecutorThread
import com.pavelbobrovko.garr.domain.repositories.AuthenticateRepository
import com.pavelbobrovko.garr.domain.repositories.FileUploadRepository
import io.reactivex.Observable

class FileUploadUseCase(postExecutorThread: PostExecutorThread
                        , private val fileUploadRepository: FileUploadRepository): BaseUseCase(postExecutorThread) {

    fun uploadFile(context: Context, path: String,idToken: String, localPhotoUrl: Uri): Observable<String>{
        return fileUploadRepository.uploadFile(context, path,idToken,localPhotoUrl)
                .observeOn(postExecutorThread)
                .subscribeOn(workExecutorThread)
    }
}