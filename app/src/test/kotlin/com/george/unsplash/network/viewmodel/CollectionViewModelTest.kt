package com.george.unsplash.network.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.george.unsplash.network.repository.CollectionRepository
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class CollectionViewModelTest {

    @Mock
    private lateinit var viewModel: CollectionViewModel

    @Mock
    private lateinit var isLoadingLiveData: LiveData<Boolean>

    @Mock
    private lateinit var observer: Observer<in Boolean>


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = spy(CollectionViewModel(ApplicationProvider.getApplicationContext()))
    }

}