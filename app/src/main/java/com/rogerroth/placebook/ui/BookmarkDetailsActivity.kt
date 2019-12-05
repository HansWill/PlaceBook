package com.rogerroth.placebook.ui

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rogerroth.placebook.R
import com.rogerroth.placebook.viewmodel.BookmarkDetailsViewModel
import kotlinx.android.synthetic.main.activity_bookmark_details.*

class BookmarkDetailsActivity : AppCompatActivity() {

	private lateinit var bookmarkDetailsViewModel: BookmarkDetailsViewModel
	private var bookmarkDetailsView: BookmarkDetailsViewModel.BookmarkDetailsView? = null

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_bookmark_details)
		setupToolbar()
		setupViewModel()
		getIntentData()
	}

	override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
		val inflater = menuInflater
		inflater.inflate(R.menu.menu_bookmark_details, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_save -> {
				saveChanges()
				return true
			}
			else -> return super.onOptionsItemSelected(item)
		}
	}

	private fun saveChanges() {
		val name = editTextName.text.toString()
		if (name.isEmpty()) {
			return
		}
		bookmarkDetailsView?.let { bookmarkView ->
			bookmarkView.name = editTextName.text.toString()
			bookmarkView.notes = editTextNotes.text.toString()
			bookmarkView.address = editTextAddress.text.toString()
			bookmarkView.phone = editTextPhone.text.toString()
			bookmarkDetailsViewModel.updateBookmark(bookmarkView)
		}
		finish()
	}

	private fun getIntentData() {

		val bookmarkId = intent.getLongExtra(
			MapsActivity.Companion.EXTRA_BOOKMARK_ID, 0)

		bookmarkDetailsViewModel.getBookmark(bookmarkId)?.observe(
			this, Observer<BookmarkDetailsViewModel.BookmarkDetailsView> {

				it?.let {
					bookmarkDetailsView = it
					// Populate fields from bookmark
					populateFields()
					populateImageView()
				}
			})
	}

	private fun setupViewModel() {
		bookmarkDetailsViewModel = ViewModelProviders.of(this).get(BookmarkDetailsViewModel::class.java)
	}

	private fun setupToolbar() {
		setSupportActionBar(toolbar)
	}

	private fun populateFields() {
		bookmarkDetailsView?.let { bookmarkView ->
			editTextName.setText(bookmarkView.name)
			editTextPhone.setText(bookmarkView.phone)
			editTextNotes.setText(bookmarkView.notes)
			editTextAddress.setText(bookmarkView.address)
		}
	}

	private fun populateImageView() {
		bookmarkDetailsView?.let { bookmarkView ->
			val placeImage = bookmarkView.getImage(this)
			placeImage?.let {
				imageViewPlace.setImageBitmap(placeImage)
			}
		}
	}

}