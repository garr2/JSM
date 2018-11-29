package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.os.Parcelable;

public interface SaveFragmentState<T> {

    Parcelable saveState();

    void savedState(Parcelable parcelable);

    T saveOtherArgs();

    void savedOtherArgs(T args);
}
