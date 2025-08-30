package com.example.quickdraw.notifications

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quickdraw.TAG
import com.example.quickdraw.network.data.ActiveContract
import network.chaintech.cmpimagepickncrop.permissionsmanager.PermissionManager

object QDNotifManager{
    const val CONTRACTS_NOTIF_CHANNEL = "contracts"
    const val CONTRACTS_GROUP = "contractsGroup"
    const val INTENT_CONTRACT_FINISHED = "com.example.quickdraw.CONTRACT_FINISHED"
    const val INTENT_CONTRACT_NAME = "CONTRACT_NAME"
    const val INTENT_CONTRACT_ID = "CONTRACT_ID"

    fun init(context:Context){
        val myChannel = NotificationChannel(CONTRACTS_NOTIF_CHANNEL, "Contracts", NotificationManager.IMPORTANCE_DEFAULT)
        val notifService = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifService.createNotificationChannel(myChannel)
    }

    fun scheduleContractNotification(context:Activity, contract: ActiveContract){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context, listOf(Manifest.permission.POST_NOTIFICATIONS).toTypedArray(), 0)
        }

        Log.i(TAG, "Contract Scheduled: $contract")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(INTENT_CONTRACT_FINISHED)
        intent.putExtra(INTENT_CONTRACT_ID, contract.activeId)
        intent.putExtra(INTENT_CONTRACT_NAME, contract.name)
        intent.setPackage("com.example.quickdraw")
        val pendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + contract.requiredTime * 1000, pendingIntent)
    }
}