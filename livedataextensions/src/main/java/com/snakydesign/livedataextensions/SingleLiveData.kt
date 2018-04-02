package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

/**
 * Created by Adib Faramarzi
 * Emits at most one item
 */
class SingleLiveData<T>(liveData: LiveData<T>) : LiveData<T>() {
    private val mediatorLiveData = MediatorLiveData<T>()
    private val mediatorObserver = Observer<T> {
        if(!hasSetValue){
            hasSetValue=true
            this@SingleLiveData.value = value
        }
    }
    init {
        mediatorLiveData.observeForever(mediatorObserver)
        mediatorLiveData.addSource(liveData, mediatorObserver)
    }
    private var hasSetValue = false
    override fun onInactive() {
        super.onInactive()
        mediatorLiveData.removeObserver(mediatorObserver)
    }


}