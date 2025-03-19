@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package com.coder_x.connect

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.core.content.edit

class SharedPrefsHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "AppPrefs"
        private const val SERVER_ADDRESS_KEY = "ServerAddress"
        private const val SERVER_PORT_KEY = "ServerPort"
        private const val EMP_ID_KEY: String = "EmpID"
        private const val EMP_NAME_KEY = "EmpName"
        private const val EMP_DEPART_KEY = "EmpDepart"
        private const val EMP_MOBILE_KEY = "EmpMobile"
        private const val EMP_HOURS_KEY = "EmpHours"
        private const val COMPLETED = "isSetupCompleted"
        private const val USERNAME = "sa"
        private const val PASSWORD = "12345678"
        private const val EMP_IMG = "EmpImg"
        private const val KEY_EMP_IMAGE_PATH = "emp_image_path"
        private const val MY_LANG = "SetLang"

    }

    // 🔹 حفظ عنوان السيرفر
    fun putServerAddress(address: String) {
        sharedPreferences.edit().putString(SERVER_ADDRESS_KEY, address).apply()
    }

    // 🔹 استرجاع عنوان السيرفر
    fun getServerAddress(): String {
        return sharedPreferences.getString(SERVER_ADDRESS_KEY, "") ?: ""
    }

    // 🔹 حفظ منفذ السيرفر
    fun putServerPort(port: String) {
        sharedPreferences.edit().putString(SERVER_PORT_KEY, port).apply()
    }

    // 🔹 استرجاع منفذ السيرفر
    fun getServerPort(): String {
        return sharedPreferences.getString(SERVER_PORT_KEY, "") ?: ""
    }

    // 🔹 حفظ ID الموظف
    fun putEmpID(empID: Int) {
        sharedPreferences.edit().putInt(EMP_ID_KEY, empID).apply()
    }

    // 🔹 استرجاع ID الموظف
    fun getEmpID(): Int {
        return sharedPreferences.getInt(EMP_ID_KEY, -1)
    }

    // 🔹 حفظ أسم الموظف
    fun putEmpName(name: String) {
        sharedPreferences.edit().putString(EMP_NAME_KEY, name).apply()
    }

    // 🔹 إسترجاع أسم الموظف
    fun getEmpName(): String {
        return sharedPreferences.getString(EMP_NAME_KEY, "") ?: ""
    }

    // 🔹 حفظ قسم الموظف
    fun putEmpDepart(depart: String) {
        sharedPreferences.edit().putString(EMP_DEPART_KEY, depart).apply()
    }

    // 🔹 إسترجاع أسم الموظف
    fun getEmpDepart(): String {
        return sharedPreferences.getString(EMP_DEPART_KEY, "") ?: ""
    }

    // 🔹 حفظ موبيل الموظف
    fun putEmpMobile(mobile: String) {
        sharedPreferences.edit().putString(EMP_MOBILE_KEY, mobile).apply()
    }

    // 🔹 إسترجاع موبيل الموظف
    @Suppress("unused", "unused", "unused", "unused", "unused", "unused")
    fun getEmpMobile(): String {
        return sharedPreferences.getString(EMP_MOBILE_KEY, "") ?: ""
    }

    // 🔹 حفظ ساعات العمل للموظف
    fun putEmpHours(hours: Int) {
        sharedPreferences.edit().putInt(EMP_HOURS_KEY, hours).apply()
    }

    // 🔹 إسترجاع  ساعات العمل الموظف
    fun getEmpHours(): Int {
        return sharedPreferences.getInt(EMP_HOURS_KEY, -1)
    }

    //🔹 تخزين الصورة كـ Base64 String
    fun putEmpImg(img: Bitmap) {
        try {
            val byteArrayOutputStream = java.io.ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encoded = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
            sharedPreferences.edit().putString(EMP_IMG, encoded).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔹 لاستعادة الصورة من بعد تخزينها
    fun getEmpImg(): Bitmap? {
        try {
            val encoded = sharedPreferences.getString(EMP_IMG, null)
            if (encoded != null) {
                val decodedBytes = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
                return android.graphics.BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // 🔹 لمسح جميع البيانات المحفوظة
    fun clearPrefs() {
        sharedPreferences.edit().clear().apply()
    }

    //🔹 للتحقق من إتمام الإعدادات
    fun isSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean("isSetupCompleted", false)
    }

    //🔹 لتعيين حالة إتمام الإعدادات
    fun setSetupCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean("isSetupCompleted", completed).apply()
    }

    // 🔹 لاسترجاع حالة أتمام الاعدادات
    fun getSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean(COMPLETED, false)
    }

    // 🔹إسترجاع أسم المستخدم
    fun getUserName(): String {
        return USERNAME
    }

    // 🔹إسترجاع كلمة السر
    fun getPassword(): String {
        return PASSWORD
    }

    //🔹 دالة لحفظ مسار صورة الموظف
    fun putEmpImagePath(path: String) {
        sharedPreferences.edit().putString(KEY_EMP_IMAGE_PATH, path).apply()
    }

    //🔹 دالة لاسترجاع مسار صورة الموظف (مفيدة في المستقبل)
    fun getEmpImagePath(): String? {
        return sharedPreferences.getString(KEY_EMP_IMAGE_PATH, null)
    }

    //🔹 دالة لحفظ لغة التطبيق
    fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    //🔹 دالة لاسترجاع لغة التطبيق
    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }


    fun saveData(data: EmpData) {
        sharedPreferences.edit() {
            putString("recordDate", data.recordDate)
            putString("checkInTime", data.checkInTime)
            putString("checkOutTime", data.checkOutTime)
            putString("delayInMinutes", data.delayInMinutes)
            putString("overtimeInMinutes", data.overtimeInMinutes)
            putBoolean("isAbsent", data.isAbsent)
        }
    }

    fun getSavedData(): EmpData? {
        val recordDate = sharedPreferences.getString("recordDate", null) ?: return null
        val checkInTime = sharedPreferences.getString("checkInTime", "00:00")!!
        val checkOutTime = sharedPreferences.getString("checkOutTime", "00:00")!!
        val workHours = sharedPreferences.getString("workHours", "00:00")!!
        val delayInMinutes = sharedPreferences.getString("delayInMinutes", "00:00")!!
        val overtimeInMinutes = sharedPreferences.getString("overtimeInMinutes", "00:00")!!
        val isAbsent = sharedPreferences.getBoolean("isAbsent", false)

        return EmpData(
            recordDate,
            checkInTime,
            checkOutTime,
            workHours,
            delayInMinutes,
            overtimeInMinutes,
            isAbsent
        )
    }

    fun clearData() {
        sharedPreferences.edit() {
            remove("recordDate")
            remove("checkInTime")
            remove("checkOutTime")
            remove("delayInMinutes")
            remove("overtimeInMinutes")
            remove("isAbsent")
        }
    }

}
