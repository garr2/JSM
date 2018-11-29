package com.garr.pavelbobrovko.notsimplechat.presentation.screen.userSettings

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.net.Uri
import android.util.Log
import com.garr.pavelbobrovko.notsimplechat.app.JSMApp
import com.garr.pavelbobrovko.notsimplechat.factory.UseCaseProvider
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseViewModel
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import io.reactivex.rxkotlin.subscribeBy

class UserSettingsViewModel: BaseViewModel<UserSettingsRouter>() {

    val etName = ObservableField<String>("")
    val etAbout = ObservableField<String>("")
    val ivPhoto = ObservableField<String>("")
    val isNameValid = ObservableBoolean(false)

    private var isNewPhotoAvaliable = false
    private lateinit var localPhotoUrl:Uri

    private val fileUploadUseCase = UseCaseProvider.provideFileUploadUseCase()

    private lateinit var userData: RegistrationUserData
    private var user =  User()

    fun setUserData(userData: RegistrationUserData){
        this.userData = userData
        if (userData.user!= null){
            user = userData.user!!
            etName.set(user.displayName)
            etAbout.set(user.about)
            ivPhoto.set(user.avatarURL)
        }
    }

    //FIXME обработать возврат ссылки на изображение
    fun onClickCamera(){
        router?.startCameraActivity()
    }

    //FIXME обработать возврат ссылки на изображение
    fun onClickGalery(){
        router?.startGaleryActivity()
    }

    fun onClickSave(){
        if (isNewPhotoAvaliable){
            val path = "images/" + localPhotoUrl.lastPathSegment
            val idToken = "Bearer " + userData.oauthIdToken
            addToDisposable(fileUploadUseCase.uploadFile(JSMApp.instance, path,idToken
                    ,localPhotoUrl).subscribeBy (
                    onNext = {
                        Log.d(ConstantInterface.LOG_TAG, "file uploaded url: $it")
                    },
                    onError = {
                        router?.showError(it)
                    }
            ))
        }
    }

    fun onClickCancel(){
        router?.setResultAndFinish()
    }

    fun setNewPhotoUrl(localUrl: Uri){
        localPhotoUrl = localUrl
        ivPhoto.set(localUrl.toString())
        isNewPhotoAvaliable = true
    }


}