@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package com.coder_x.connect

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import java.io.ByteArrayOutputStream

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
        private const val EMP_IMG_URI_KEY = "employee_image_uri"
        private const val EMP_IMG_BASE64_KEY = "employee_image_base64"
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
    fun getEmployeeId(): Int {
        return sharedPreferences.getInt(EMP_ID_KEY, -1)
    }

    // 🔹 حفظ أسم الموظف
    fun putEmpName(name: String) {
        sharedPreferences.edit() { putString(EMP_NAME_KEY, name) }
    }

    // 🔹 إسترجاع أسم الموظف
    fun getEmployeeName(): String {
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
    fun getMobileNumber(): String {
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

    fun putEmployeeImage(bitmap: Bitmap): String? {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            // استخدام JPEG للحصول على حجم أصغر، جودة 80% كافية للصور
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // حفظ في SharedPreferences
            sharedPreferences.edit { putString(EMP_IMG_BASE64_KEY, encoded) }
            Log.d("SharedPrefsHelper", "صورة محفوظة كـ Base64: ${encoded.take(20)}...")

            return encoded
        } catch (e: Exception) {
            Log.e("SharedPrefsHelper", "خطأ في حفظ الصورة كـ Base64", e)
            return null
        }
    }

    fun putEmployeeImageFromUri(context: Context, uri: Uri): String? {
        try {
            // حفظ مسار Uri
            val uriString = uri.toString()
            sharedPreferences.edit { putString(EMP_IMG_URI_KEY, uriString) }

            // تحويل Uri إلى Bitmap ثم إلى Base64
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            return putEmployeeImage(bitmap)

        } catch (e: Exception) {
            Log.e("SharedPrefsHelper", "خطأ في حفظ صورة Uri", e)
            return null
        }
    }

    fun getEmployeeImageAsBase64(): String? {
        val encoded = sharedPreferences.getString(EMP_IMG_BASE64_KEY, null)
        Log.d("SharedPrefsHelper", "استعادة صورة Base64: ${encoded?.take(20) ?: "null"}...")
        return encoded
    }

    fun getEmployeeImageAsBitmap(): Bitmap? {
        try {
            val encoded = getEmployeeImageAsBase64()
            if (encoded != null) {
                val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
                return android.graphics.BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
            }
            return null
        } catch (e: Exception) {
            Log.e("SharedPrefsHelper", "خطأ في استعادة الصورة كـ Bitmap", e)
            return null
        }
    }

    fun getEmployeeImageUri(): String? {
        return sharedPreferences.getString(EMP_IMG_URI_KEY, null)
    }

    /**
     * دالة مساعدة للتحقق من وجود صورة للموظف بأي تنسيق
     */
    fun hasEmployeeImage(): Boolean {
        return getEmployeeImageAsBase64() != null || getEmployeeImageUri() != null
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
