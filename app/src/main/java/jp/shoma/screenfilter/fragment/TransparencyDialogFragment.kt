package jp.shoma.screenfilter.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import jp.shoma.screenfilter.constant.PrefConst
import jp.shoma.screenfilter.util.PrefUtil
import jp.shoma.screenfilter.R
import jp.shoma.screenfilter.event.ReStartScreenFilterEvent
import org.greenrobot.eventbus.EventBus


class TransparencyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_transparency_dialog, null)

        val value = view.findViewById<View>(R.id.value) as TextView
        val transparency = PrefUtil.getSpValInt(activity, PrefConst.KEY_TRANSPARENCY)
        value.text = activity.getString(R.string.transparency_percent, transparency.toString())

        val seekBar = view.findViewById<View>(R.id.seek_bar) as SeekBar
        seekBar.apply {
            progress = transparency
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar1: SeekBar, progress1: Int, fromUser: Boolean) {
                    value.text = activity.getString(R.string.transparency_percent, progress1.toString())
                }

                override fun onStartTrackingTouch(seekBar1: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar1: SeekBar) {
                }
            })
        }

        return AlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton("OK", { _, _ ->
                    PrefUtil.putSpValInt(activity, PrefConst.KEY_TRANSPARENCY, seekBar.progress)
                    val isStarted = PrefUtil.getSpValBoolean(activity, PrefConst.KEY_IS_FILTER_STARTED)
                    if (isStarted) {
                        EventBus.getDefault().post(ReStartScreenFilterEvent())
                    }
                })
                .setNegativeButton("CANCEL", null)
                .create()
    }

    companion object {
        private val TAG = TransparencyDialogFragment::class.java.name

        fun show(fragmentManager: FragmentManager) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                val dialog = TransparencyDialogFragment()
                dialog.show(fragmentManager, TAG)
            }
        }
    }
}
