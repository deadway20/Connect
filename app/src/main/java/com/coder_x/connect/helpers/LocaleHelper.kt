package com.coder_x.connect.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {
    private lateinit var prefsHelper: SharedPrefsHelper

    fun setAppLocale(context: Context, languageCode: String): Context {
        prefsHelper = SharedPrefsHelper(context)
        prefsHelper.setLanguage(languageCode)
        return updateResources(context, languageCode)
    }

    fun getLocalizedContext(context: Context): Context {
        prefsHelper = SharedPrefsHelper(context)
        val language = prefsHelper.getLanguage()
        return updateResources(context, language)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            val localeList = LocaleList(locale)
            configuration.setLocales(localeList)
            return context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }

    fun getPersistedLanguage(context: Context): String {
        prefsHelper = SharedPrefsHelper(context)
        return prefsHelper.getLanguage()
    }

    fun getDisplayLanguageName(languageCode: String): String {
        val locale = Locale(languageCode)
        return locale.getDisplayName(locale).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    fun getSupportedLanguages(): List<String> = listOf("en", "ar")
}