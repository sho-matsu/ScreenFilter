package jp.shoma.screenfilter

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.view.WindowManager
import android.widget.FrameLayout
import android.graphics.PixelFormat
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.view.View


class ScreenFilterService : Service() {
    private lateinit var view : View

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notification = NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setContentTitle("Screen Filter")
                .setContentText("タップすると設定画面を起動できます")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        notification.flags = NotificationCompat.FLAG_ONGOING_EVENT

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(STATUS_BAR_ICON_ID, notification)

        val transparency = PrefUtil.getSpValInt(this, PrefConst.KEY_TRANSPARENCY)
        view = FrameLayout(this)
        view.setBackgroundColor(Color.BLACK)
        view.background.alpha = (255 * transparency / 100 * 0.8).toInt()
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT)
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(view, params)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(view)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(STATUS_BAR_ICON_ID)
    }

    companion object {
        val TAG = ScreenFilterService::class.java.name
        val STATUS_BAR_ICON_ID = 0x01
    }
}
