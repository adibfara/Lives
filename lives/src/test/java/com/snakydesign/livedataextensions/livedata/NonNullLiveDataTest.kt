package com.snakydesign.livedataextensions.livedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.snakydesign.livedataextensions.nonNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Adib Faramarzi (adibfara@gmail.com) - 06/04/2018
 */
@Suppress("UNCHECKED_CAST")
class NonNullLiveDataTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()
    @Test
    fun `test LiveData nonNull operator should not emit null`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData1.nonNull()
        testingLiveData.observeForever(observer)
        sourceLiveData1.value = null
        sourceLiveData1.value = null
        sourceLiveData1.value = null
        Mockito.verifyZeroInteractions(observer)
    }

    @Test
    fun `test LiveData nonNull operator should emit non nulls`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData1.nonNull()
        testingLiveData.observeForever(observer)
        sourceLiveData1.value = null
        sourceLiveData1.value = 2
        Mockito.verify(observer).onChanged(2)
        sourceLiveData1.value = null
        Mockito.verifyNoMoreInteractions(observer)
    }
}