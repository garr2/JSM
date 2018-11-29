package com.garr.pavelbobrovko.notsimplechat.presentation.screen.main

import com.garr.pavelbobrovko.notsimplechat.app.JSMApp
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseViewModel
import com.pddstudio.preferences.encrypted.EncryptedPreferences

class MainViewModel: BaseViewModel<MainRouter>() {

    private val ePreferences = EncryptedPreferences.Builder(JSMApp.instance)
            .withEncryptionPassword(PASS).build()

    private var localId: String

    companion object {
        private val LOCAL_ID = "localId"
        private val PASS = "qwerty"
    }

    init {
        localId = getLocalId()

        if (localId == LOCAL_ID){
            router?.startLoginActivity()
        }
    }

    private fun getLocalId(): String {
        return ePreferences.getString(LOCAL_ID, LOCAL_ID)
    }
}