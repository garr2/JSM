package com.garr.pavelbobrovko.notsimplechat.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary



class JSMApp: Application() {

    companion object {
        lateinit var instance: JSMApp
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this)
    }
}