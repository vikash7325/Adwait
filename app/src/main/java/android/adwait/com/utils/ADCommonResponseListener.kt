package android.adwait.com.utils

interface ADCommonResponseListener {

    abstract fun onSuccess(data:Any?)
    abstract fun onError(data:Any?)
}