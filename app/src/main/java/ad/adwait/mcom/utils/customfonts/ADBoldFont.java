package ad.adwait.mcom.utils.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class ADBoldFont extends androidx.appcompat.widget.AppCompatTextView {


    public ADBoldFont(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public ADBoldFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public ADBoldFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = ADFontCache.getTypeface("fonts/OpenSans-Bold.ttf", context);
        setTypeface(customFont);
    }
}
