package jp.shoma.screenfilter.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import jp.shoma.screenfilter.R
import jp.shoma.screenfilter.constant.PrefConst
import jp.shoma.screenfilter.event.StartScreenFilterEvent
import jp.shoma.screenfilter.event.StopScreenFilterEvent
import jp.shoma.screenfilter.service.ScreenFilterService
import jp.shoma.screenfilter.util.PrefUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private val OVERLAY_PERMISSION_REQ_CODE = 1000
    private lateinit var mContext : Context
    private var mService : ScreenFilterService? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mService = (binder as? ScreenFilterService.ScreenFilterBinder)?.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

        // UIオーバーレイのパーミッションチェック
        checkOverlayPermission()

        // START/STOPチェックボックスの設定
        val check = findViewById(R.id.check) as CheckBox
        check.apply {
            isChecked = PrefUtil.getSpValBoolean(mContext, PrefConst.KEY_IS_FILTER_STARTED)
            text = if (isChecked) "STOP" else "START"

            setOnCheckedChangeListener { compoundButton, _ ->
                val isChecked = compoundButton.isChecked
                PrefUtil.putSpValBoolean(mContext, PrefConst.KEY_IS_FILTER_STARTED, isChecked)
                if (isChecked) {
                    startScreenFilter(null)
                } else {
                    stopScreenFilter(null)
                }
            }
        }

        // 透過率シークバーの設定
        val value = findViewById(R.id.value) as TextView
        val transparency = PrefUtil.getSpValInt(mContext, PrefConst.KEY_TRANSPARENCY)
        value.text = getString(R.string.transparency_percent, transparency.toString())
        val seekBar = findViewById(R.id.seek_bar) as SeekBar
        seekBar.apply {
            progress = transparency
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar1: SeekBar, progress1: Int, fromUser: Boolean) {
                    // 値が変わる都度、画面に反映
                    value.text = getString(R.string.transparency_percent, progress1.toString())
                }

                override fun onStartTrackingTouch(seekBar1: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar1: SeekBar) {
                    // 変更が終わったら保存
                    PrefUtil.putSpValInt(mContext, PrefConst.KEY_TRANSPARENCY, seekBar.progress)
                    mService?.setBackgroundColor()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        val isChecked = PrefUtil.getSpValBoolean(this, PrefConst.KEY_IS_FILTER_STARTED)
        // フィルター起動中だが、ServiceがBindされていなければBindする
        if (isChecked && !ScreenFilterService.isBind()) {
            val intent = Intent(mContext, ScreenFilterService::class.java)
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

        // ServiceがBindされていればUnbindする
        if (ScreenFilterService.isBind()) {
            unbindService(mServiceConnection)
        }
    }

    /**
     * UIオーバーレイのパーミッションを取得する
     */
    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName))
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(mContext)) {
                // UIオーバーレイのパーミッションを取得できなかった場合、アプリを終了する
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Subscribe
    fun startScreenFilter(startScreenFilterEvent: StartScreenFilterEvent?) {
        val intent = Intent(mContext, ScreenFilterService::class.java)
        startService(intent)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    @Subscribe
    fun stopScreenFilter(stopScreenFilterEvent: StopScreenFilterEvent?) {
        stopService(Intent(mContext, ScreenFilterService::class.java))
        if (ScreenFilterService.isBind()) {
            unbindService(mServiceConnection)
        }
    }
}