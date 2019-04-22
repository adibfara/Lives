package com.snakydesign.livedataextensions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * Created by Adib Faramarzi
 * Emits at most one item
 */
class SingleLiveData<T>(liveData: LiveData<T>) : MediatorLiveData<T>() {
    private var hasSetValue = false
    private val mediatorObserver = Observer<T> {
            if(!hasSetValue){
                hasSetValue=true
                this@SingleLiveData.value = it
            }
    }
    init {
        if(liveData.value!=null){
            hasSetValue=true
            this.value = liveData.value
        }else {
            addSource(liveData, mediatorObserver)
        }
    }


}