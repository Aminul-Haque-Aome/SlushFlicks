package com.sifat.slushflicks.repository.movie.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagedList
import com.google.gson.Gson
import com.sifat.slushflicks.api.ApiTag
import com.sifat.slushflicks.api.StatusCode
import com.sifat.slushflicks.api.StatusCode.Companion.INTERNAL_ERROR
import com.sifat.slushflicks.api.home.movie.MovieServiceFake
import com.sifat.slushflicks.api.home.movie.model.MovieListApiModel
import com.sifat.slushflicks.data.DataManager
import com.sifat.slushflicks.helper.JobManager
import com.sifat.slushflicks.model.ShowModelMinimal
import com.sifat.slushflicks.rule.MainCoroutineRule
import com.sifat.slushflicks.ui.state.DataState.Error
import com.sifat.slushflicks.ui.state.DataState.Success
import com.sifat.slushflicks.util.getOrAwaitValue
import com.sifat.slushflicks.utils.any
import com.sifat.slushflicks.utils.api.NetworkStateManager
import com.sifat.slushflicks.utils.api.moviePage1
import com.sifat.slushflicks.utils.api.moviePage2
import com.sifat.slushflicks.utils.api.moviePageInvalid
import com.sifat.slushflicks.utils.getFakeDataSource
import com.sifat.slushflicks.utils.getShowList
import com.sifat.slushflicks.utils.single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UpcomingMovieRepositoryImplTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineDispatcher = MainCoroutineRule()

    private lateinit var sut: UpcomingMovieRepositoryImpl

    private lateinit var manager: DataManager
    private lateinit var service: MovieServiceFake
    private lateinit var apiKey: String
    private lateinit var networkState: NetworkStateManager
    private lateinit var jobManager: JobManager
    private val boundaryCallback: PagedList.BoundaryCallback<ShowModelMinimal> =
        object : PagedList.BoundaryCallback<ShowModelMinimal>() {}

    @Before
    fun setup() {
        manager = mock(DataManager::class.java)
        service = MovieServiceFake(Gson())
        jobManager = mock(JobManager::class.java)
        networkState = mock(NetworkStateManager::class.java)
        apiKey = "apiKey"
        sut = UpcomingMovieRepositoryImpl(
            dataManager = manager,
            movieService = service,
            apiKey = apiKey,
            jobManager = jobManager,
            networkStateManager = networkState,
            dispatcher = Dispatchers.Main
        )
    }

    @Test
    fun testCollectionFirstPage() {
        // Arrange
        val list = Gson().fromJson(moviePage1, MovieListApiModel::class.java)
        val expected = list.results.size
        `when`(networkState.isOnline()).thenReturn(true)
        //Act
        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                val actual = sut.getMovieList(1).getOrAwaitValue() as Success
                //Assert
                assertEquals(expected, actual.dataResponse.data)
                verify(manager, single()).softInsertMovie(any())
                verify(manager, single()).insertNewMovieCollection(any(), any())
                verify(manager, single()).getGenres()
                verify(networkState, single()).isOnline()
                verify(jobManager, single()).addJob(any(), any())
                verifyNoMoreInteractions(networkState)
                verifyNoMoreInteractions(manager)
                verifyNoMoreInteractions(jobManager)
            }
        }
    }

    @Test
    fun testCollectionSecondPage() {
        // Arrange
        val list = Gson().fromJson(moviePage2, MovieListApiModel::class.java)
        val expected = list.results.size
        `when`(networkState.isOnline()).thenReturn(true)
        //Act
        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                val actual = sut.getMovieList(2).getOrAwaitValue() as Success
                //Assert
                assertEquals(expected, actual.dataResponse.data)
                verify(manager, single()).softInsertMovie(any())
                verify(manager, single()).addMovieCollection(any())
                verify(manager, single()).getGenres()
                verify(networkState, single()).isOnline()
                verify(jobManager, single()).addJob(any(), any())
                verifyNoMoreInteractions(networkState)
                verifyNoMoreInteractions(manager)
                verifyNoMoreInteractions(jobManager)
            }
        }
    }

    @Test
    fun testCollectionInvalidPage() {
        // Arrange
        val list = Gson().fromJson(moviePageInvalid, MovieListApiModel::class.java)
        val expected = list.results.size
        `when`(networkState.isOnline()).thenReturn(true)
        //Act
        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                val actual = sut.getMovieList(3).getOrAwaitValue() as Success
                //Assert
                assertEquals(expected, actual.dataResponse.data)
                verify(networkState, single()).isOnline()
                verify(jobManager, single()).addJob(any(), any())
                verifyNoMoreInteractions(networkState)
                verifyZeroInteractions(manager)
                verifyNoMoreInteractions(jobManager)
            }
        }
    }

    @Test
    fun testMovieCastNoInternet() {
        // Arrange
        val tag = ApiTag.UPCOMING_MOVIE_API_TAG
        `when`(networkState.isOnline()).thenReturn(false)

        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                //Act
                val actual = sut.getMovieList(1).getOrAwaitValue() as Error
                //Assert
                actual.dataResponse.run {
                    assertEquals(statusCode, INTERNAL_ERROR)
                    assertEquals(tag, apiTag)
                    assertNull(errorMessage)
                }
                verify(networkState, single()).isOnline()
                verifyZeroInteractions(manager)
                verifyNoMoreInteractions(networkState)
            }
        }
    }

    @Test
    fun testMovieCastUnauth() {
        // Arrange
        val tag = ApiTag.UPCOMING_MOVIE_API_TAG
        `when`(networkState.isOnline()).thenReturn(true)
        service.errorCode = StatusCode.UNAUTHORIZED
        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                //Act
                val actual = sut.getMovieList(1).getOrAwaitValue() as Error
                //Assert
                actual.dataResponse.run {
                    assertEquals(statusCode, StatusCode.UNAUTHORIZED)
                    assertEquals(tag, apiTag)
                    assertNotNull(errorMessage)
                }
                verify(networkState, single()).isOnline()
                verifyZeroInteractions(manager)
                verifyNoMoreInteractions(networkState)
            }
        }
    }

    @Test
    fun testMovieCastResourceNotFound() {
        // Arrange
        val tag = ApiTag.UPCOMING_MOVIE_API_TAG
        `when`(networkState.isOnline()).thenReturn(true)
        service.errorCode = StatusCode.RESOURCE_NOT_FOUND
        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                //Act
                val actual = sut.getMovieList(1).getOrAwaitValue() as Error
                //Assert
                actual.dataResponse.run {
                    assertEquals(statusCode, StatusCode.RESOURCE_NOT_FOUND)
                    assertEquals(tag, apiTag)
                    assertNotNull(errorMessage)
                }
                verify(networkState, single()).isOnline()
                verifyZeroInteractions(manager)
                verifyNoMoreInteractions(networkState)
            }
        }
    }

    @Test
    fun testMovieList() {
        // Arrange
        val expected = getShowList()
        val dataSource = getFakeDataSource<Int, ShowModelMinimal>(expected)
        `when`(manager.getPagingMovies(anyString())).thenReturn(dataSource)

        Assertions.assertDoesNotThrow {
            mainCoroutineDispatcher.runBlockingTest {
                //Act
                val actual =
                    sut.getPagingMovieList(boundaryCallback).getOrAwaitValue() as Success

                //Assert
                assertEquals(expected[2], actual.dataResponse.data?.get(2))
                verify(manager, single()).getPagingMovies(anyString())
                verifyNoMoreInteractions(manager)
                verifyZeroInteractions(networkState)
                verifyZeroInteractions(jobManager)
            }
        }
    }
}