package com.snakydesign.livedataextensions

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */
class UtilsKtTest {


    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }


    @Test
    fun `test nonNull observeForever`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val nonNullObserver = Mockito.mock(NonNullObserver::class.java) as NonNullObserver<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.observeForever(observer)
        sourceLiveData.observeNonNullForever(nonNullObserver)
        sourceLiveData.value = null
        Mockito.verify(observer).onChanged(null)
        sourceLiveData.value = 2
        Mockito.verify(observer).onChanged(2)
        Mockito.verify(nonNullObserver, times(1)).onChanged(2)

        Mockito.verifyNoMoreInteractions(observer)
        Mockito.verifyNoMoreInteractions(nonNullObserver)
    }

}