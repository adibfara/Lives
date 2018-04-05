package com.snakydesign.livedataextensions;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Adib Faramarzi
 * Used in doBeforeNext and doAfterNext operators
 */
public interface OnNextAction<T> {
    void performAction(@Nullable T value);
}
