package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Typeface


class Fonts private constructor(context: Context) {
    val crackman = Typeface.createFromAsset(context.assets, "fonts/crackman.ttf")
    val neuropol = Typeface.createFromAsset(context.assets, "fonts/neuropol_x_rg.ttf")
    val quicksand = Typeface.createFromAsset(context.assets, "fonts/quicksand-bold.otf")

    companion object : SingletonHolder<Fonts, Context>(::Fonts)
}