package com.mo2a.example.charades_

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class Movie(@PrimaryKey
             val movieId: Int,
             @ColumnInfo(name = "title")
             val title: String)

fun createMovies(context: Context): ArrayList<Movie>{
    var i= 0
    val movies: ArrayList<Movie> = ArrayList()
        context.resources.openRawResource(R.raw.data).reader().forEachLine {
            val movie= Movie(i,it)
            i++
            movies.add(movie)
        }
    return movies
}