package jp.shoma.screenfilter

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    val OVERLAY_PERMISSION_REQ_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        val check = findViewById(R.id.check) as CheckBox
        check.isChecked = PrefUtil.getSpValBoolean(this, PrefConst.KEY_IS_FILTER_STARTED)
        check.text =
                if (check.isChecked) {
                    startScreenFilter(null)
                    "STOP"
                } else {
                    stopScreenFilter(null)
                    "START"
                }
        check.setOnCheckedChangeListener { compoundButton, _ ->
            val isChecked = compoundButton.isChecked
            PrefUtil.putSpValBoolean(this, PrefConst.KEY_IS_FILTER_STARTED, isChecked)
            if (isChecked) {
                startScreenFilter(null)
            } else {
                stopScreenFilter(null)
            }
        }

        findViewById(R.id.transparency).setOnClickListener {
            TransparencyDialogFragment.show(fragmentManager)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName))
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                // nothing to do !
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Subscribe
    fun startScreenFilter(startScreenFilterEvent: StartScreenFilterEvent?) {
        startService(Intent(this@MainActivity, ScreenFilterService::class.java))
    }

    @Subscribe
    fun stopScreenFilter(stopScreenFilterEvent: StopScreenFilterEvent?) {
        stopService(Intent(this@MainActivity, ScreenFilterService::class.java))
    }

    @Subscribe
    fun reStartScreenFilter(reStartScreenFilterEvent: ReStartScreenFilterEvent) {
        stopService(Intent(this@MainActivity, ScreenFilterService::class.java))
        startService(Intent(this@MainActivity, ScreenFilterService::class.java))
    }
}
