package com.garr.pavelbobrovko.notsimplechat.deprecated;

/**
 * Created by garr on 21.02.2018.
 */

public interface OnDBReadCompleteListener<T> {
    void onComplete(T type);
    void onFailtrue(Exception e);
}
