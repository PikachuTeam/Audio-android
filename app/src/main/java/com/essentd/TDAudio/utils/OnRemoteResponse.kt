package com.essentd.TDAudio.utils

/**
 * Created by dongc on 9/1/2017.
 */
interface OnRemoteResponse<in E> {
    fun onSuccess(data: E)

    fun onError(throwable: Throwable)
}