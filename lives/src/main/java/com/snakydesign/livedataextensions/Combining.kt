/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass

package com.snakydesign.livedataextensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.snakydesign.livedataextensions.livedata.SingleLiveData
import com.snakydesign.livedataextensions.operators.SingleLiveDataConcat
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Merges this LiveData with another one, and emits any item that was emitted by any of them
 */
fun <T> LiveData<T>.mergeWith(vararg liveDatas: LiveData<T>): LiveData<T> {
    val mergeWithArray = mutableListOf<LiveData<T>>()
    mergeWithArray.add(this)
    mergeWithArray.addAll(liveDatas)
    return merge(mergeWithArray)
}


/**
 * Merges multiple LiveData, and emits any item that was emitted by any of them
 */
fun <T> merge(liveDataList: List<LiveData<T>>): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    liveDataList.forEach { liveData ->

        liveData.value?.let {
            finalLiveData.value = it
        }

        finalLiveData.addSource(liveData) { source ->
            finalLiveData.value = source
        }
    }
    return finalLiveData
}

/**
 * Emits the `startingValue` before any other value.
 */
fun <T> LiveData<T>.startWith(startingValue: T?): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    finalLiveData.value = startingValue
    finalLiveData.addSource(this) { source ->
        finalLiveData.value = source
    }
    return finalLiveData
}

/**
 * zips both of the LiveData and emits a value after both of them have emitted their values,
 * after that, emits values whenever any of them emits a value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T, Y> zip(first: LiveData<T>, second: LiveData<Y>): LiveData<Pair<T, Y>> {
    return zip(first, second) { t, y -> Pair(t, y) }
}

fun <T, Y, Z> zip(first: LiveData<T>, second: LiveData<Y>, zipFunction: (T, Y) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    var firstEmitted = false
    var firstValue: T? = null

    var secondEmitted = false
    var secondValue: Y? = null
    finalLiveData.addSource(first) { value ->
        firstEmitted = true
        firstValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = zipFunction(firstValue!!, secondValue!!)
                firstEmitted = false
                secondEmitted = false
            }
        }
    }
    finalLiveData.addSource(second) { value ->
        secondEmitted = true
        secondValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = zipFunction(firstValue!!, secondValue!!)
                firstEmitted = false
                secondEmitted = false
            }
        }
    }
    return finalLiveData
}


/**
 * zips three LiveData and emits a value after all of them have emitted their values,
 * after that, emits values whenever any of them emits a value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T, Y, X, Z> zip(first: LiveData<T>, second: LiveData<Y>, third: LiveData<X>, zipFunction: (T, Y, X) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    var firstEmitted = false
    var firstValue: T? = null

    var secondEmitted = false
    var secondValue: Y? = null

    var thirdEmitted = false
    var thirdValue: X? = null
    finalLiveData.addSource(first) { value ->
        firstEmitted = true
        firstValue = value
        if (firstEmitted && secondEmitted && thirdEmitted) {
            finalLiveData.value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
        }
    }

    finalLiveData.addSource(second) { value ->
        secondEmitted = true
        secondValue = value
        if (firstEmitted && secondEmitted && thirdEmitted) {
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
            finalLiveData.value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
        }
    }

    finalLiveData.addSource(third) { value ->
        thirdEmitted = true
        thirdValue = value
        if (firstEmitted && secondEmitted && thirdEmitted) {
            firstEmitted = false
            secondEmitted = false
            thirdEmitted = false
            finalLiveData.value = zipFunction(firstValue!!, secondValue!!, thirdValue!!)
        }
    }

    return finalLiveData
}

fun <T, Y, X> zip(first: LiveData<T>, second: LiveData<Y>, third: LiveData<X>): LiveData<Triple<T, Y, X>> {
    return zip(first, second, third) { t, y, x -> Triple(t, y, x) }
}

/**
 * Combines the latest values from two LiveData objects.
 * First emits after both LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, T, Z> combineLatest(first: LiveData<X>, second: LiveData<T>, combineFunction: (X, T) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    var firstEmitted = false
    var firstValue: X? = null

    var secondEmitted = false
    var secondValue: T? = null
    finalLiveData.addSource(first) { value ->
        firstEmitted = true
        firstValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
            }
        }
    }
    finalLiveData.addSource(second) { value ->
        secondEmitted = true
        secondValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
            }
        }
    }
    return finalLiveData
}
/**
 * Combines the latest values from two LiveData objects.
 * First emits after both LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, T, Z> combineLatest(first: LiveData<X>, second: LiveData<Y>, third: LiveData<T>, combineFunction: (X, Y, T) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    var firstEmitted = false
    var firstValue: X? = null

    var secondEmitted = false
    var secondValue: Y? = null

    var thirdEmitted = false
    var thirdValue: T? = null
    finalLiveData.addSource(first) { value ->
        firstEmitted = true
        firstValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted && thirdEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!, thirdValue!!)
            }
        }
    }
    finalLiveData.addSource(second) { value ->
        secondEmitted = true
        secondValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted && thirdEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!, thirdValue!!)
            }
        }
    }
    finalLiveData.addSource(third) { value ->
        thirdEmitted = true
        thirdValue = value
        synchronized(finalLiveData) {
            if (firstEmitted && secondEmitted && thirdEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!, thirdValue!!)
            }
        }
    }
    return finalLiveData
}

/**
 * Combines the latest values from collection of LiveData objects.
 * First emit is triggered after all of the LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T, Z> combineLatestCompat(vararg sources: LiveData<out T>, combineFunction: (Array<Any?>) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    val emitted = Array(sources.size) { false }
    val values = arrayOfNulls<Any?>(sources.size)

    sources.forEachIndexed { index, source ->
        finalLiveData.addSource(source) { value ->
            emitted[index] = true
            values[index] = value

            synchronized(finalLiveData) {
                val allEmitted = emitted.count { it } == emitted.size

                if (allEmitted) {
                    finalLiveData.value = combineFunction(values)
                }
            }
        }
    }

    return finalLiveData
}

/**
 * Combines the latest values from collection of LiveData objects.
 * First emit is triggered after all of the LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
inline fun <reified T, Z> combineLatest(vararg sources: LiveData<out T>, crossinline combineFunction: (Array<T?>) -> Z): LiveData<Z> {
    return combineLatestCompat(*sources, combineFunction = { latestResult ->
        val mappedResult = Array(latestResult.size) { index ->
            latestResult[index] as? T?
        }

        combineFunction(mappedResult)
    })
}

/**
 * Converts the LiveData to `SingleLiveData` and concats it with the `otherLiveData` and emits their
 * values one by one
 */
