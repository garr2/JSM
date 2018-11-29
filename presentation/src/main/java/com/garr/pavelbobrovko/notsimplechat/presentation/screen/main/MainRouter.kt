package com.garr.pavelbobrovko.notsimplechat.presentation.screen.main

import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseRouter

class MainRouter(activity: MainActivity): BaseRouter<MainActivity>(activity) {

    fun startLoginActivity(){
        activity.startLoginActivity()
    }

    fun startEmailVerifyActivity(){

    }

}