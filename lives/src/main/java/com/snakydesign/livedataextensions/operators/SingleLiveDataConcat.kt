package com.snakydesign.livedataextensions.operators

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import com.snakydesign.livedataextensions.livedata.SingleLiveData

/**
 * Created by Adib Faramarzi (adibfara@gmail.com)
 */
class SingleLiveDataConcat<T>(liveDataList:List<SingleLiveData<T>>): MediatorLiveData<T>() {
    constructor(vararg liveData:SingleLiveData<T>):this(liveData.toList())

    private val emittedValues = mutableListOf<T?>()
    private val hasEmittedValues = mutableListOf<Boolean>()
    private var lastEmittedLiveDataIndex = -1
    init {
        (0 until liveDataList.size).forEach {
            index->
            emittedValues.add(null)
            hasEmittedValues.add(false)
            addSource(liveDataList[index] , {
                emittedValues[index] = it
                hasEmittedValues[index] = true
                removeSource(this)
                checkEmit()
            })
        }
    }

    private fun checkEmit(){
        if(lastEmittedLiveDataIndex >= emittedValues.size)
            return
        synchronized(hasEmittedValues){
            while (hasEmittedValues[lastEmittedLiveDataIndex+1]){
                value = emittedValues[lastEmittedLiveDataIndex+1]
                lastEmittedLiveDataIndex += 1
            }
        }
    }
}