
/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass
package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.snakydesign.livedataextensions.livedata.NonNullLiveData
import com.snakydesign.livedataextensions.livedata.SingleLiveData


/**
 * Emits the items that are different from all the values that have been emitted so far
 */
fun <T> LiveData<T>.distinct(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    val dispatchedValues = mutableSetOf<T?>()
    mutableLiveData.addSource(this) {
        if(!dispatchedValues.contains(it)) {
            mutableLiveData.value = it
            dispatchedValues.add(it)
        }
    }
    return mutableLiveData
}

/**
 * Emits the items that are different from the last item
 */
fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var latestValue : T? = null
    mutableLiveData.addSource(this) {
        if(latestValue!=it) {
            mutableLiveData.value = it
            latestValue = it
        }
    }
    return mutableLiveData
}

/**
 * Emits the items that pass through the predicate
 */
inline fun <T> LiveData<T>.filter(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if(predicate(it))
            mutableLiveData.value = it
    }
    return mutableLiveData
}

/**
 * Emits at most 1 item and returns a SingleLiveData
 */
fun <T> LiveData<T>.first(): SingleLiveData<T> {
    return SingleLiveData(this)
}

/**
 * Emits the first n valueus
 */
fun <T> LiveData<T>.take(count:Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var takenCount = 0
    mutableLiveData.addSource(this) {
        if(takenCount<count) {
            mutableLiveData.value = it
            takenCount++
        } else {
            mutableLiveData.removeSource(this)
        }
    }
    return mutableLiveData
}

/**
 * Takes until a certain predicate is met, and does not emit anything after that, whatever the value.
 */
inline fun <T> LiveData<T>.takeUntil(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = predicate(value)
    mutableLiveData.addSource(this) {
        if(predicate(it)) metPredicate = true
        if(!metPredicate) {
            mutableLiveData.value = it
        } else {
            mutableLiveData.removeSource(this)
        }
    }
    return mutableLiveData
}

/**
 * Skips the first n values
 */
fun <T> LiveData<T>.skip(count:Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var skippedCount = 0
    mutableLiveData.addSource(this) {
        if(skippedCount>=count) {
            mutableLiveData.value = it
        }
        skippedCount++
    }
    return mutableLiveData
}

/**
 * Skips all values until a certain predicate is met (the item that actives the predicate is also emitted)
 */
inline fun <T> LiveData<T>.skipUntil(crossinline predicate : (T?)->Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = false
    mutableLiveData.addSource(this) {
        if(metPredicate || predicate(it)) {
            metPredicate=true
            mutableLiveData.value = it
        }
    }
    return mutableLiveData
}

/**
 * emits the item that was emitted at `index` position
 * Note: This only works for elements that were emitted `after` the `elementAt` is applied.
 */
fun <T> LiveData<T>.elementAt(index:Int): SingleLiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var currentIndex = 0
    if(this.value != null)
        currentIndex = -1
    mutableLiveData.addSource(this) {
        if(currentIndex==index) {
            mutableLiveData.value = it
            mutableLiveData.removeSource(this)
        }
        currentIndex++
    }
    return SingleLiveData(mutableLiveData)
}

/**
 * Emits only the values that are not null
 */
fun <T> LiveData<T>.nonNull(): NonNullLiveData<T> {
    return NonNullLiveData(this)
}

/**
 * Emits the default value when a null value is emitted
 */
fun <T> LiveData<T>.defaultIfNull(default:T):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        mutableLiveData.value = it ?: default
    }
    return mutableLiveData
}