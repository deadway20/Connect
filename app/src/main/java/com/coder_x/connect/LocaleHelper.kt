package com.coder_x.connect

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale) // ضبط الاتجاه تلقائيًا بناءً على اللغة المختارة

        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
