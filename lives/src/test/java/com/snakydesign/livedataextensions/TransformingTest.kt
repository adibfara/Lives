package com.snakydesign.livedataextensions

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.test.assertNull

/**
 * Created by Adib Faramarzi (adibfara@gmail.com)
 */
@Suppress("UNCHECKED_CAST")
class TransformingTest {


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
    fun `test LiveData filter initial item`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val testingLiveData = liveDataOf(2).filter {
            it!=null && it > 1
        }
        testingLiveData.observeForever(observer)

        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData filter multiple items`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val originalLiveData = liveDataOf(2)
        val testingLiveData = originalLiveData.filter {
            it!=null && it > 10
        }
        testingLiveData.observeForever(observer)

        originalLiveData.value = 11
        assertEquals(11,testingLiveData.value)

        originalLiveData.value = 100
        assertEquals(100,testingLiveData.value)

        originalLiveData.value = 5
        assertEquals(100,testingLiveData.value)
    }

    @Test
    fun `test LiveData map`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val testingLiveData = liveDataOf(2).map { 3 }
        testingLiveData.observeForever(observer)

        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData switchMap`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val testingLiveData = liveDataOf(2).switchMap { liveDataOf(4) }
        testingLiveData.observeForever(observer)

        assertEquals(4,testingLiveData.value)
        Mockito.verify(observer).onChanged(4)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData doBeforeNext`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val mockedBeforeNext = mock(TestOnNextAction::class.java) as OnNextAction<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.doBeforeNext(mockedBeforeNext)
        val expectedValue = 3
        testingLiveData.observeForever(observer)

        sourceLiveData.value = expectedValue
        val inOrder = inOrder(mockedBeforeNext,observer)
        inOrder.verify(mockedBeforeNext, times(1))(expectedValue)
        assertEquals(expectedValue,testingLiveData.value)
        inOrder.verify(observer).onChanged(expectedValue)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `test LiveData doAterNext`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val mockedBeforeNext = spy(TestOnNextAction::class.java) as OnNextAction<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.doAfterNext(mockedBeforeNext)
        val expectedValue = 3
        testingLiveData.observeForever(observer)

        sourceLiveData.value = expectedValue
        val inOrder = inOrder(mockedBeforeNext,observer)
        inOrder.verify(observer).onChanged(expectedValue)
        inOrder.verify(mockedBeforeNext, times(1))(expectedValue)
        assertEquals(expectedValue,testingLiveData.value)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `test LiveData buffer`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<List<Int?>>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.buffer(3)
        val expectedValue = listOf(1,2,3)
        val secondExpectedValue = listOf(4,5,6)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 1
        assertEquals(null,testingLiveData.value)
        sourceLiveData.value = 2
        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 3
        assertEquals(expectedValue,testingLiveData.value)
        verify(observer).onChanged(expectedValue)

        sourceLiveData.value = 4
        assertEquals(expectedValue,testingLiveData.value)
        sourceLiveData.value = 5
        assertEquals(expectedValue,testingLiveData.value)

        sourceLiveData.value = 6
        assertEquals(secondExpectedValue,testingLiveData.value)
        verify(observer).onChanged(secondExpectedValue)

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData amb and they fire after being amb-ed`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val secondSourceLiveData = MutableLiveData<Int>()
        val testingLiveData = amb(sourceLiveData,secondSourceLiveData)
        testingLiveData.observeForever(observer)

        //choose the second observer to win the race
        secondSourceLiveData.value = 1
        sourceLiveData.value = 2
        assertEquals(1,testingLiveData.value)
        verify(observer).onChanged(1)
        sourceLiveData.value = 5
        assertEquals(1,testingLiveData.value)
        secondSourceLiveData.value = 10
        assertEquals(10,testingLiveData.value)
        verify(observer).onChanged(10)

        verifyNoMoreInteractions(observer)
    }
    @Test
    fun `test LiveData amb and one has value before amb`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val secondSourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 2
        val testingLiveData = amb(sourceLiveData,secondSourceLiveData)
        testingLiveData.observeForever(observer)
        verify(observer).onChanged(2)

        //choose the second observer to win the race
        secondSourceLiveData.value = 1
        assertEquals(2,testingLiveData.value)
        sourceLiveData.value = 5
        verify(observer).onChanged(5)
        assertEquals(5,testingLiveData.value)

        secondSourceLiveData.value = 10
        assertEquals(5,testingLiveData.value)

        sourceLiveData.value = 20
        assertEquals(20,testingLiveData.value)
        verify(observer).onChanged(20)
        verify(observer).onChanged(20)

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData scan with without initial seed, does not emit first item`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.scan { acc, value ->
            value?.let { acc?.plus(it) }
        }
        testingLiveData.observeForever(observer)
        assertNull(testingLiveData.value)
        verifyZeroInteractions(observer)
    }

    @Test
    fun `test LiveData scan without initial seed`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.scan { acc, value ->
            value?.let { acc?.plus(it) }
        }
        testingLiveData.observeForever(observer)
        assertNull(testingLiveData.value)

        sourceLiveData.value = 1
        assertEquals(null, testingLiveData.value)

        sourceLiveData.value = 2
        assertEquals(3, testingLiveData.value)
        verify(observer).onChanged(3)

        sourceLiveData.value = 5
        assertEquals(8, testingLiveData.value)
        verify(observer).onChanged(8)

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData scan with initial seed`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<String>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.scan("W") { acc: String, value: Int ->
            acc + "X$value"
        }
        testingLiveData.observeForever(observer)

        assertEquals("W", testingLiveData.value)
        verify(observer).onChanged("W")

        sourceLiveData.value = 1
        assertEquals("WX1", testingLiveData.value)
        verify(observer).onChanged("WX1")

        sourceLiveData.value = 2
        assertEquals("WX1X2", testingLiveData.value)
        verify(observer).onChanged("WX1X2")

        verifyNoMoreInteractions(observer)
    }
}