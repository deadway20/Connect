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
        private const val MY_THEME = "SetTheme"

    }

    // 🔹 حفظ عنوان السيرفر
    fun putServerAddress(address: String) {
        sharedPreferences.edit() { putString(SERVER_ADDRESS_KEY, address) }
    }

    // 🔹 استرجاع عنوان السيرفر
    fun getServerAddress(): String {
        return sharedPreferences.getString(SERVER_ADDRESS_KEY, "") ?: ""
    }

    // 🔹 حفظ منفذ السيرفر
    fun putServerPort(port: String) {
        sharedPreferences.edit() { putString(SERVER_PORT_KEY, port) }
    }

    // 🔹 استرجاع منفذ السيرفر
    fun getServerPort(): String {
        return sharedPreferences.getString(SERVER_PORT_KEY, "") ?: ""
    }

    // 🔹 حفظ ID الموظف
    fun putEmpID(empID: Int) {
        sharedPreferences.edit() { putInt(EMP_ID_KEY, empID) }
    }

    // 🔹 استرجاع ID الموظف
    fun getEmpID(): Int {
        return sharedPreferences.getInt(EMP_ID_KEY, -1)
    }

    // 🔹 حفظ أسم الموظف
    fun putEmpName(name: String) {
        sharedPreferences.edit() { putString(EMP_NAME_KEY, name) }
    }

    // 🔹 إسترجاع أسم الموظف
    fun getEmpName(): String {
        return sharedPreferences.getString(EMP_NAME_KEY, "") ?: ""
    }

    // 🔹 حفظ قسم الموظف
    fun putEmpDepartment(depart: String) {
        sharedPreferences.edit() { putString(EMP_DEPART_KEY, depart) }
    }

    // 🔹 إسترجاع أسم الموظف
    fun getEmpDepartment(): String {
        return sharedPreferences.getString(EMP_DEPART_KEY, "") ?: ""
    }

    // 🔹 حفظ موبيل الموظف
    fun putEmpMobile(mobile: String) {
        sharedPreferences.edit() { putString(EMP_MOBILE_KEY, mobile) }
    }

    // 🔹 إسترجاع موبيل الموظف
    @Suppress("unused", "unused", "unused", "unused", "unused", "unused")
    fun getEmpMobile(): String {
        return sharedPreferences.getString(EMP_MOBILE_KEY, "") ?: ""
    }

    // 🔹 حفظ ساعات العمل للموظف
    fun putEmpHours(hours: Int) {
        sharedPreferences.edit() { putInt(EMP_HOURS_KEY, hours) }
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
            sharedPreferences.edit() { putString(EMP_IMG, encoded) }
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
        sharedPreferences.edit() { clear() }
    }

    //🔹 للتحقق من إتمام الإعدادات
    fun isSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean("isSetupCompleted", false)
    }

    //🔹 لتعيين حالة إتمام الإعدادات
    fun setSetupCompleted(completed: Boolean) {
        sharedPreferences.edit() { putBoolean("isSetupCompleted", completed) }
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
        sharedPreferences.edit() { putString(KEY_EMP_IMAGE_PATH, path) }
    }

    //🔹 دالة لاسترجاع مسار صورة الموظف (مفيدة في المستقبل)
    fun getEmpImagePath(): String? {
        return sharedPreferences.getString(KEY_EMP_IMAGE_PATH, null)
    }

    //🔹 دالة لحفظ لغة التطبيق
    fun setLanguage(language: String) {
        sharedPreferences.edit() { putString("language", language) }
    }

    //🔹 دالة لاسترجاع لغة التطبيق
    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    // 🔹 دالة لحفظ الثيم
    fun setTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit() { putBoolean("theme", isDarkTheme) }
    }

    // 🔹 دالة لاسنرجاع الثيم
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("theme", false)
    }

    fun saveData(data: EmployeeData) {
        sharedPreferences.edit() {
            putString("recordDate", data.recordDate)
            putString("clockIn", data.clockIn)
            putString("clockOut", data.clockOut)
            putString("delayInMinutes", data.delayInMinutes)
            putString("overtimeInMinutes", data.overtimeInMinutes)
            putBoolean("isAttend", data.isAttend)
            putBoolean("isAbsence", data.isAbsence)
        }
    }

    fun getSavedData(): EmployeeData? {
        val recordDate = sharedPreferences.getString("recordDate", null) ?: return null
        val clockIn = sharedPreferences.getString("clockIn", "00:00")!!
        val clockOut = sharedPreferences.getString("clockOut", "00:00")!!
        val totalHours = sharedPreferences.getString("totalHours", "00:00")!!
        val delayInMinutes = sharedPreferences.getString("delayInMinutes", "00:00")!!
        val overtimeInMinutes = sharedPreferences.getString("overtimeInMinutes", "00:00")!!
        val isAttend = sharedPreferences.getBoolean("isAttend", false)
        val isAbsence = sharedPreferences.getBoolean("isAbsence", false)
        val attendCount = sharedPreferences.getInt("attendCount", 0)
        val absenceCount = sharedPreferences.getInt("absenceCount", 0)

        return EmployeeData(
            recordDate,
            clockIn,
            clockOut,
            totalHours,
            overtimeInMinutes,
            delayInMinutes,
            isAttend,
            isAbsence,
            attendCount,
            absenceCount

        )
    }

    fun clearData() {
        sharedPreferences.edit() {
            remove("recordDate")
            remove("clockIn")
            remove("clockOut")
            remove("workHours")
            remove("delayInMinutes")
            remove("overtimeInMinutes")
            remove("isAbsence")
        }
    }

}
