Lives - Android LiveData Extensions for Kotlin and Java
-------------------------------------------------------
[![Build Status](https://travis-ci.org/adibfara/Lives.svg?branch=master)](https://travis-ci.org/adibfara/Lives)[![Latest Version](https://img.shields.io/bintray/v/adibfara/lives/lives.svg?label=version)](https://github.com/adibfara/Lives)

Add RxJava-like operators to your LiveData objects with ease, usable in Kotlin (as extension functions) and Java (as static functions to a class called Lives)

Download
--------
Add the dependencies to your project:

```groovy
implementation 'android.arch.lifecycle:extensions:x.x.x'
implementation 'com.snakydesign.livedataextensions:lives:1.2.1'
```

If you want to use this library on a Java project, add the following dependency:
```groovy
implementation 'org.jetbrains.kotlin:kotlin-stdlib:x.x.x'
```

Usage
--------

#### Kotlin
**Import the functions**
```kotlin
    import com.snakydesign.livedataextensions.*
```
**Creating LiveData**

- `liveDataOf` : Create a LiveData object from a value (like `just` in RxJava, although it immediately emits the value)
```kotlin
    val liveData = liveDataOf(2) //liveData will produce 2 (as Int) when observed
```

- `from` : Creates a LiveData that emits the value that the `callable` function produces, and immediately emits it.
```kotlin
    val liveData = liveDataOf {computePI()}
```

- `emptyLiveData` : Creates an empty LiveData
```kotlin
    val liveData = emptyLiveData<Int>()
```


**Filtering**

- `distinct` : Emits the items that are different from all the values that have been emitted so far
```kotlin
    val originalLiveData = MutableLiveData<Int>()
    val newLiveData = originalLiveData.distinct()
    originalLiveData.value = 2
    originalLiveData.value = 2 // newLiveData will not produce this
    originalLiveData.value = 3 // newLiveData will produce
    originalLiveData.value = 2 // newLiveData will not produce this
```

- `distinctUntilChanged` : Emits the items that are different from the last item
```kotlin
    val originalLiveData = MutableLiveData<Int>()
    val newLiveData = originalLiveData.distinctUntilChanged()
    originalLiveData.value = 2
    originalLiveData.value = 2 // newLiveData will not produce this
    originalLiveData.value = 3 // newLiveData will produce
    originalLiveData.value = 2 // newLiveData will produce
```

- `filter` :Emits the items that pass through the predicate
```kotlin
    val originalLiveData = MutableLiveData<Int>()
    val newLiveData = originalLiveData.filter { it > 2 }
    originalLiveData.value = 3 // newLiveData will produce
    originalLiveData.value = 2 // newLiveData will not produce this
```

- `first()` : produces a SingleLiveData that produces only one Item.
- `take(n:Int)` : produces a LiveData that produces only the first n Items.
- `takeUntil(predicate)` : Takes until a certain predicate is met, and does not emit anything after that, whatever the value.
- `skip(n)` : Skips the first n values.
- `skipUntil(predicate)` : Skips all values until a certain predicate is met (the item that actives the predicate is also emitted).
- `elementAt(index)` : emits the item that was emitted at `index` position
- `nonNull()` : Will never emit the nulls to the observers (outputs a `NonNullLiveData`).
- `defaultIfNull(value)`: Will produce the `value` when `null` is recieved.

**Combining**

- `merge(List<LiveData>)` : Merges multiple LiveData, and emits any item that was emitted by any of them
- `LiveData.merge(LiveData)` : Merges this LiveData with another one, and emits any item that was emitted by any of them
- `concat(LiveData...)` : Concats multiple LiveData objects (and converts them to `SingleLiveData` if necessary, and emits their first item in order. (Please check the note below.)
- `LiveData.then(LiveData)` : Concats the first LiveData with the given one. (Please check the note below.)
- `LiveData.merge(LiveData)` : Merges this LiveData with another one, and emits any item that was emitted by any of them
- `startWith(startingValue)`: Emits the `startingValue` before any other value.
- `zip(firstLiveData, secondLiveData, zipFunction)`: zips both of the LiveDatas using the zipFunction and emits a value after both of them have emitted their values, after that, emits values whenever any of them emits a value.
- `combineLatest(firstLiveData, secondLiveData, combineFunction)`: combines both of the LiveDatas using the combineFunction and emits a value after any of them have emitted a value.
- `LiveData.sampleWith(otherLiveData)`: Samples the current live data with other live data, resulting in a live data that emits the last value emitted by the original live data (if any) whenever the other live data emits

**Transforming**

- `map(mapperFunction)` : Map each value emitted to another value (and type) with the given function
- `switchMap(mapperFunction)` : Maps any values that were emitted by the LiveData to the given function that produces another LiveData
- `doBeforeNext(OnNextAction)` : Does the `onNext` function before everything actually emitting the item to the observers
- `doAfterNext(OnNextAction)` : Does the `onNext` function after emitting the item to the observers(function) : Does the `onNext` function before everything actually emitting the item to the observers
- `buffer(count)` : Buffers the items emitted by the LiveData, and emits them when they reach the `count` as a List.
- `scan(accumulator)` : Applies the accumulator function to each emitted item, starting with the second emitted item. Initial value of the accumulator is the first item.
- `scan(seed, accumulator)` : Applies the accumulator function to each emitted item, starting with the initial seed.
- `amb(LiveData...)` : Emits the items of the first LiveData that emits the item. Items of other LiveDatas will never be emitted and are not considered.
- `toMutableLiveData()` : Converts a LiveData to a MutableLiveData with the initial value set by this LiveData's value

#### Java

You can call any function prefixed with `Lives` keyword.

    import com.snakydesign.livedataextensions.Lives;

 - Example (create a LiveData with the initial value of 2 and map each value to its String type
    ```kotlin
    LiveData<String> liveData = Lives.map(Lives.just(2), new Function1<Integer, String>() {
                @Override
                public Integer invoke(Integer integer) {
                    return String.valueOf(integer);
                }
            }) ;
    ```

#### Notes

Please note that because of design of `LiveData`, after a value is emitted to an observer, and then another value is emitted, the old value is destroyed in any LiveData object. So unlike RxJava, if a new Observer is attached, It will only receive the most recent value.

So If you want to use operators like `concat`, you have to consider allowing only one observer to the LiveData.

PRs are more than welcome, and please file an issue If you encounter something 🍻.

You can also ping me on twitter [@TheSNAKY](http://twitter.com/TheSNAKY).


License
=======

    Copyright 2018 Adib Faramarzi.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
