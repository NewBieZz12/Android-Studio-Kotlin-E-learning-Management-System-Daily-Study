package com.example.swe401swe2304437

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

data class ChapterItem(val title: String, val subtitle: String, var isDownloaded: Boolean)

class ChapterAdapter(
    private val context: Context,
    private val dataSource: List<ChapterItem>,
    private val onActionClick: (ChapterItem) -> Unit
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_chapter, parent, false)
        val currentItem = getItem(position) as ChapterItem

        val rootLayout = rowView.findViewById<View>(R.id.rowRootLayout)
        val txtTitle = rowView.findViewById<TextView>(R.id.txtChapterTitle)
        val txtSubtitle = rowView.findViewById<TextView>(R.id.txtChapterSubtitle)
        val btnAction = rowView.findViewById<ImageButton>(R.id.btnRowAction)

        txtTitle.text = currentItem.title
        txtSubtitle.text = currentItem.subtitle

        if (currentItem.isDownloaded) {
            rootLayout.setBackgroundResource(R.drawable.rounded_blue_box)
            txtTitle.setTextColor(Color.WHITE)
            txtSubtitle.setTextColor(Color.parseColor("#B0C4DE"))

            btnAction.setImageResource(R.drawable.ic_delete)
            btnAction.setColorFilter(Color.WHITE)
        } else {
            rootLayout.setBackgroundResource(R.drawable.rounded_grey_box)
            txtTitle.setTextColor(Color.DKGRAY)
            txtSubtitle.setTextColor(Color.GRAY)

            btnAction.setImageResource(R.drawable.ic_download)
            btnAction.setColorFilter(Color.parseColor("#001F3F"))
        }

        btnAction.setOnClickListener {
            onActionClick(currentItem)
        }

        return rowView
    }
}