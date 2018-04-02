package com.snakydesign.livedataextensions

import android.annotation.SuppressLint
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.core.util.Function
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import filter
import map
import mutableLiveDataOf
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

/**
 * Created by Adib Faramarzi
 */
class LDExtTest {

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
    fun `test LiveData creation`(){
        val testingLiveData = mutableLiveDataOf(2)
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        assertEquals(2,testingLiveData.value)

        testingLiveData.observeForever(observer)
        verify(observer).onChanged(2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData filter initial item`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val testingLiveData = mutableLiveDataOf(2).filter {
            it!=null && it > 1
        }
        testingLiveData.observeForever(observer)

        assertEquals(2,testingLiveData.value)
        verify(observer).onChanged(2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `test LiveData filter multiple items`(){
        val observer= Mockito.mock(Observer::class.java) as Observer<Int>
        val originalLiveData = mutableLiveDataOf<Int>()
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
        val testingLiveData = mutableLiveDataOf(2).map { 3 }
        testingLiveData.observeForever(observer)

        assertEquals(3,testingLiveData.value)
        verify(observer).onChanged(3)
        verifyNoMoreInteractions(observer)
    }

}