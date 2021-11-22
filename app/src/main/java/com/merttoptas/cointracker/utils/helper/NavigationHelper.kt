package com.merttoptas.cointracker.utils.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.merttoptas.cointracker.MainActivity
import com.merttoptas.cointracker.features.loginandregister.LoginAndRegisterActivity

object NavigationHelper {

    fun startMainActivity(activity: Activity,context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        activity.finish()
    }

    fun startLoginAndRegisterActivity(context: Context) {
        val intent = Intent(context, LoginAndRegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}