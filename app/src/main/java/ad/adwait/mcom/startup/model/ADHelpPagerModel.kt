package ad.adwait.mcom.startup.model

import ad.adwait.mcom.utils.ADBaseModel

class ADHelpPagerModel : ADBaseModel() {

    private var image_drawable: Int = 0
    private var header: String = ""
    private var textData: String = ""

    fun getHeader(): String {
        return header
    }

    fun setHeader(header: String) {
        this.header = header
    }

    fun getTextData(): String {
        return textData
    }

    fun setTextData(textData: String) {
        this.textData = textData
    }

    fun getImage_drawables(): Int {
        return image_drawable
    }

    fun setImage_drawables(image_drawable: Int) {
        this.image_drawable = image_drawable
    }
}