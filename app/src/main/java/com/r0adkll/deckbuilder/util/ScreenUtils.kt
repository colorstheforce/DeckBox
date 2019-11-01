package com.r0adkll.deckbuilder.util

import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.view.View

object ScreenUtils {

    fun smallestWidth(resources: Resources, widthInDp: Int): Boolean {
        return resources.configuration.smallestScreenWidthDp >= widthInDp
    }

    fun smallestWidth(resources: Resources, config: Config): Boolean = smallestWidth(resources, config.widthInDp)

    fun orientation(resources: Resources, orientation: Int): Boolean {
        return resources.configuration.orientation == orientation
    }

    fun View.smallestWidth(config: Config): Boolean = smallestWidth(this.resources, config)
    fun Context.smallestWidth(config: Config): Boolean = smallestWidth(this.resources, config)
    fun androidx.fragment.app.Fragment.smallestWidth(config: Config): Boolean = smallestWidth(this.resources, config)

    fun View.orientation(orientation: Int): Boolean = orientation(this.resources, orientation)
    fun Context.orientation(orientation: Int): Boolean = orientation(this.resources, orientation)
    fun androidx.fragment.app.Fragment.orientation(orientation: Int): Boolean = orientation(this.resources, orientation)

    enum class Config(val widthInDp: Int) {
        PHONE(320),
        PHABLET(480),
        TABLET_7IN(600),
        TABLET_10(720)
    }
}
