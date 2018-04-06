package com.snakydesign.livedataextensions

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.*
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

/**
 * Created by Adib Faramarzi
 */
@Suppress("UNCHECKED_CAST")
class CombiningTest {
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
    fun `test LiveData merge multiple LiveData`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        sourceLiveData1.value = 2
        sourceLiveData2.value = 3
        val testingLiveData = merge(listOf(sourceLiveData1,sourceLiveData2))
        testingLiveData.observeForever(observer)
        Assert.assertEquals(3, testingLiveData.value)
        verify(observer).onChanged(2)
        verify(observer).onChanged(3)


        sourceLiveData1.value = 5
        Assert.assertEquals(5, testingLiveData.value)
        Mockito.verify(observer).onChanged(5)

        sourceLiveData2.value = 8
        Assert.assertEquals(8, testingLiveData.value)
        verify(observer).onChanged(8)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData merge with another LiveData`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        sourceLiveData1.value = 2
        sourceLiveData2.value = 3
        val testingLiveData = sourceLiveData1.mergeWith(sourceLiveData2)
        testingLiveData.observeForever(observer)
        Assert.assertEquals(3, testingLiveData.value)
        verify(observer).onChanged(2)
        verify(observer).onChanged(3)


        sourceLiveData1.value = 5
        Assert.assertEquals(5, testingLiveData.value)
        Mockito.verify(observer).onChanged(5)

        sourceLiveData2.value = 8
        Assert.assertEquals(8, testingLiveData.value)
        verify(observer).onChanged(8)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData startWith`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()

        val testingLiveData = sourceLiveData1.startWith(2)
        testingLiveData.observeForever(observer)
        Assert.assertEquals(2, testingLiveData.value)
        verify(observer).onChanged(2)

        sourceLiveData1.value = 3
        verify(observer).onChanged(3)
        Assert.assertEquals(3, testingLiveData.value)

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData zip with another LiveData`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Pair<Boolean,Int>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val expectedResult = Pair(true,3)
        val testingLiveData = zip(sourceLiveData1,sourceLiveData2)
        testingLiveData.observeForever(observer)


        sourceLiveData1.value = true
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData2.value = 3
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)

        verifyNoMoreInteractions(observer)
    }
    @Test
    fun `test LiveData zip with 2 other LiveData`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Triple<Boolean,Int,String>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<String>()
        val expectedResult = Triple(true,3,"hello")
        val testingLiveData = zip(sourceLiveData1,sourceLiveData2,sourceLiveData3)
        testingLiveData.observeForever(observer)


        sourceLiveData1.value = true
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData2.value = 3
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData3.value = "hello"
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)

        verifyNoMoreInteractions(observer)
    }
}