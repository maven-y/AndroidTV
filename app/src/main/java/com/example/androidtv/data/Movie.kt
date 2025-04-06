package com.example.androidtv.data

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.webkit.URLUtil

/**
 * Movie data class representing a movie in our catalog
 *
 * 
 * id Unique identifier for the movie
 * title Movie title (nullable for legacy data)
 * description Movie description (nullable for legacy data)
 * backgroundImageUrl URL for background image (nullable for legacy data)
 * cardImageUrl URL for card image (nullable for legacy data)
 * videoUrl URL for video content (nullable for legacy data)
 * studio Movie studio (nullable for legacy data)
 * is4K Flag indicating if movie is 4K quality (optional)
 */
data class Movie(
    var id: Long,
    var title: String?,
    var description: String?,
    var backgroundImageUrl: String?,
    var cardImageUrl: String?,
    var videoUrl: String?,
    var studio: String?,
    var is4K: Boolean = false
) : Parcelable {
    
    companion object {
        private const val MAX_TITLE_LENGTH = 100
        private const val MAX_DESCRIPTION_LENGTH = 500
        
        /**
         * check data before processing
         */
        fun validate(movie: Movie): Boolean {
            if (movie.title?.length ?: 0 > MAX_TITLE_LENGTH) {
                Log.w("Movie", "Title too long: ${movie.title?.length}")
                return false
            }
            
            if (movie.description?.length ?: 0 > MAX_DESCRIPTION_LENGTH) {
                Log.w("Movie", "Description too long: ${movie.description?.length}")
                return false
            }
            
            // chek URLs if present
            if (movie.videoUrl?.isNotBlank() == true && !URLUtil.isValidUrl(movie.videoUrl)) {
              Log.w("Movie", "Invalid video URL: ${movie.videoUrl}")
                return false
            }
            
            if (movie.backgroundImageUrl?.isNotBlank() == true && !URLUtil.isValidUrl(movie.backgroundImageUrl)) {
                Log.w("Movie", "Invalid background image URL: ${movie.backgroundImageUrl}")
                return false
            }
            
            if (movie.cardImageUrl?.isNotBlank() == true && !URLUtil.isValidUrl(movie.cardImageUrl)) {
                Log.w("Movie", "Invalid card image URL: ${movie.cardImageUrl}")
                return false
            }


            
            return true
        }

        /**
         * Get a formatted title for display
         */
        fun getDisplayTitle(movie: Movie): String {
            return if (movie.is4K) {
                "${movie.title ?: "Unknown Title"} (4K)"
            } else {
                movie.title ?: "Unknown Title"
            }
        }// Get a formatted description for display
        fun getDisplayDescription(movie: Movie): String {
            return movie.description?.take(MAX_DESCRIPTION_LENGTH) ?: "No description available"
        }

      //Parcelable implementation
        @JvmField
        val CREATOR = object : Parcelable.Creator<Movie> {
            override fun createFromParcel(parcel: Parcel): Movie {
                try {
                    return Movie(parcel)
                } catch (e: Exception) {
                    Log.e("Movie", "Error creating from parcel: ${e.message}")
                    throw e
                }
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        try {
            parcel.writeLong(id)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeString(backgroundImageUrl)
            parcel.writeString(cardImageUrl)
            parcel.writeString(videoUrl)
            parcel.writeString(studio)
            parcel.writeByte(if (is4K) 1 else 0)
        } catch (e: Exception) {
            Log.e("Movie", "Error writing to parcel: ${e.message}")
            throw e
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}
