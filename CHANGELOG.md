## [1.2.0]
### Added
- `LiveData.sampleWith(otherLiveData)`: Samples the current live data with other live data, resulting in a live data that emits the last value emitted by the original live data (if any) whenever the other live data emits
- `scan(accumulator)` : Applies the accumulator function to each emitted item, starting with the second emitted item. Initial value of the accumulator is the first item.
- `scan(seed, accumulator)` : Applies the accumulator function to each emitted item, starting with the initial seed.
- `combineLatest(firstLiveData, secondLiveData, combineFunction)`: combines both of the LiveDatas using the combineFunction and emits a value after any of them have emitted a value.

### Changed
- change `api` to `implementation` when importing the architecture components libraries to make them compile-only
