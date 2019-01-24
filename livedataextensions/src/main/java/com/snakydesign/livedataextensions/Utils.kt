package com.snakydesign.livedataextensions

import android.arch.lifecycle.*

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

/**
 * Adds the given observer to the observers list within the lifespan of the given
 * owner. The events are dispatched on the main thread. If LiveData already has data
 * set, it will be delivered to the observer.
 * <p>
 * The observer will only receive events if the owner is in {@link Lifecycle.State#STARTED}
 * or {@link Lifecycle.State#RESUMED} state (active).
 * <p>
 * If the owner moves to the {@link Lifecycle.State#DESTROYED} state, the observer will
 * automatically be removed.
 * <p>
 * When data changes while the {@code owner} is not active, it will not receive any updates.
 * If it becomes active again, it will receive the last available data automatically.
 * <p>
 * LiveData keeps a strong reference to the observer and the owner as long as the
 * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
 * the observer &amp; the owner.
 * <p>
 * If the given owner is already in {@link Lifecycle.State#DESTROYED} state, LiveData
 * ignores the call.
 * <p>
 * If the given owner, observer tuple is already in the list, the call is ignored.
 * If the observer is already in the list with another owner, LiveData throws an
 * {@link IllegalArgumentException}.
 *
 * Note: this observer will drop any null values
 *
 * @param owner    The LifecycleOwner which controls the observer
 * @param observer The observer that will receive the events
 */
fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: NonNullObserver<T>) {
    this.observe(owner, InternalNonNullObserver(observer))
}

/**
 * Adds the given observer to the observers list. This call is similar to
 * {@link LiveData#observe(LifecycleOwner, Observer)} with a LifecycleOwner, which
 * is always active. This means that the given observer will receive all events and will never
 * be automatically removed. You should manually call {@link #removeObserver(Observer)} to stop
 * observing this LiveData.
 * While LiveData has one of such observers, it will be considered
 * as active.
 * <p>
 * If the observer was already added with an owner to this LiveData, LiveData throws an
 * {@link IllegalArgumentException}.
 *
 * Note: this observer will drop any null values
 *
 * @param observer The observer that will receive the events
 */
fun <T> LiveData<T>.observeNonNullForever(observer: NonNullObserver<T>) {
    this.observeForever(InternalNonNullObserver(observer))
}

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: Observer<T>) {
    liveData.observe(this, observer)
}

fun <T> LifecycleOwner.observeNonNull(liveData: LiveData<T>, observer: NonNullObserver<T>) {
    liveData.observeNonNull(this, observer)
}