package com.snakydesign.livedataextensions;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

/**
 * @param <T> The type of the parameter
 * @author Adib Faramarzi (adibfara@gmail.com)
 * <p>
 * A simple callback that can receive from {@link LiveData}.
 * @see LiveData LiveData - for a usage description.
 */
public interface NonNullObserver<T> {
    /**
     * Called when the data is changed.
     *
     * @param t The new non null data
     */
    void onChanged(@NonNull T t);
}
