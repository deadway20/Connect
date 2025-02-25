package com.coder_x.connect

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DataBaseHelper {

    private const val DB_NAME = "Connect"
    private const val USER = "sa"
    private const val PASSWORD = "12345678"

    fun connect(serverAddress: String, serverPort: String): Connection? {
        return try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            val url =
                "jdbc:jtds:sqlserver://$serverAddress:$serverPort/$DB_NAME"  // استخدام الـ serverPort من المستخدم
            DriverManager.getConnection(url, USER, PASSWORD)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


//    fun insertEmployeeSimple(
//        context: Context,
//        name: String,
//        department: String,
//        mobile: String,
//        workHours: Int
//    ) {
//        try {
//            // استرجاع بيانات السيرفر من SharedPreferences باستخدام Context
//            val sharedPreferences =
//                context.getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE)
//            val serverAddress = sharedPreferences.getString("serverAddress", "") ?: ""
//            val serverPort = sharedPreferences.getString("serverPort", "") ?: ""
//
//            // التحقق إذا كانت القيم فارغة
//            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
//                Toast.makeText(
//                    context,
//                    "❌ يرجى الرجوع وإدخال عنوان السيرفر والمنفذ في الإعدادات",
//                    Toast.LENGTH_LONG
//                ).show()
//                return
//            }
//
//            // الاتصال بالسيرفر
//            val connection = connect(serverAddress, serverPort)
//
//            if (connection == null) {
//                Toast.makeText(
//                    context,
//                    "❌ فشل الاتصال بالسيرفر، تحقق من الإعدادات",
//                    Toast.LENGTH_LONG
//                ).show()
//                return
//            }
//            // الاتصال بالسيرفر باستخدام القيم المخزنة
//            val query =
//                "INSERT INTO EmpInfo (Name,Department,Mobile,WorkHours) VALUES('$name', '$department', '$mobile', '$workHours')"
//            val statement = connection.createStatement()
//            statement?.executeUpdate(query)
//            connection.close()
//
//            Toast.makeText(context, "تم حفظ البيانات في السيرفر بنجاح ✅", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(
//                context,
//                "❌ حدث خطأ أثناء إدخال البيانات: ${e.message}",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }


    fun insertEmployeeSimple(
        context: Context, name: String, department: String, mobile: String, workHours: Int
    ) {
        try {
            // استرجاع بيانات السيرفر من SharedPreferences
            val sharedPreferences =
                context.getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE)
            val serverAddress = sharedPreferences.getString("serverAddress", "") ?: ""
            val serverPort = sharedPreferences.getString("serverPort", "") ?: ""

            // طباعة تفاصيل الاتصال بالسيرفر
            Log.d("mylogd", "Server Address: $serverAddress , Server Port: $serverPort")
            // التحقق إذا كانت القيم فارغة
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(
                    context, "❌ يرجى إدخال عنوان السيرفر والمنفذ في الإعدادات", Toast.LENGTH_LONG
                ).show()
                return
            }

            // الاتصال بالسيرفر
            val connection = connect(serverAddress, serverPort)
            if (connection == null) {
                Toast.makeText(
                    context, "❌ فشل الاتصال بالسيرفر، تحقق من الإعدادات", Toast.LENGTH_LONG
                ).show()
                return
            }

            // تجهيز الاستعلام
            val query =
                "INSERT INTO EmpInfo (Name, Department, Mobile, WorkHours) VALUES (?, ?, ?, ?)"
            val preparedStatement = connection.prepareStatement(query)

            // تعيين القيم في الاستعلام لتجنب مشاكل SQL Injection
            preparedStatement.setString(1, name)
            preparedStatement.setString(2, department)
            preparedStatement.setString(3, mobile)
            preparedStatement.setInt(4, workHours)

            // تنفيذ الاستعلام ومعرفة عدد الصفوف المتأثرة
            val rowsAffected = preparedStatement.executeUpdate()

            // إغلاق الاتصال
            preparedStatement.close()
            connection.close()

            // التحقق مما إذا كان الإدراج ناجحًا
            if (rowsAffected > 0) {
                Toast.makeText(context, "✅ تم حفظ البيانات في السيرفر بنجاح!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    context, "⚠️ لم يتم حفظ أي بيانات، تحقق من صحة الاستعلام", Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "❌ خطأ أثناء إدخال البيانات: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

}