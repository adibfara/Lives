/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

/**
 * Merges this LiveData with another one, and emits any item that was emitted by any of them
 */
fun <T> LiveData<T>.mergeWith(vararg liveDatas : LiveData<T>): LiveData<T> {
    val mergeWithArray = mutableListOf<LiveData<T>>()
    mergeWithArray.add(this)
    mergeWithArray.addAll(liveDatas)
    return merge(mergeWithArray)
}


/**
 * Merges multiple LiveData, and emits any item that was emitted by any of them
 */
fun <T> merge(liveDataList : List<LiveData<T>>): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    liveDataList.forEach {
        liveData->

        liveData.value?.let{
            finalLiveData.value = it
        }

        finalLiveData.addSource(liveData, {
            source->
            finalLiveData.value = source
        })
    }
    return finalLiveData
}

/**
 * Emits the `startingValue` before any other value.
 */
fun <T> LiveData<T>.startWith(startingValue:T?):LiveData<T>{
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    finalLiveData.value = startingValue
    finalLiveData.addSource(this, { source ->
        finalLiveData.value = source
    })
    return finalLiveData
}

/**
 * zips both of the LiveData and emits a value after both of them have emitted their values,
 * after that, emits values whenever any of them emits a value.
 */
fun <T,Y> zip(first : LiveData<T>, second : LiveData<Y>): LiveData<Pair<T,Y>> {
    val finalLiveData: MediatorLiveData<Pair<T,Y>> = MediatorLiveData()
    
    var firstEmitted = false
    var firstValue:T? = null
    
    var secondEmitted = false
    var secondValue:Y? = null
    finalLiveData.addSource(first, {
        value->
        firstEmitted=true
        firstValue = value
        synchronized(finalLiveData,{
            if (firstEmitted && secondEmitted){
                finalLiveData.value = Pair(firstValue!!,secondValue!!)
            }
        })
    })
    finalLiveData.addSource(second, {
        value->
        secondEmitted=true
        secondValue = value
        synchronized(finalLiveData,{
            if (firstEmitted && secondEmitted){
                finalLiveData.value = Pair(firstValue!!,secondValue!!)
            }
        })
    })
    return finalLiveData
}


/**
 * zips three LiveData and emits a value after all of them have emitted their values,
 * after that, emits values whenever any of them emits a value.
 */
fun <T,Y,X> zip(first : LiveData<T>, second : LiveData<Y>, third : LiveData<X>): LiveData<Triple<T,Y,X>> {
    val finalLiveData: MediatorLiveData<Triple<T,Y,X>> = MediatorLiveData()
    
    var firstEmitted = false
    var firstValue:T? = null
    
    var secondEmitted = false
    var secondValue:Y? = null
    
    var thirdEmitted = false
    var thirdValue:X? = null
    finalLiveData.addSource(first, {
        value->
        firstEmitted=true
        firstValue = value
        if (firstEmitted && secondEmitted && thirdEmitted){
            finalLiveData.value = Triple(firstValue!!,secondValue!!,thirdValue!!)
        }
    })
    
    finalLiveData.addSource(second, {
        value->
        secondEmitted=true
        secondValue = value
        if (firstEmitted && secondEmitted && thirdEmitted){
            finalLiveData.value = Triple(firstValue!!,secondValue!!,thirdValue!!)
        }
    })
    
    finalLiveData.addSource(third, {
        value->
        thirdEmitted=true
        thirdValue = value
        if (firstEmitted && secondEmitted && thirdEmitted){
            finalLiveData.value = Triple(firstValue!!,secondValue!!,thirdValue!!)
        }
    })
    
    return finalLiveData
}