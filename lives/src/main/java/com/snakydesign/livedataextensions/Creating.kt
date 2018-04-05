/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * Creates a LiveData that emits the initialValue immediately.
 */
fun<T> just(initialValue:T): MutableLiveData<T> {
    val returnedLiveData = MutableLiveData<T>()
    if(initialValue!=null)
        returnedLiveData.value = initialValue
    return returnedLiveData
}


/**
 * Creates a LiveData that emits the value that the `callable` function produces, immediately.
 */
fun<T> from(callable : ()->T):LiveData<T>{
    val returnedLiveData = MutableLiveData<T>()
    returnedLiveData.value = callable.invoke()
    return returnedLiveData
}
