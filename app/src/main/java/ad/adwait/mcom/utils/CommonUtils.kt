package and.com.polam.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

class CommonUtils {

    companion object {

        public fun getHtmlText(msg: String): Spanned {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT)
            } else {
                return Html.fromHtml(msg)
            }
        }

        public fun checkForEmpty(text:String):String{
            var str = text

            if (str.isEmpty() || str.toLowerCase().equals("null")){
                str = ""
            }

            return str
        }
    }
}