/**
 * Created by Adib Faramarzi
 */

@file:JvmName("Lives")
@file:JvmMultifileClass

package com.snakydesign.livedataextensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
 * after that, emits values whenever both of them emit another value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y> zip(first: LiveData<X>, second: LiveData<Y>): LiveData<Pair<X?, Y?>> {
    return zip(first, second) { x, y -> Pair(x, y) }
}

/**
 * zips both of the LiveData and emits a value after both of them have emitted their values,
 * after that, emits values whenever both of them emit another value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, R> zip(first: LiveData<X>, second: LiveData<Y>, zipFunction: (X?, Y?) -> R): LiveData<R> {
    val finalLiveData: MediatorLiveData<R> = MediatorLiveData()

    val firstEmit: Emit<X?> = Emit()
    val secondEmit: Emit<Y?> = Emit()

    val combine: () -> Unit = {
        if (firstEmit.emitted && secondEmit.emitted) {
            val combined = zipFunction(firstEmit.value, secondEmit.value)
            firstEmit.reset()
            secondEmit.reset()
            finalLiveData.value = combined
        }
    }

    finalLiveData.addSource(first) { value ->
        firstEmit.value = value
        combine()
    }
    finalLiveData.addSource(second) { value ->
        secondEmit.value = value
        combine()
    }
    return finalLiveData
}

/**
 * zips three LiveData and emits a value after all of them have emitted their values,
 * after that, emits values whenever all of them emit another value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, Z, R> zip(
    first: LiveData<X>,
    second: LiveData<Y>,
    third: LiveData<Z>,
    zipFunction: (X?, Y?, Z?) -> R
): LiveData<R> {
    val finalLiveData: MediatorLiveData<R> = MediatorLiveData()

    val firstEmit: Emit<X?> = Emit()
    val secondEmit: Emit<Y?> = Emit()
    val thirdEmit: Emit<Z?> = Emit()

    val combine: () -> Unit = {
        if (firstEmit.emitted && secondEmit.emitted && thirdEmit.emitted) {
            val combined = zipFunction(firstEmit.value, secondEmit.value, thirdEmit.value)
            firstEmit.reset()
            secondEmit.reset()
            thirdEmit.reset()
            finalLiveData.value = combined
        }
    }

    finalLiveData.addSource(first) { value ->
        firstEmit.value = value
        combine()
    }
    finalLiveData.addSource(second) { value ->
        secondEmit.value = value
        combine()
    }
    finalLiveData.addSource(third) { value ->
        thirdEmit.value = value
        combine()
    }
    return finalLiveData
}

/**
 * zips three LiveData and emits a value after all of them have emitted their values,
 * after that, emits values whenever all of them emit another value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, Z> zip(first: LiveData<X>, second: LiveData<Y>, third: LiveData<Z>): LiveData<Triple<X?, Y?, Z?>> {
    return zip(first, second, third) { x, y, z -> Triple(x, y, z) }
}

/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, R> combineLatest(first: LiveData<X>, second: LiveData<Y>, combineFunction: (X?, Y?) -> R): LiveData<R> {
    val finalLiveData: MediatorLiveData<R> = MediatorLiveData()

    val firstEmit: Emit<X?> = Emit()
    val secondEmit: Emit<Y?> = Emit()

    val combine: () -> Unit = {
        if (firstEmit.emitted && secondEmit.emitted) {
            val combined = combineFunction(firstEmit.value, secondEmit.value)
            finalLiveData.value = combined
        }
    }

    finalLiveData.addSource(first) { value ->
        firstEmit.value = value
        combine()
    }
    finalLiveData.addSource(second) { value ->
        secondEmit.value = value
        combine()
    }
    return finalLiveData
}

/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y> combineLatest(first: LiveData<X>, second: LiveData<Y>): LiveData<Pair<X?, Y?>> =
    combineLatest(first, second) { x, y -> Pair(x, y) }

/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, Z, R> combineLatest(
    first: LiveData<X>,
    second: LiveData<Y>,
    third: LiveData<Z>,
    combineFunction: (X?, Y?, Z?) -> R
): LiveData<R> {
    val finalLiveData: MediatorLiveData<R> = MediatorLiveData()

    val firstEmit: Emit<X?> = Emit()
    val secondEmit: Emit<Y?> = Emit()
    val thirdEmit: Emit<Z?> = Emit()

    val combine: () -> Unit = {
        if (firstEmit.emitted && secondEmit.emitted && thirdEmit.emitted) {
            val combined = combineFunction(firstEmit.value, secondEmit.value, thirdEmit.value)
            finalLiveData.value = combined
        }
    }

    finalLiveData.addSource(first) { value ->
        firstEmit.value = value
        combine()
    }
    finalLiveData.addSource(second) { value ->
        secondEmit.value = value
        combine()
    }
    finalLiveData.addSource(third) { value ->
        thirdEmit.value = value
        combine()
    }
    return finalLiveData
}

/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <X, Y, Z> combineLatest(first: LiveData<X>, second: LiveData<Y>, third: LiveData<Z>): LiveData<Triple<X?, Y?, Z?>> =
    combineLatest(first, second, third) { x, y, z -> Triple(x, y, z) }

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

/**
 * Wrapper that wraps an emitted value.
 */
private class Emit<T> {

    internal var emitted: Boolean = false

    internal var value: T? = null
        set(value) {
            field = value
            emitted = true
        }

    fun reset() {
        value = null
        emitted = false
    }
}