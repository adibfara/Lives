package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

/**
 * Created by Adib Faramarzi
 * Emits at most one item
 */
class SingleLiveData<T>(liveData: LiveData<T>) : LiveData<T>() {
    private var hasSetValue = false
    private val mediatorLiveData = MediatorLiveData<T>()
    private val mediatorObserver = Observer<T> {
        if(!hasSetValue){
            hasSetValue=true
            this@SingleLiveData.value = it
        }
    }
    init {
        if(liveData.value!=null){
            hasSetValue=true
            this.value = value
        }else {
            mediatorLiveData.observeForever(mediatorObserver)
            mediatorLiveData.addSource(liveData, mediatorObserver)
        }
    }
    override fun onInactive() {
        super.onInactive()
        mediatorLiveData.removeObserver(mediatorObserver)
    }


}