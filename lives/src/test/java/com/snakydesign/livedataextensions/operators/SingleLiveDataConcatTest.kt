package com.snakydesign.livedataextensions.operators

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.snakydesign.livedataextensions.concat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Created by Adib Faramarzi
 */
@Suppress("UNCHECKED_CAST")
class SingleLiveDataConcatTest<T> : MediatorLiveData<T>(){
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test LiveData concat multiple LiveData with wrong initial data`(){
        val observer= mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<Int>()
        sourceLiveData2.value = 3
        val testingLiveData = concat(sourceLiveData1,sourceLiveData2,sourceLiveData3)
        testingLiveData.observeForever(observer)
        Assert.assertEquals(null, testingLiveData.value)
        verifyZeroInteractions(observer)
    }

    @Test
    fun `test LiveData concat multiple LiveData with proper initial data`(){
        val observer= mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<Int>()
        sourceLiveData1.value = 2
        val testingLiveData = concat(sourceLiveData1,sourceLiveData2,sourceLiveData3)
        testingLiveData.observeForever(observer)
        Assert.assertEquals(2, testingLiveData.value)
        verify(observer).onChanged(2)
        verifyZeroInteractions(observer)
    }

    @Test
    fun `test LiveData concat multiple LiveData with initial and on going data`(){
        val observer= mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<Int>()
        sourceLiveData2.value = 2
        val testingLiveData = concat(sourceLiveData1,sourceLiveData2,sourceLiveData3)
        testingLiveData.observeForever(observer)
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData1.value = 1

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(1)
        inOrder.verify(observer).onChanged(2)
        sourceLiveData3.value = 3
        inOrder.verify(observer).onChanged(3)
        assertEquals(3, testingLiveData.value)
        inOrder.verifyNoMoreInteractions()
    }

}