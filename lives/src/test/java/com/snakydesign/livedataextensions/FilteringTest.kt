package com.snakydesign.livedataextensions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.junit.*
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.lang.IllegalStateException
import kotlin.test.assertEquals

/**
 * Created by Adib Faramarzi
 */
@Suppress("UNCHECKED_CAST")
class FilteringTest {


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
    fun `test LiveData filter`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val testingLiveData = liveDataOf(2).filter {
            it!=null && it > 1
        }
        testingLiveData.observeForever(observer)

        Assert.assertEquals(2, testingLiveData.value)
        Mockito.verify(observer).onChanged(2)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData distinct`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.distinct()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData distinct when new value inserted`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.distinct()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 2
        assertEquals(3,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData distinctUntilChanged same value given`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.distinctUntilChanged()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData distinctUntilChanged updating source from observer doesn't trigger change`() {
        val sourceLiveData = MutableLiveData<Int>()
        var timesCalled = 0
        val observer = Observer<Int> { t ->
            timesCalled++
            // set same value from observer
            sourceLiveData.value = t
        }
        val testingLiveData = sourceLiveData.distinctUntilChanged()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2

        if (timesCalled > 1) {
            throw IllegalStateException()
        }
    }

    @Test
    fun `test LiveData distinctUntilChanged`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.distinctUntilChanged()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData first without initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.first()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(2,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData first with initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 2
        val testingLiveData = sourceLiveData.first()
        testingLiveData.observeForever(observer)

        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(2,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData take(2) without initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.take(2)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 4

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData take(2) with initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 2
        val testingLiveData = sourceLiveData.take(2)
        testingLiveData.observeForever(observer)

        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 4

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData takeUntil without initial predicate met`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.takeUntil { it!=null && it >= 3 }
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 1
        assertEquals(1,testingLiveData.value)
        Mockito.verify(observer).onChanged(1)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3

        sourceLiveData.value = 2

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData takeUntil with initial predicate met`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 5
        val testingLiveData = sourceLiveData.takeUntil { it!=null && it >= 3 }
        testingLiveData.observeForever(observer)

        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 3

        sourceLiveData.value = 2

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData skip(1) without initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.skip(1)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 4
        assertEquals(4,testingLiveData.value)
        Mockito.verify(observer).onChanged(4)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData skip(1) with initial value`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 2
        val testingLiveData = sourceLiveData.skip(1)
        testingLiveData.observeForever(observer)

        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 4
        assertEquals(4,testingLiveData.value)
        Mockito.verify(observer).onChanged(4)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData skipUntil without initial predicate met`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.skipUntil { it!=null && it >= 3 }
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 1
        sourceLiveData.value = 2

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 1
        assertEquals(1,testingLiveData.value)
        Mockito.verify(observer).onChanged(1)

        sourceLiveData.value = 7
        assertEquals(7,testingLiveData.value)
        Mockito.verify(observer).onChanged(7)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData skipUntil with initial predicate met`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 3
        val testingLiveData = sourceLiveData.skipUntil { it!=null && it >= 3 }
        testingLiveData.observeForever(observer)

        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = 1
        assertEquals(1,testingLiveData.value)
        Mockito.verify(observer).onChanged(1)

        sourceLiveData.value = 7
        assertEquals(7,testingLiveData.value)
        Mockito.verify(observer).onChanged(7)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData elementAt(1) without initial values`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        val testingLiveData = sourceLiveData.elementAt(1)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 1
        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(2,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData elementAt(1) with initial values that should be ignored`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = 1
        sourceLiveData.value = 2
        val testingLiveData = sourceLiveData.elementAt(1)
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 3
        assertEquals(null,testingLiveData.value)

        sourceLiveData.value = 4
        assertEquals(4,testingLiveData.value)
        Mockito.verify(observer).onChanged(4)

        sourceLiveData.value = 5
        assertEquals(4,testingLiveData.value)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData nonNull when initial is emitted a null`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = null
        val testingLiveData = sourceLiveData.nonNull()
        testingLiveData.observeForever(observer)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData nonNull when initial is emitted a null and emits non null values after`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = null
        val testingLiveData = sourceLiveData.nonNull()
        testingLiveData.observeForever(observer)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = null

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData defaultIfNull when initial is emitted a null`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = null
        val testingLiveData = sourceLiveData.defaultIfNull(2)
        testingLiveData.observeForever(observer)

        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData defaultIfNull when initial is emitted a null and emits non null values after`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val sourceLiveData = MutableLiveData<Int>()
        sourceLiveData.value = null
        val testingLiveData = sourceLiveData.defaultIfNull(5)
        testingLiveData.observeForever(observer)

        assertEquals(5,testingLiveData.value)
        Mockito.verify(observer).onChanged(5)

        sourceLiveData.value = 2
        assertEquals(2,testingLiveData.value)
        Mockito.verify(observer).onChanged(2)

        sourceLiveData.value = 3
        assertEquals(3,testingLiveData.value)
        Mockito.verify(observer).onChanged(3)

        sourceLiveData.value = null
        assertEquals(5,testingLiveData.value)
        Mockito.verify(observer,times(2)).onChanged(5)

        Mockito.verifyNoMoreInteractions(observer)
    }
}