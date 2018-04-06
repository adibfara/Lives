package com.snakydesign.livedataextensions.livedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.snakydesign.livedataextensions.first
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mockito

/**
 * Created by Adib Faramarzi
 */
class SingleLiveDataTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test SingleLiveDataTest without initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = SingleLiveData(sourceLiveData)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        kotlin.test.assertEquals(2, testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        kotlin.test.assertEquals(2, testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test SingleLiveDataTest with initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 2
        val testingLiveData = SingleLiveData(sourceLiveData)
        testingLiveData.observeForever(observer)

        kotlin.test.assertEquals(2, testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        kotlin.test.assertEquals(2, testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }
}