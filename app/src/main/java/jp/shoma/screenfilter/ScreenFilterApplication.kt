package jp.shoma.screenfilter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.RemoteException
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener

class ScreenFilterApplication : Application() {

  companion object {
    private const val REFERRER_TAG = "Referrer Tag"
  }

  override fun onCreate() {
    super.onCreate()

    // Notification Channelの作成
    initNotificationChannel()

    checkInstallReferrer()
  }

  private fun initNotificationChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(
      NotificationChannel(
        getString(R.string.notification_channel_id), "Screen Filter",
        NotificationManager.IMPORTANCE_LOW
      )
    )
  }

  private fun checkInstallReferrer() {
    val installReferrerClient = InstallReferrerClient.newBuilder(this).build()
    installReferrerClient.startConnection(object : InstallReferrerStateListener {
      override fun onInstallReferrerSetupFinished(responseCode: Int) {
        when (responseCode) {
          InstallReferrerClient.InstallReferrerResponse.OK -> {
            try {
              val referrerDetails = installReferrerClient.installReferrer
              val url = referrerDetails.installReferrer
              Log.e(REFERRER_TAG, url)
            } catch (e: RemoteException) {
              Log.e(REFERRER_TAG, e.localizedMessage)
            } finally {
              installReferrerClient.endConnection()
            }
          }
          InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
            Log.e(REFERRER_TAG, "InstallReferrerResponse.FEATURE_NOT_SUPPORTED")
          }
          InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
            Log.e(REFERRER_TAG, "InstallReferrerResponse.SERVICE_UNAVAILABLE")
          }
          InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {
            Log.e(REFERRER_TAG, "InstallReferrerResponse.SERVICE_DISCONNECTED")
          }
          InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {
            Log.e(REFERRER_TAG, "InstallReferrerResponse.DEVELOPER_ERROR")
          }
        }
      }

      override fun onInstallReferrerServiceDisconnected() {
        installReferrerClient.startConnection(this)
      }
    })
  }
}