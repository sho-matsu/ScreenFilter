package jp.shoma.screenfilter.service

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
import android.os.Binder
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import jp.shoma.screenfilter.R
import jp.shoma.screenfilter.activity.MainActivity
import jp.shoma.screenfilter.constant.PrefConst
import jp.shoma.screenfilter.util.PrefUtil


class ScreenFilterService : Service() {
    private lateinit var mView: View

    private val mBinder = ScreenFilterBinder();

    inner class ScreenFilterBinder : Binder() {
        fun getService() : ScreenFilterService {
            return this@ScreenFilterService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        mIsBind = true
        setView()
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind")
        mIsBind = true
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        mIsBind = false
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notification = NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setContentTitle("Screen Filter")
                .setContentText("タップすると設定画面を起動できます")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        startForeground(STATUS_BAR_ICON_ID, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(mView)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(STATUS_BAR_ICON_ID)
    }

    fun setView() {
        Log.d(TAG, "setView")

        mView = FrameLayout(this)
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
        wm.addView(mView, params)

        setBackgroundColor()
    }

    fun setBackgroundColor() {
        Log.d(TAG, "setBackgroundColor")

//        val color = PrefUtil.getSpValStr(this, PrefConst.KEY_COLOR)
//        view.setBackgroundColor(Color.parseColor(color))
        mView.setBackgroundColor(Color.BLACK)

        val transparency = PrefUtil.getSpValInt(this, PrefConst.KEY_TRANSPARENCY)
        mView.background.alpha = (255 * transparency / 100 * 0.9).toInt()
    }

    companion object {
        val TAG = ScreenFilterService::class.java.name
        val STATUS_BAR_ICON_ID = 0x01
        private var mIsBind: Boolean = false

        fun isBind() = mIsBind
    }
}
