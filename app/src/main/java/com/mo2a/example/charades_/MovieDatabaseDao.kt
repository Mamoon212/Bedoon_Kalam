package com.mo2a.example.charades_

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovieDatabaseDao {

    @Insert
    fun insert(movie: Movie)

    @Query("SELECT * from movie_table WHERE movieId = :key")
    fun get(key: Int): LiveData<Movie>

    @Query("SELECT * from movie_table WHERE movieId = :key")
    fun getMovie(key: Int): Movie

    @Query("SELECT * FROM movie_table ORDER BY movieId DESC")
    fun getAllMovies(): List<Movie>
}