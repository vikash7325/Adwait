package ad.adwait.mcom.utils

interface ADCommonResponseListener {

    abstract fun onSuccess(data:Any?)
    abstract fun onError(data:Any?)
}