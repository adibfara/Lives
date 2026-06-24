    /**
     * Created by Adib Faramarzi
     */

    @file:JvmName("Lives")
    @file:JvmMultifileClass

    package com.snakydesign.livedataextensions

    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MediatorLiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.Transformations
    import com.snakydesign.livedataextensions.livedata.SingleLiveData
    import java.util.concurrent.atomic.AtomicBoolean

    /**
     * Maps any values that were emitted by the LiveData to the given function
     */
    fun <T, O> LiveData<T>.map(function: MapperFunction<T, O>): LiveData<O> {
        return Transformations.map(this, function)
    }

    /**
     * Maps any values that were emitted by the LiveData to the given function that produces another LiveData
     */
    fun <T, O> LiveData<T>.switchMap(function: MapperFunction<T, LiveData<O>>): LiveData<O> {
        return Transformations.switchMap(this, function)
    }

    /**
     * Does the `onNext` function before everything actually emitting the item to the observers
     */
    fun <T> LiveData<T>.doBeforeNext(onNext: OnNextAction<T>): MutableLiveData<T> {
        val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
        mutableLiveData.addSource(this) {
            onNext(it)
            mutableLiveData.value = it
        }
        return mutableLiveData
    }

    /**
     * Does the `onNext` function after emitting the item to the observers
     */
    fun <T> LiveData<T>.doAfterNext(onNext: OnNextAction<T>): MutableLiveData<T> {
        val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
        mutableLiveData.addSource(this) {
            mutableLiveData.value = it
            onNext(it)
        }
        return mutableLiveData
    }

    /**
     * Buffers the items emitted by the LiveData, and emits them when they reach the `count` as a List.
     */
    fun <T> LiveData<T>.buffer(count: Int): MutableLiveData<List<T?>> {
        val mutableLiveData: MediatorLiveData<List<T?>> = MediatorLiveData()
        val latestBuffer = mutableListOf<T?>()
        mutableLiveData.addSource(this) { value ->
            latestBuffer.add(value)
            if (latestBuffer.size == count) {
                mutableLiveData.value = latestBuffer.toList()
                latestBuffer.clear()
            }

        }
        return mutableLiveData
    }

    /**
     * Returns a LiveData that applies a specified accumulator function to each item that is emitted
     * after the first item has been emitted.
     * Note: The LiveData should not emit nulls. Add .nonNull() to your LiveData if you want to ensure this.
     *
     * @param accumulator the function that is applied to each item
     */
    fun <T> LiveData<T>.scan(accumulator: (accumulatedValue: T, currentValue: T) -> T): MutableLiveData<T> {
        var accumulatedValue: T? = null
        val hasEmittedFirst = AtomicBoolean(false)
        return MediatorLiveData<T>().apply {
            addSource(this@scan) { emittedValue ->
                if (hasEmittedFirst.compareAndSet(false, true)) {
                    accumulatedValue = emittedValue
                } else {
                    accumulatedValue = accumulator(accumulatedValue!!, emittedValue!!)
                    value = accumulatedValue
                }
            }
        }
    }

    /**
     * Returns a LiveData that applies a specified accumulator function to the first item emitted by a source LiveData,
     * then feeds the result of that function along with the second item emitted by the source LiveData into the same function,
     * and so on, emitting the result of each of these iterations.
     * Note: Your LiveData should not emit nulls. Add .nonNull() to your LiveData if you want to ensure this.
     *
     * @param initialSeed the initial value of the accumulator
     * @param accumulator the function that is applied to each item
     */
    fun <T, R> LiveData<T>.scan(initialSeed: R, accumulator: (accumulated: R, currentValue: T) -> R): MutableLiveData<R> {
        var accumulatedValue = initialSeed
        return MediatorLiveData<R>().apply {
            value = initialSeed
            addSource(this@scan) { emittedValue ->
                accumulatedValue = accumulator(accumulatedValue, emittedValue!!)
                value = accumulatedValue
            }
        }
    }

    /**
     * Emits the items of the first LiveData that emits the item. Items of other LiveDatas will never be emitted and are not considered.
     */
    fun <T> amb(vararg inputLiveData: LiveData<T>, considerNulls: Boolean = true): MutableLiveData<T> {
        val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()

        var activeLiveDataIndex = inputLiveData.indexOfFirst { it.value != null }
        if (activeLiveDataIndex >= 0) {
            mutableLiveData.value = inputLiveData[activeLiveDataIndex].value
        }
        inputLiveData.forEachIndexed { index, liveData ->
            mutableLiveData.addSource(liveData) { value ->
                if (considerNulls || value != null) {
                    activeLiveDataIndex = index
                    inputLiveData.forEachIndexed { index, liveData ->
                        if (index != activeLiveDataIndex) {
                            mutableLiveData.removeSource(liveData)
                        }
                    }

                    if (index == activeLiveDataIndex) {
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
    fun <T> LiveData<T>.toSingleLiveData(): SingleLiveData<T> = first()

    /**
     * Converts a LiveData to a MutableLiveData with the initial value set by this LiveData's value
     */
    fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
        val liveData = MutableLiveData<T>()
        liveData.value = this.value
        return liveData
    }

    /**
     *  Emits all the items in the buffer each time
     */
    fun <T> LiveData<T>.replay(): MutableLiveData<MutableList<T?>> {
        val mutableLiveData = MediatorLiveData<MutableList<T?>>()
        val buffer = mutableListOf<T?>()

        mutableLiveData.addSource(this) { value ->
            buffer.add(value)
            mutableLiveData.value = buffer
        }

        return mutableLiveData
    }

    
    /**
     * Mapper function used in the operators that need mapping
     */
    typealias MapperFunction<T, O> = (T) -> O

    /**
     * Mapper function used in the operators that need mapping
     */
    typealias OnNextAction<T> = (T?) -> Unit