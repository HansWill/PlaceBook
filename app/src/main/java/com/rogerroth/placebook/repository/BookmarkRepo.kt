package com.rogerroth.placebook.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.rogerroth.placebook.db.BookmarkDao
import com.rogerroth.placebook.db.PlaceBookDatabase
import com.rogerroth.placebook.model.Bookmark

class BookmarkRepo(private val context: Context) {

	private var db: PlaceBookDatabase = PlaceBookDatabase.getInstance(context)
	private var bookmarkDao: BookmarkDao = db.bookmarkDao()

	fun updateBookmark(bookmark: Bookmark) {
		bookmarkDao.updateBookmark(bookmark)
	}

	fun getBookmark(bookmarkId: Long): Bookmark {
		return bookmarkDao.loadBookmark(bookmarkId)
	}

	fun addBookmark(bookmark: Bookmark): Long? {
		val newId = bookmarkDao.insertBookmark(bookmark)
		bookmark.id = newId
		return newId
	}

	fun createBookmark(): Bookmark {
		return Bookmark()
	}

	fun getLiveBookmark(bookmarkId: Long): LiveData<Bookmark> {
		val bookmark = bookmarkDao.loadLiveBookmark(bookmarkId)
		return bookmark
	}

	val allBookmarks: LiveData<List<Bookmark>>
		get() {
			return bookmarkDao.loadAll()
		}
}