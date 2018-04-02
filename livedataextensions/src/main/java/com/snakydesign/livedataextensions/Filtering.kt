
/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData


fun <T> LiveData<T>.distinct(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    val dispatchedValues = mutableListOf<T?>()
    mutableLiveData.addSource(this, {
        if(!dispatchedValues.contains(it)) {
            mutableLiveData.value = it
            dispatchedValues.add(it)
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var latestValue : T? = null
    mutableLiveData.addSource(this, {
        if(latestValue!=it) {
            mutableLiveData.value = it
            latestValue = it
        }
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.filter(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        if(predicate(it))
            mutableLiveData.value = it
    })
    return mutableLiveData
}

fun <T> LiveData<T>.first(): SingleLiveData<T> {
    return SingleLiveData(take(1))
}

fun <T> LiveData<T>.firstOrDefault(default:T): SingleLiveData<T> {
    return SingleLiveData(take(1).map { it ?: default })
}

fun <T> LiveData<T>.take(count:Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var takenCount = 0
    mutableLiveData.addSource(this, {
        if(takenCount<count) {
            mutableLiveData.value = it
            takenCount++
        }
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.takeUntil(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = false
    mutableLiveData.addSource(this, {
        if(predicate(it)) metPredicate = true
        if(!metPredicate) {
            mutableLiveData.value = it
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.skip(count:Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var skippedCount = 0
    mutableLiveData.addSource(this, {
        if(skippedCount>=count) {
            mutableLiveData.value = it
        }
        skippedCount++
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.skipUntil(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = false
    mutableLiveData.addSource(this, {
        if(metPredicate || predicate(it)) {
            metPredicate=true
            mutableLiveData.value = it
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.elementAt(index:Int): SingleLiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var currentIndex = 0
    mutableLiveData.addSource(this, {
        if(currentIndex==index) {
            mutableLiveData.value = it
            mutableLiveData.removeSource(this)
        }
        currentIndex++
    })
    return SingleLiveData(mutableLiveData)
}

fun <T> LiveData<T>.nonNull():LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        if(it!=null)
            mutableLiveData.value = it
    })
    return mutableLiveData
}

fun <T> LiveData<T>.defaultIfNull(default:T):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        mutableLiveData.value = it ?: default
    })
    return mutableLiveData
}