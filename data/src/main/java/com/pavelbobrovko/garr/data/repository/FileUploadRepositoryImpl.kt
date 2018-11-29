package com.pavelbobrovko.garr.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.pavelbobrovko.garr.data.net.FileUploadService
import com.pavelbobrovko.garr.data.net.RestApi
import com.pavelbobrovko.garr.domain.repositories.FileUploadRepository
import io.reactivex.Observable
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths


class FileUploadRepositoryImpl(val restApi: FileUploadService): FileUploadRepository {

    override fun uploadFile(context: Context, path: String, idToken: String, localPhotoUrl: Uri): Observable<String> {

       /*val bitmap = BitmapFactory.decodeFile(localPhotoUrl.toString())
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        val array: Array<Byte> = stream.toByteArray() as Array<Byte>
        val uri = URI(localPhotoUrl.toString())
        val file = File(localPhotoUrl.path, localPhotoUrl.lastPathSegment)
        val fileInputStream = FileInputStream(file)
        val bytes = ByteArray(file.length() as Int)
        fileInputStream.read(bytes)*/

        val iStream = context.contentResolver.openInputStream(localPhotoUrl)
        val inputData = iStream.readBytes()
        iStream.read(inputData)

        var name = localPhotoUrl.lastPathSegment

        return restApi.uploadFile(path,idToken,name,inputData)
    }

    /*@Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        inputStream.read(buffer)
        var len = 0
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }*/

}