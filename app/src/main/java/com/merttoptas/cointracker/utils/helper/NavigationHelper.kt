package com.merttoptas.cointracker.utils.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.merttoptas.cointracker.features.mainactivity.MainActivity

object NavigationHelper {

    fun startMainActivity(activity: Activity, context: Context, isDirectToLogin: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(MainActivity.DIRECT_TO_LOGIN, isDirectToLogin)
        context.startActivity(intent)
        activity.finish()
    }
}