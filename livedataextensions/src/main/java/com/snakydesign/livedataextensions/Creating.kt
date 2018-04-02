/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

fun<T> just(initialValue:T): MutableLiveData<T> {
    val returnedLiveData = MutableLiveData<T>()
    if(initialValue!=null)
        returnedLiveData.value = initialValue
    return returnedLiveData
}


fun rangeOf(initialValue: Int, count:Int): LiveData<Int> {
    val returnedLiveData = MutableLiveData<Int>()
    initialValue.rangeTo(count).forEach { returnedLiveData.value = it }
    return returnedLiveData
}

fun<T> from(callable : ()->T):LiveData<T>{
    val returnedLiveData = MutableLiveData<T>()
    returnedLiveData.value = callable.invoke()
    return returnedLiveData
}