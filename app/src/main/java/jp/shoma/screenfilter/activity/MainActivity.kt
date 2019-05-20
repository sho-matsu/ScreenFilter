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
import android.view.View
import android.widget.*
import jp.shoma.screenfilter.R
import jp.shoma.screenfilter.adapter.ColorListAdapter
import jp.shoma.screenfilter.constant.PrefConst
import jp.shoma.screenfilter.event.StartScreenFilterEvent
import jp.shoma.screenfilter.event.StopScreenFilterEvent
import jp.shoma.screenfilter.service.ScreenFilterService
import jp.shoma.screenfilter.util.PrefUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    companion object {
        private const val OVERLAY_PERMISSION_REQ_CODE = 1000
    }
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
        val check = findViewById<CheckBox>(R.id.check)
        check.apply {
            isChecked = PrefUtil.getSpValBoolean(mContext, PrefConst.KEY_IS_FILTER_STARTED)
            val mode = if (isChecked) getString(R.string.stop) else getString(R.string.active)
            text =  getString(R.string.screen_filter_switch, mode)

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
        val value = findViewById<TextView>(R.id.value)
        val transparency = PrefUtil.getSpValInt(mContext, PrefConst.KEY_TRANSPARENCY)
        value.text = getString(R.string.transparency_percent, transparency.toString())
        val seekBar = findViewById<SeekBar>(R.id.seek_bar)
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

        val colorSelector = findViewById<Spinner>(R.id.color_spinner)
        colorSelector.apply {
            adapter = ColorListAdapter(mContext)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    PrefUtil.putSpValInt(mContext, PrefConst.KEY_SELECTED_COLOR, position)
                    mService?.setBackgroundColor()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            setSelection(PrefUtil.getSpValInt(mContext, PrefConst.KEY_SELECTED_COLOR))
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        val isChecked = PrefUtil.getSpValBoolean(this, PrefConst.KEY_IS_FILTER_STARTED)
        if (isChecked) {
            // ServiceがBindされていなければBindする
            if (!ScreenFilterService.isBound()) {
                val intent = Intent(mContext, ScreenFilterService::class.java)
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
            }
            // Serviceが起動されていなければ起動する
            if (!ScreenFilterService.isStarted()) {
                val intent = Intent(mContext, ScreenFilterService::class.java)
                startService(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

        // ServiceがBindされていればUnbindする
        if (ScreenFilterService.isBound()) {
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
                        Uri.parse("package:$packageName"))
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
        if (ScreenFilterService.isBound()) {
            unbindService(mServiceConnection)
        }
    }
}
