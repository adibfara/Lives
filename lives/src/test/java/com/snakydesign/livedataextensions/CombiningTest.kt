package com.snakydesign.livedataextensions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.verifyZeroInteractions

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
    fun `test LiveData merge multiple LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData1 = MutableLiveData<Int>()
        val sourceLiveData2 = MutableLiveData<Int>()
        sourceLiveData1.value = 2
        sourceLiveData2.value = 3
        val testingLiveData = merge(listOf(sourceLiveData1, sourceLiveData2))
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
    fun `test LiveData merge with another LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
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
    fun `test LiveData startWith`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
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
    fun `test LiveData zip with another LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Pair<Boolean?, Int?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val expectedResult = Pair(true, 3)
        val testingLiveData = zip(sourceLiveData1, sourceLiveData2) { b, i -> Pair(b, i) }
        testingLiveData.observeForever(observer)


        sourceLiveData1.value = true
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData2.value = 3
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)
        sourceLiveData2.value = 4 // should not trigger the live data

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData zip with another LiveData for second emission`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Pair<Boolean?, Int?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val expectedResult = Pair(true, 3)
        val expectedResult2 = Pair(false, 4)
        val testingLiveData = zip(sourceLiveData1, sourceLiveData2) { b, i -> Pair(b, i) }
        testingLiveData.observeForever(observer)


        sourceLiveData1.value = true
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData2.value = 3
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)
        sourceLiveData2.value = 4
        sourceLiveData1.value = false // should not trigger the live data

        verify(observer).onChanged(expectedResult2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData zip with 2 other LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Triple<Boolean?, Int?, String?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<String>()
        val expectedResult = Triple(true, 3, "hello")
        val testingLiveData = zip(sourceLiveData1, sourceLiveData2, sourceLiveData3) { b, i, s -> Triple(b, i, s) }
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

    @Test
    fun `test LiveData zip with null values`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Triple<Boolean?, Int?, String?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<String>()
        val testingLiveData = zip(sourceLiveData1, sourceLiveData2, sourceLiveData3)
        testingLiveData.observeForever(observer)

        // Ensure there is no emit until all sources have emitted
        sourceLiveData1.value = null
        sourceLiveData2.value = null
        Assert.assertEquals(null, testingLiveData.value)
        verify(observer, never()).onChanged(any())

        // After all emitted null, we expect an emit with null values
        sourceLiveData3.value = null
        val expectedResult = Triple<Boolean?, Int?, String?>(null, null, null)
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)

        // Ensure there is no emit until all sources have emitted a new value
        sourceLiveData2.value = 42
        sourceLiveData3.value = "42"
        verifyZeroInteractions(observer)

        // After all emitted new value, we expect another emit
        sourceLiveData1.value = true
        val expectedResult2 = Triple<Boolean?, Int?, String?>(true, 42, "42")
        Assert.assertEquals(expectedResult2, testingLiveData.value)
        verify(observer).onChanged(expectedResult2)
    }

    @Test
    fun `test LiveData sample with another LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Triple<Boolean?, Int?, String?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<String>()
        val expectedResult = Triple(true, 3, "hello")
        val testingLiveData = zip(sourceLiveData1, sourceLiveData2, sourceLiveData3) { b, i, s -> Triple(b, i, s) }
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

    @Test
    fun `test LiveData combineLatest with another LiveData`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Pair<Boolean?, Int?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val expectedResult = Pair(true, 3)
        val expectedResult2 = Pair(true, 4)
        val expectedResult3 = Pair(false, 4)
        val testingLiveData = combineLatest(sourceLiveData1, sourceLiveData2) { boolean, int -> Pair(boolean, int) }
        testingLiveData.observeForever(observer)


        sourceLiveData1.value = true
        Assert.assertEquals(null, testingLiveData.value)
        sourceLiveData2.value = 3

        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)

        sourceLiveData2.value = 4

        Assert.assertEquals(expectedResult2, testingLiveData.value)
        verify(observer).onChanged(expectedResult2)

        sourceLiveData1.value = false


        Assert.assertEquals(expectedResult3, testingLiveData.value)
        verify(observer).onChanged(expectedResult3)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData combineLatest with null values`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Pair<Boolean?, Int?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val testingLiveData = combineLatest(sourceLiveData1, sourceLiveData2) { boolean, int -> Pair(boolean, int) }
        testingLiveData.observeForever(observer)

        // Ensure there is no emit until all sources have emitted
        sourceLiveData1.value = null
        Assert.assertEquals(null, testingLiveData.value)
        verify(observer, never()).onChanged(any())

        // After all emitted null, we expect an emit with null values
        sourceLiveData2.value = null
        val expectedResult = Pair<Boolean?, Int?>(null, null)
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)

        // One emitted a non-null value
        sourceLiveData2.value = 4
        val expectedResult2 = Pair<Boolean?, Int?>(null, 4)
        Assert.assertEquals(expectedResult2, testingLiveData.value)
        verify(observer).onChanged(expectedResult2)

        // Both emitted a non-null value
        sourceLiveData1.value = false
        val expectedResult3 = Pair(false, 4)
        Assert.assertEquals(expectedResult3, testingLiveData.value)
        verify(observer).onChanged(expectedResult3)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData combineLatest three with null values`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Triple<Boolean?, Int?, Long?>>
        val sourceLiveData1 = MutableLiveData<Boolean>()
        val sourceLiveData2 = MutableLiveData<Int>()
        val sourceLiveData3 = MutableLiveData<Long>()
        val testingLiveData = combineLatest(sourceLiveData1, sourceLiveData2, sourceLiveData3) { boolean, int, long ->
            Triple(boolean, int, long)
        }
        testingLiveData.observeForever(observer)

        // Ensure there is no emit until all sources have emitted
        sourceLiveData1.value = null
        sourceLiveData2.value = null
        Assert.assertEquals(null, testingLiveData.value)
        verify(observer, never()).onChanged(any())

        // After all emitted null, we expect an emit with null values
        sourceLiveData3.value = null
        val expectedResult = Triple<Boolean?, Int?, Long?>(null, null, null)
        Assert.assertEquals(expectedResult, testingLiveData.value)
        verify(observer).onChanged(expectedResult)
    }

    @Test
    fun `test LiveData sampleWith another live data`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val samplerLiveData = MutableLiveData<Boolean>()
        val testingLiveData = sourceLiveData.sampleWith(samplerLiveData)
        testingLiveData.observeForever(observer)


        sourceLiveData.value = 1
        Assert.assertNull(testingLiveData.value)

        sourceLiveData.value = 2
        Assert.assertNull(testingLiveData.value)
        samplerLiveData.value = true
        Assert.assertEquals(2, testingLiveData.value)
        verify(observer).onChanged(2)

        sourceLiveData.value = 3
        Assert.assertEquals(2, testingLiveData.value)
        samplerLiveData.value = true
        Assert.assertEquals(3, testingLiveData.value)
        verify(observer).onChanged(3)

        samplerLiveData.value = true
        sourceLiveData.value = 5

        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData sampleWith another live data to not emit when there are no new values`() {
        val observer = Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val samplerLiveData = MutableLiveData<Boolean>()
        val testingLiveData = sourceLiveData.sampleWith(samplerLiveData)
        testingLiveData.observeForever(observer)

        samplerLiveData.value = true
        samplerLiveData.value = true
        samplerLiveData.value = true
        Assert.assertNull(testingLiveData.value)
        sourceLiveData.value = 5
        samplerLiveData.value = true
        Assert.assertEquals(5, testingLiveData.value)
        verify(observer).onChanged(5)

        verifyNoMoreInteractions(observer)
    }
}
