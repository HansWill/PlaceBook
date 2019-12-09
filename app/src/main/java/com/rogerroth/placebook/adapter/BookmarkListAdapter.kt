package com.rogerroth.placebook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rogerroth.placebook.R
import com.rogerroth.placebook.ui.MapsActivity
import com.rogerroth.placebook.viewmodel.MapsViewModel.BookmarkView

class BookmarkListAdapter(
	private var bookmarkData: List<BookmarkView>?, private val mapsActivity: MapsActivity) : RecyclerView.Adapter<BookmarkListAdapter.ViewHolder>() {

	class ViewHolder(v: View, private val mapsActivity: MapsActivity) : RecyclerView.ViewHolder(v) {
		val nameTextView: TextView = v.findViewById(R.id.bookmarkNameTextView) as TextView
		val categoryImageView: ImageView = v.findViewById(R.id.bookmarkIcon) as ImageView

		init {
			v.setOnClickListener {
				val bookmarkView = itemView.tag as BookmarkView
				mapsActivity.moveToBookmark(bookmarkView)
			}
		}
	}

	fun setBookmarkData(bookmarks: List<BookmarkView>) {
		this.bookmarkData = bookmarks
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkListAdapter.ViewHolder {
		val vh = BookmarkListAdapter.ViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.bookmark_item, parent, false),
			mapsActivity
		)
		return vh
	}

	override fun onBindViewHolder(holder: BookmarkListAdapter.ViewHolder, position: Int) {

		val bookmarkData = bookmarkData ?: return
		val bookmarkViewData = bookmarkData[position]

		holder.itemView.tag = bookmarkViewData
		holder.nameTextView.text = bookmarkViewData.name
		bookmarkViewData.categoryResourceId?.let {
			holder.categoryImageView.setImageResource(it)
		}
	}

	override fun getItemCount(): Int {
		return bookmarkData?.size ?: 0
	}
}