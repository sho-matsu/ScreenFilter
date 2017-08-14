package jp.shoma.screenfilter

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
import android.view.View


class ScreenFilterService : Service() {
    private lateinit var view : View;
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val transparency = PrefUtil.getSpValInt(this, PrefConst.KEY_TRANSPARENCY)
        view = FrameLayout(this)
        view.setBackgroundColor(Color.BLACK)
        view.background.alpha = 255 * transparency / 100
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
    }

    companion object {
        val TAG = ScreenFilterService::class.java.name
    }
}
