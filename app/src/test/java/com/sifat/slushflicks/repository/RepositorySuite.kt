package com.sifat.slushflicks.repository

import com.sifat.slushflicks.repository.genre.impl.GenreRepositoryImplTest
import com.sifat.slushflicks.repository.movie.impl.MovieHomeRepositoryImplTest
import com.sifat.slushflicks.repository.tv.impl.TvHomeRepositoryImplTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(value = [GenreRepositoryImplTest::class, TvHomeRepositoryImplTest::class, MovieHomeRepositoryImplTest::class])
class RepositorySuite