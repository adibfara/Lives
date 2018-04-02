import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations

/**
 * Created by Adib Faramarzi
 */

fun <T,O> LiveData<T>.map(  function : (T)->O):LiveData<O>{
    return Transformations.map(this,function)
}

fun <T,O> LiveData<T>.switchMap(function : (T)->LiveData<O>):LiveData<O>{
    return Transformations.switchMap(this,function)
}

fun<T> mutableLiveDataOf(initialValue:T? = null):MutableLiveData<T>{
    val returnedLiveData = MutableLiveData<T>()
    if(initialValue!=null)
        returnedLiveData.value = initialValue
    return returnedLiveData
}


fun rangeOf(initialValue: Int, count:Int):LiveData<Int>{
    val returnedLiveData = MutableLiveData<Int>()
    initialValue.rangeTo(count).forEach { returnedLiveData.value = it }
    return returnedLiveData
}

fun <T> LiveData<T>.nonNull():LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        if(it!=null)
            mutableLiveData.value = it
    })
    return mutableLiveData
}

fun <T> LiveData<T>.distinct():LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    val dispatchedValues = mutableListOf<T?>()
    mutableLiveData.addSource(this, {
        if(!dispatchedValues.contains(it)) {
            mutableLiveData.value = it
            dispatchedValues.add(it)
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.distinctUntilChanged():LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var latestValue : T? = null
    mutableLiveData.addSource(this, {
        if(latestValue!=it) {
            mutableLiveData.value = it
            latestValue = it
        }
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.filter(crossinline predicate : (T?)->Boolean):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, {
        if(predicate(it))
            mutableLiveData.value = it
    })
    return mutableLiveData
}

fun <T> LiveData<T>.mergeWith(vararg liveDatas : LiveData<T>):LiveData<T>{
    val mergeWithArray = mutableListOf<LiveData<T>>()
    mergeWithArray.addAll(liveDatas)
    mergeWithArray.add(this)
    return merge(mergeWithArray)
}



fun <T> merge(liveDataList : List<LiveData<T>>):LiveData<T>{
    val finalLiveData:MediatorLiveData<T> = MediatorLiveData()
    liveDataList.forEach {
        liveData->
        finalLiveData.addSource(liveData, {
            source->
            finalLiveData.value = source
        })
    }
    return finalLiveData
}

fun <T> LiveData<T>.first():LiveData<T>{
    return take(1)
}

fun <T> LiveData<T>.firstOrDefault(default:T):LiveData<T>{
    return take(1).map { it ?: default }
}

fun <T> LiveData<T>.take(count:Int):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var takenCount = 0
    mutableLiveData.addSource(this, {
        if(takenCount<count) {
            mutableLiveData.value = it
            takenCount++
        }
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.takeUntil(crossinline predicate : (T?)->Boolean):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = false
    mutableLiveData.addSource(this, {
        if(predicate(it)) metPredicate = true
        if(!metPredicate) {
            mutableLiveData.value = it
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.skip(count:Int):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var skippedCount = 0
    mutableLiveData.addSource(this, {
        if(skippedCount>=count) {
            mutableLiveData.value = it
        }
        skippedCount++
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.skipUntil(crossinline predicate : (T?)->Boolean):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var metPredicate = false
    mutableLiveData.addSource(this, {
        if(metPredicate || predicate(it)) {
            metPredicate=true
            mutableLiveData.value = it
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.elementAt(index:Int):LiveData<T>{
    val mutableLiveData:MediatorLiveData<T> = MediatorLiveData()
    var currentIndex = 0
    mutableLiveData.addSource(this, {
        if(currentIndex==index) {
            mutableLiveData.value = it
        }
        currentIndex++
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.doOnNext(crossinline onNext : (T?)->Unit):LiveData<T>{
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
