package com.rogerroth.placebook.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rogerroth.placebook.util.FileUtils
import com.rogerroth.placebook.util.ImageUtils

@Entity
data class Bookmark(
	@PrimaryKey(autoGenerate = true) var id: Long? = null,
	var placeId: String? = null,
	var name: String = "",
	var address: String = "",
	var latitude: Double = 0.0,
	var longitude: Double = 0.0,
	var phone: String = "",
	var notes: String = "",
	var category: String = ""
) {

	fun setImage(image: Bitmap, context: Context) {
		id?.let {
			ImageUtils.saveBitmapToFile(context, image,
				generateImageFilename(it))
		}
	}

	fun deleteImage(context: Context) {
		id?.let {
			FileUtils.deleteFile(context, generateImageFilename(it))
		}
	}

	companion object {
		fun generateImageFilename(id: Long): String {
			return "bookmark$id.png"
		}
	}
}