
/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations

fun <T,O> LiveData<T>.map(  function : (T)->O):LiveData<O>{
    return Transformations.map(this,function)
}

fun <T,O> LiveData<T>.switchMap(function : (T)->LiveData<O>):LiveData<O>{
    return Transformations.switchMap(this,function)
}

inline fun <T> LiveData<T>.doBeforeNext(crossinline onNext : (T?)->Unit):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        onNext(it)
        mutableLiveData.value = it
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.doAfterNext(crossinline onNext : (T?)->Unit):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        mutableLiveData.value = it
        onNext(it)
    })
    return mutableLiveData
}

fun <T> LiveData<T>.buffer(count:Int): LiveData<List<T?>> {
    val mutableLiveData: MediatorLiveData<List<T?>> = MediatorLiveData()
    val latestBuffer = mutableListOf<T?>()
    mutableLiveData.addSource(this, {
        value->
        synchronized(latestBuffer,{
            latestBuffer.add(value)
            if (latestBuffer.size == count){
                mutableLiveData.value = latestBuffer.toList()
                latestBuffer.clear()
            }
        })

    })
    return mutableLiveData
}

fun <T> amb(vararg inputLiveData: LiveData<T>, considerNulls:Boolean = true): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var activeLiveDataIndex = -1
    inputLiveData.forEachIndexed {
        index, liveData ->
        mutableLiveData.addSource(liveData, {
            value ->
            if(considerNulls || value != null){
                synchronized(activeLiveDataIndex,{
                        activeLiveDataIndex = index
                })
                inputLiveData.forEachIndexed { index, liveData ->
                    if(index != activeLiveDataIndex){
                        mutableLiveData.removeSource(liveData)
                    }
                }

                if(index == activeLiveDataIndex){
                    mutableLiveData.value = value
                }
            }



        })
    }
    return mutableLiveData
}

fun <T> LiveData<T>.toSingleLiveData():SingleLiveData<T> = SingleLiveData(this)