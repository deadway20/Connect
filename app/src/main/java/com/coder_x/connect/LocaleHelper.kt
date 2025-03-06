package com.coder_x.connect

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    // دالة لتطبيق اللغة على السياق
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config: Configuration = resources.configuration

        config.setLocale(locale)
        config.setLayoutDirection(locale) // ضبط الاتجاه تلقائيًا

        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // دالة لتطبيق اللغة المحفوظة
    fun applyLanguage(context: Context) {
        val prefsHelper = SharedPrefsHelper(context)
        val savedLanguage = prefsHelper.getLanguage()
        setLocale(context, savedLanguage)
    }

    // دالة للحصول على السياق المحدث باللغة المحفوظة (مفيدة في attachBaseContext)
    fun getLocalizedContext(baseContext: Context): Context {
        val prefsHelper = SharedPrefsHelper(baseContext)
        val savedLanguage = prefsHelper.getLanguage()

        val locale = Locale(savedLanguage)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return baseContext.createConfigurationContext(config)
    }
}