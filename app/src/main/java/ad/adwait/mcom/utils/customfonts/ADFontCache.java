package ad.adwait.mcom.utils.customfonts;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class ADFontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<String, Typeface>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
}
