package jp.shoma.screenfilter.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import jp.shoma.screenfilter.R
import android.widget.TextView
import jp.shoma.screenfilter.model.ColorList
import java.util.HashMap


class ColorListAdapter(val context: Context,
                       val list: ArrayList<ColorList.Companion.ColorPattern> = ColorList.get(),
                       val layoutId : Int = R.layout.color_list_row) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView

        val holder : ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutId, null)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.color)
            holder.textView = view.findViewById(R.id.name)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val color = getItem(position)
        holder.imageView.setBackgroundColor(Color.parseColor(color.colorCode))
        holder.textView.text = color.colorName

        return view
    }

    override fun getItem(position: Int) : ColorList.Companion.ColorPattern = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = list.size

    internal class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var textView: TextView
    }
}