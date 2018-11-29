package com.garr.pavelbobrovko.notsimplechat.presentation.screen.userSettings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseRouter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.subscribeBy

class UserSettingsRouter(activity: UserSettingsActivity): BaseRouter<UserSettingsActivity>(activity) {

    @SuppressLint("CheckResult")
    fun startCameraActivity(){
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe{granted ->
                    if (granted){
                        activity.openCamera()
                    }else showError(Throwable("no needed permission."))
                }
    }

    @SuppressLint("CheckResult")
    fun startGaleryActivity(){
        RxPermissions(activity)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe{granted ->
                    if (granted){
                        val galleryIntent = Intent(Intent.ACTION_PICK)
                        galleryIntent.type = "image/*"
                        activity.startActivityForResult(galleryIntent, activity.GALLERY_REQUEST_CODE)
                    }else showError(Throwable("no needed permission."))
                }
    }

    fun setResultAndFinish(result: Int = Activity.RESULT_CANCELED){
        activity.setResult(result)
        finishApp()
    }

    fun finishApp(){
        activity.finish()
    }
}