fun <T> LiveData<T>.then(otherLiveData:LiveData<T>):LiveData<T>{
    return if (this is SingleLiveData){
        when (otherLiveData) {
            is SingleLiveData -> SingleLiveDataConcat(this,otherLiveData)
            else -> SingleLiveDataConcat(this,otherLiveData.toSingleLiveData())
        }
    }else{
        when (otherLiveData) {
            is SingleLiveData -> SingleLiveDataConcat(this.toSingleLiveData(),otherLiveData)
            else -> SingleLiveDataConcat(this.toSingleLiveData(),otherLiveData.toSingleLiveData())
        }
    }
}
fun <T> LiveData<T>.concatWith(otherLiveData:LiveData<T>) = then(otherLiveData)

/**
 * Concats the given LiveData together and emits their values one by one in order
 */
fun <T> concat(vararg liveData:LiveData<T>):LiveData<T>{
    val liveDataList = mutableListOf<SingleLiveData<T>>()
    liveData.forEach {
        if( it is SingleLiveData<T>)
            liveDataList.add(it)
        else
            liveDataList.add(it.toSingleLiveData())
    }
    return SingleLiveDataConcat(liveDataList)
}

/**
 * Samples the current live data with other live data, resulting in a live data that emits the last
 * value emitted by the original live data (if there were any values emitted) whenever the other live
 * data emits
 */
fun <T> LiveData<T>.sampleWith(other: LiveData<*>): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    val hasValueToConsume = AtomicBoolean(false)
    var latestValue: T? = null
    finalLiveData.addSource(this) {
        hasValueToConsume.set(true)
        latestValue = it
    }
    finalLiveData.addSource(other) {
        if (hasValueToConsume.compareAndSet(true, false)) {
            finalLiveData.value = latestValue
        }
    }
    return finalLiveData
}