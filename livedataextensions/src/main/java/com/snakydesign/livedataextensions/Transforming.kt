
/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations

/**
 * maps any values that were emitted by the LiveData to the given function
 */
fun <T,O> LiveData<T>.map(function : MapperFunction<T,O>):LiveData<O>{
    return Transformations.map(this,function)
}

/**
 * maps any values that were emitted by the LiveData to the given function that produces another LiveData
 */
fun <T,O> LiveData<T>.switchMap(function : MapperFunction<T,LiveData<O>>):LiveData<O>{
    return Transformations.switchMap(this,function)
}

/**
 * Does the `onNext` function before everything actually emitting the item to the observers
 */
fun <T> LiveData<T>.doBeforeNext(onNext : OnNextAction<T>):MutableLiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        onNext.performAction(it)
        mutableLiveData.value = it
    }
    return mutableLiveData
}

/**
 * Does the `onNext` function after emitting the item to the observers
 */
fun <T> LiveData<T>.doAfterNext(onNext : OnNextAction<T>):MutableLiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        mutableLiveData.value = it
        onNext.performAction(it)
    }
    return mutableLiveData
}

/**
 * Buffers the items emitted by the LiveData, and emits them when they reach the `count` as a List.
 */
fun <T> LiveData<T>.buffer(count:Int): MutableLiveData<List<T?>> {
    val mutableLiveData: MediatorLiveData<List<T?>> = MediatorLiveData()
    val latestBuffer = mutableListOf<T?>()
    mutableLiveData.addSource(this) { value ->
        synchronized(latestBuffer) {
            latestBuffer.add(value)
            if (latestBuffer.size == count){
                mutableLiveData.value = latestBuffer.toList()
                latestBuffer.clear()
            }
        }

    }
    return mutableLiveData
}

/**
 * Emits the items of the first LiveData that emits the item. Items of other LiveDatas will never be emitted and are not considered.
 */
fun <T> amb(vararg inputLiveData: LiveData<T>, considerNulls:Boolean = true): MutableLiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()

    var activeLiveDataIndex = inputLiveData.indexOfFirst { it.value !=null }
    if (activeLiveDataIndex>=0){
        mutableLiveData.value = inputLiveData[activeLiveDataIndex].value
    }
    inputLiveData.forEachIndexed {
        index, liveData ->
        mutableLiveData.addSource(liveData) {
            value ->
            if(considerNulls || value != null){
                synchronized(activeLiveDataIndex) {
                    activeLiveDataIndex = index
                }
                inputLiveData.forEachIndexed { index, liveData ->
                    if(index != activeLiveDataIndex){
                        mutableLiveData.removeSource(liveData)
                    }
                }

                if(index == activeLiveDataIndex){
                    mutableLiveData.value = value
                }
            }


        }
    }
    return mutableLiveData
}

/**
 * Converts a LiveData to a SingleLiveData (exactly similar to LiveData.first()
 */
fun <T> LiveData<T>.toSingleLiveData():SingleLiveData<T> = first()

/**
 * Converts a LiveData to a MutableLiveData with the initial value set by this LiveData's value
 */
fun <T> LiveData<T>.toMutableLiveData():MutableLiveData<T>{
    val liveData =  MutableLiveData<T>()
    liveData.value = this.value
    return liveData
}

/**
 * Mapper function used in the operators that need mapping
 */
typealias MapperFunction<T,O> = (T)->O