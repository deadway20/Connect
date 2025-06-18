@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package com.coder_x.connect.helpers

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import com.coder_x.connect.model.EmployeeData
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

    fun putEmployeeImage(context: Context, uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val byteArray = outputStream.toByteArray()
            val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)

            sharedPreferences.edit().apply {
                putString(EMP_IMG_BASE64_KEY, encoded)
                putString(EMP_IMG_URI_KEY, uri.toString())
                apply()
            }

            Log.d("SharedPrefsHelper", "تم حفظ الصورة بنجاح (Base64 + Uri)")
        } catch (e: Exception) {
            Log.e("SharedPrefsHelper", "فشل في حفظ صورة الموظف", e)
        }
    }

    fun getEmployeeImageBitmap(): Bitmap? {
        return try {
            val encoded = sharedPreferences.getString(EMP_IMG_BASE64_KEY, null)
            if (!encoded.isNullOrEmpty() && encoded.length > 100) {
                val cleanBase64 = encoded.substringAfter("base64,", encoded)
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                Log.e("SharedPrefs", "البيانات فارغة أو غير صالحة")
                null
            }
        } catch (e: Exception) {
            Log.e("SharedPrefs", "خطأ في استرجاع الصورة", e)
            null
        }
    }


    fun getEmployeeImageBase64(): String? {
        return sharedPreferences.getString(EMP_IMG_BASE64_KEY, null)
    }


    fun getEmployeeImageUri(): Uri? {
        val uriString = sharedPreferences.getString(EMP_IMG_URI_KEY, null)
        return uriString?.toUri()
    }
    // 🔹 لمسح جميع البيانات المحفوظة
    fun clearPrefs() {
        sharedPreferences.edit() { clear() }
    }


    //🔹 للتحقق من إتمام الإعدادات
    fun isSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean(COMPLETED, false)
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
