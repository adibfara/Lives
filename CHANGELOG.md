## [1.4.0]
### Changed
- [_BREAKING_] `map` and `switchMap` operators are now using the implementation of [Android LiveData KTX](https://developer.android.com/kotlin/ktx#livedata) .
- [_BREAKING_] NonNullLiveData class has been removed. .nonNull() is now returning a regular live data.

### Added


## [1.3.0]
### Changed
- [_BREAKING_] Update the dependencies to AndroidX
- Removed synchronized blocks, since all LiveData operation is already handled on the main thread
- [_BREAKING_] change the name of `just(value)` to `liveDataOf(value)`, since just was too broad.
- [_BREAKING_] change the name of `from { }` to `liveDataOf { }`, since just was too broad.
- [_BREAKING_] change the name of `empty()` to `emptyLiveData()`, since empty was too broad.

## [1.2.0]
### Added
- `LiveData.sampleWith(otherLiveData)`: Samples the current live data with other live data, resulting in a live data that emits the last value emitted by the original live data (if any) whenever the other live data emits
- `scan(accumulator)` : Applies the accumulator function to each emitted item, starting with the second emitted item. Initial value of the accumulator is the first item.
- `scan(seed, accumulator)` : Applies the accumulator function to each emitted item, starting with the initial seed.
- `combineLatest(firstLiveData, secondLiveData, combineFunction)`: combines both of the LiveDatas using the combineFunction and emits a value after any of them have emitted a value.

### Changed
- change `api` to `implementation` when importing the architecture components libraries to make them compile-only

## [1.0.1]
### Added
- Add support for android v15.
- Add the operator empty<T>() to easily create MutableLiveData objects.
