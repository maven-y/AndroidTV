package com.example.androidtv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtv.data.Movie
import com.example.androidtv.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository.create()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie

    init {
        loadMovies()
    }

    fun onMovieSelected(movie: Movie?) {
        _selectedMovie.value = movie
    }

    private fun loadMovies() {
        viewModelScope.launch {
            repository.getMovies().collect { movieList ->
                _movies.value = movieList
            }
        }
    }
}