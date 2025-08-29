package com.example.quickdraw.notifications

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quickdraw.MainActivity
import com.example.quickdraw.R
import com.example.quickdraw.notifications.QDNotifManager.CONTRACTS_GROUP
import com.example.quickdraw.notifications.QDNotifManager.CONTRACTS_NOTIF_CHANNEL
import com.example.quickdraw.notifications.QDNotifManager.INTENT_CONTRACT_ID
import com.example.quickdraw.notifications.QDNotifManager.INTENT_CONTRACT_NAME

//TODO: Implement notification groups
//https://developer.android.com/reference/android/service/notification/NotificationListenerService.html#getActiveNotifications()
class QDNotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null && intent != null){
            val contractId = intent.getIntExtra(INTENT_CONTRACT_ID, 0)
            val contractName = intent.getStringExtra(INTENT_CONTRACT_NAME)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(context, CONTRACTS_NOTIF_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Contract finished!")
                .setContentText(contractName)
                .setGroup(CONTRACTS_GROUP)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            if(notificationManager.activeNotifications.size > 1){
               //build summary
                val summaryBuilder = NotificationCompat.Builder(context, CONTRACTS_NOTIF_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle("Contracts finished")
                    .setContentText("${notificationManager.activeNotifications.size} Contracts to redeem!")
                    .setGroup(CONTRACTS_GROUP)
                    .setGroupSummary(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                NotificationManagerCompat.from(context).notify(0, summaryBuilder.build())
            }

            NotificationManagerCompat.from(context).notify(contractId, builder.build())
        }
    }
}