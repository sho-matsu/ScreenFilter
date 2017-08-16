package jp.shoma.screenfilter.model

class ColorList {

    companion object {
        fun get() : ArrayList<ColorPattern> {
            return ArrayList<ColorPattern>().apply {
                add((ColorPattern("#000000", "黒")))
                add((ColorPattern("#FFFFFF", "白")))
                add((ColorPattern("#FF0000", "赤")))
                add((ColorPattern("#FFFF00", "黃")))
                add((ColorPattern("#0000FF", "青")))
                add((ColorPattern("#008000", "緑")))
                add((ColorPattern("#FF00FF", "マゼンタ")))
            }
        }
        class ColorPattern(val colorCode : String, val colorName : String)
    }
}