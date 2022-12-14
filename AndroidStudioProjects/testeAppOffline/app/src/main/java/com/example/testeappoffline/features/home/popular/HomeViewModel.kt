package com.example.testeappoffline.features.home.popular

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.testeappoffline.base.BaseViewModel
import com.example.testeappoffline.data.db.local.entity.PopularMovie
import com.example.testeappoffline.data.db.local.repository.PopularRepository
import com.example.testeappoffline.data.remote.model.Movie
import com.example.testeappoffline.data.remote.repository.MovieRepository
import com.example.testeappoffline.features.home.popular.paging.MovieDataSource
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context,
    private val movieRepository: MovieRepository,
    private val popularRepository: PopularRepository
) : BaseViewModel() {

    private val _listMoviesOff = MutableLiveData<List<PopularMovie>>()
    val listMoviesOff: LiveData<List<PopularMovie>> = _listMoviesOff

    var pagedListMovie: LiveData<PagedList<Movie>>

    private var netIsOn = true

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()
        pagedListMovie = initializedPagedListBuilder(config).build()
        netIsOn = netIsOn(context)

        if (!netIsOn) {
            initRoom()
        }
    }

    private fun initRoom() {
        viewModelScope.launch {
            _listMoviesOff.postValue(popularRepository.getPopularMovies())
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config)
            : LivePagedListBuilder<Int, Movie> {

        val dataSourceFactory = object : DataSource.Factory<Int, Movie>() {
            override fun create(): DataSource<Int, Movie> {
                return MovieDataSource(movieRepository, popularRepository, netIsOn)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }


}