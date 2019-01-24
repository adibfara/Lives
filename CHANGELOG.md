## [1.2.1]
### Changed
- change the name of `just` to `liveDataOf`, since just was too broad.
- change the name of `empty()` to `emptyLiveData()`, since empty was too broad.

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
