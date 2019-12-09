package com.rogerroth.placebook.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rogerroth.placebook.R
import com.rogerroth.placebook.util.ImageUtils
import com.rogerroth.placebook.viewmodel.BookmarkDetailsViewModel
import kotlinx.android.synthetic.main.activity_bookmark_details.*
import java.io.File

class BookmarkDetailsActivity : AppCompatActivity(), PhotoOptionDialogFragment.PhotoOptionDialogListener {

	private lateinit var bookmarkDetailsViewModel: BookmarkDetailsViewModel
	private var bookmarkDetailsView: BookmarkDetailsViewModel.BookmarkDetailsView? = null
	private var photoFile: File? = null

	override fun onCaptureClick() {
		photoFile = null
		try {
			photoFile = ImageUtils.createUniqueImageFile(this)
		} catch (ex: java.io.IOException) {
			return
		}
		photoFile?.let { photoFile ->
			val photoUri = FileProvider.getUriForFile(this, "com.rogerroth.placebook.fileprovider", photoFile)
			val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri)
			val intentActivities = packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
			intentActivities.map { it.activityInfo.packageName }
				.forEach { grantUriPermission(it, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }
			startActivityForResult(captureIntent, REQUEST_CAPTURE_IMAGE)
		}
	}

	override fun onPickClick() {
		val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
		startActivityForResult(pickIntent, REQUEST_GALLERY_IMAGE)
	}

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

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode == android.app.Activity.RESULT_OK) {

			when (requestCode) {

				REQUEST_CAPTURE_IMAGE -> {

					val photoFile = photoFile ?: return

					val uri = FileProvider.getUriForFile(this, "com.rogerroth.placebook.fileprovider", photoFile)
					revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

					val image = getImageWithPath(photoFile.absolutePath)
					image?.let { updateImage(it) }
				}

				REQUEST_GALLERY_IMAGE -> if (data != null && data.data != null) {
					val imageUri = data.data
					val image = getImageWithAuthority(imageUri)
					image?.let { updateImage(it) }
				}
			}
		}
	}

	private fun getImageWithAuthority(uri: Uri?): Bitmap? {
		return ImageUtils.decodeUriStreamToSize(uri, resources.getDimensionPixelSize(R.dimen.default_image_width),
			resources.getDimensionPixelSize(R.dimen.default_image_height), this)
	}

	private fun updateImage(image: Bitmap) {
		val bookmarkView = bookmarkDetailsView ?: return
		imageViewPlace.setImageBitmap(image)
		bookmarkView.setImage(this, image)
	}

	private fun getImageWithPath(filePath: String): Bitmap? {
		return ImageUtils.decodeFileToSize(filePath, resources.getDimensionPixelSize(
				R.dimen.default_image_width), resources.getDimensionPixelSize(R.dimen.default_image_height))
	}


	private fun replaceImage() {
		val newFragment = PhotoOptionDialogFragment.newInstance(this)
		newFragment?.show(supportFragmentManager, "photoOptionDialog")
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
		imageViewPlace.setOnClickListener {
			replaceImage()
		}
	}

	companion object {
		private const val REQUEST_CAPTURE_IMAGE = 1
		private const val REQUEST_GALLERY_IMAGE = 2
	}

}