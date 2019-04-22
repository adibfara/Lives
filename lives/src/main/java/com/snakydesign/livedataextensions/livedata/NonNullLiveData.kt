package com.snakydesign.livedataextensions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * Created by Adib Faramarzi
 * Only emits values that are not null
 */
class NonNullLiveData<T>(liveData:LiveData<T>) : LiveData<T>() {
    private val mediatorLiveData = MediatorLiveData<T>()
    private val mediatorObserver = Observer<T> {
        it?.let{
            this@NonNullLiveData.value = it
        }
    }
    init {
            mediatorLiveData.observeForever(mediatorObserver)
            mediatorLiveData.addSource(liveData, mediatorObserver)
    }
    override fun onInactive() {
        super.onInactive()
        mediatorLiveData.removeObserver(mediatorObserver)
    }

}
