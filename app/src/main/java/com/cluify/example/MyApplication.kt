package com.cluify.example

import androidx.multidex.MultiDexApplication
import com.cluify.sdk.logger.CluifyLogListener
import com.cluify.sdk.manager.CluifyManager
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        CluifyManager.init(this, object : CluifyLogListener {
            override fun onError(tag: String?, message: String, t: Throwable?) {
                Crashlytics.logException(t)
            }

            override fun onWarn(tag: String?, message: String, t: Throwable?) {
                Crashlytics.log(message)
            }

            override fun onWtf(tag: String?, message: String, t: Throwable?) {
                Crashlytics.log(message)
            }

            override fun onInfo(tag: String?, message: String) {
                Crashlytics.log(message)
            }
        })
    }
}