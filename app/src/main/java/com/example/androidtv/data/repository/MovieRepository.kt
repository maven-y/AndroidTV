package com.example.androidtv.data.repository

import android.util.Log
import com.example.androidtv.data.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Holds base URL and API endpoint configuration
object ApiConfig {
    const val BASE_URL = "https://api.npoint.io/"
    const val MOVIE_ENDPOINT = "1eae1d122f7429a023f8"
}

// Retrofit service interface for fetching movie data
interface MovieApiService {
    @GET(ApiConfig.MOVIE_ENDPOINT)
    suspend fun fetchMovies(): MovieResponse
}

// Expected API response structure
data class MovieResponse(
    val record: List<Movie>
)

// Repository to handle all movie-related data operations
class MovieRepository(
    private val api: MovieApiService,
    private val logError: (Throwable) -> Unit = { Log.e("MovieRepository", "Error fetching movies", it) }
) {

    /**
     * Retrieves a list of movies from the API.
     * If an error occurs, logs the error and returns an empty list to prevent crashes.
     */
    fun getMovies(): Flow<List<Movie>> {
        return flow {
            val response = api.fetchMovies()
            emit(response.record)
        }.catch { e ->
            Log.e("MovieRepository", "Error: ", e)
            emit(emptyList())
        }
    }

    companion object {
        /**
         * Factory method to create an instance of [MovieRepository] with default Retrofit setup.
         */
        fun create(): MovieRepository {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService = retrofit.create(MovieApiService::class.java)
            return MovieRepository(apiService)
        }
    }
}