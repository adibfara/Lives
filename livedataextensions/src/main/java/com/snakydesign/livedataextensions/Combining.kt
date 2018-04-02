/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.mergeWith(vararg liveDatas : LiveData<T>): LiveData<T> {
    val mergeWithArray = mutableListOf<LiveData<T>>()
    mergeWithArray.addAll(liveDatas)
    mergeWithArray.add(this)
    return merge(mergeWithArray)
}

fun <T> merge(liveDataList : List<LiveData<T>>): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    liveDataList.forEach {
        liveData->
        finalLiveData.addSource(liveData, {
            source->
            finalLiveData.value = source
        })
    }
    return finalLiveData
}

fun <T> LiveData<T>.startWith(startingValue:T?):LiveData<T>{
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    finalLiveData.value = startingValue
    finalLiveData.addSource(this, { source ->
        finalLiveData.value = source
    })
    return finalLiveData
}
fun <T,Y> zip(first : LiveData<T>, second : SingleLiveData<Y>): SingleLiveData<Pair<T,Y>> {
    val finalLiveData: MediatorLiveData<Pair<T,Y>> = MediatorLiveData()
    
    var firstEmitted = false
    var firstValue:T? = null
    
    var secondEmitted = false
    var secondValue:Y? = null
    finalLiveData.addSource(first, {
        value->
        firstEmitted=true
        firstValue = value
        finalLiveData.removeSource(first)
        if (firstEmitted && secondEmitted){
            firstValue?.let{
                firstValue->
                secondValue?.let{
                    secondValue->
                    finalLiveData.value = Pair(firstValue,secondValue)
                }
            }
        }
    })
    finalLiveData.addSource(second, {
        value->
        secondEmitted=true
        secondValue = value
        finalLiveData.removeSource(second)
        if (secondEmitted && firstEmitted){
            secondValue?.let{
                secondValue->
                firstValue?.let{
                    firstValue->
                    finalLiveData.value = Pair(firstValue,secondValue)
                }
            }
        }
    })
    return SingleLiveData(finalLiveData)
}
fun <T,Y,X> zip(first : LiveData<T>, second : SingleLiveData<Y>, third : SingleLiveData<X>): SingleLiveData<Triple<T,Y,X>> {
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
        finalLiveData.removeSource(first)
        if (firstEmitted && secondEmitted && thirdEmitted){
            firstValue?.let{
                firstValue->
                secondValue?.let{
                    secondValue->
                    thirdValue?.let {
                        thirdValue->
                        finalLiveData.value = Triple(firstValue,secondValue,thirdValue)
                    }
                }
            }
        }
    })
    
    finalLiveData.addSource(second, {
        value->
        secondEmitted=true
        secondValue = value
        finalLiveData.removeSource(second)
        if (firstEmitted && secondEmitted && thirdEmitted){
            secondValue?.let{
                secondValue->
                firstValue?.let{
                    firstValue->
                    thirdValue?.let {
                        thirdValue->
                        finalLiveData.value = Triple(firstValue,secondValue,thirdValue)
                    }
                }
            }
        }
    })
    
    finalLiveData.addSource(third, {
        value->
        thirdEmitted=true
        thirdValue = value
        finalLiveData.removeSource(third)
        if (firstEmitted && secondEmitted && thirdEmitted){
            firstValue?.let{
                firstValue->
                secondValue?.let{
                    secondValue->
                    thirdValue?.let {
                        thirdValue->
                        finalLiveData.value = Triple(firstValue,secondValue,thirdValue)
                    }
                }
            }
        }
    })
    
    return SingleLiveData(finalLiveData)
}