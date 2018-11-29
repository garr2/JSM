package com.pavelbobrovko.garr.domain.repositories

import android.content.Context
import android.net.Uri
import io.reactivex.Observable

interface FileUploadRepository: BaseRepository {

    fun uploadFile(context: Context, path: String, idToken:String, localPhotoUrl: Uri): Observable<String>
}