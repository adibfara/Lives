package com.snakydesign.livedataextensions

import android.arch.lifecycle.Observer

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

internal class InternalNonNullObserver<T>(val actualObserver: NonNullObserver<T>) : Observer<T> {
    override fun onChanged(t: T?) {
        t?.let {
            actualObserver.onChanged(t)
        }

    }

}