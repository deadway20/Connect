package com.coder_x.connect

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataBaseHelper {
    private const val DB_NAME = "Connect"
    private const val DRIVER_CLASS = "net.sourceforge.jtds.jdbc.Driver"
    private lateinit var SERVER_ADDRESS: String
    private lateinit var SERVER_PORT: String
    private var EMP_ID: Int = 0
    private const val SQL_CONNECTION_TAG = "SQLConnection"


    //🔹 دالة لإنشاء اتصال بقاعدة البيانات
    fun connect(context: Context, serverAddress: String, serverPort: String): Connection? {
        return try {
            val prefsHelper = SharedPrefsHelper(context)

            Class.forName(DRIVER_CLASS)
            val url = "jdbc:jtds:sqlserver://$serverAddress:$serverPort/$DB_NAME"
            DriverManager.getConnection(url, prefsHelper.getUserName(), prefsHelper.getPassword())


        } catch (e: ClassNotFoundException) {
            Log.e(SQL_CONNECTION_TAG, "لم يتم العثور على JDBC Driver", e)
            null
        } catch (e: SQLException) {
            Log.e(SQL_CONNECTION_TAG, "خطأ في الاتصال بقاعدة البيانات", e)
            null
        } catch (e: Exception) {
            Log.e(SQL_CONNECTION_TAG, "خطأ غير متوقع أثناء الاتصال", e)
            null
        }
    }

    //🔹 دالة لإدراج بيانات موظف جديد في قاعدة البيانات
    fun insertEmployee(
        context: Context, name: String, department: String, mobile: String, workHours: Int
    ) = Thread {
        try {
            val prefsHelper = SharedPrefsHelper(context)
            SERVER_ADDRESS = prefsHelper.getServerAddress()
            SERVER_PORT = prefsHelper.getServerPort()

            val connection = connect(context, SERVER_ADDRESS, SERVER_PORT)
            if (connection == null) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "❌ فشل الاتصال بقاعدة البيانات!", Toast.LENGTH_LONG)
                        .show()
                }
                return@Thread
            }
            connection.use { conn ->
                val query =
                    "INSERT INTO EmpInfo (Name, Department, Mobile, WorkHours) VALUES (?, ?, ? ,?)"
                val statement =
                    conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
                statement.setString(1, name)
                statement.setString(2, department)
                statement.setString(3, mobile)
                statement.setInt(4, workHours)

                val rowsInserted = statement.executeUpdate()


                if (rowsInserted > 0) {
                    val resultSet = statement.generatedKeys
                    if (resultSet.next()) {
                        // الحصول على `Emp_ID`
                        EMP_ID = resultSet.getInt(1)
                        prefsHelper.putEmpID(EMP_ID)

                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context, " ✅ تم حفظ البيانات بنجاح!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "⚠️ لم يتم العثور على الـ ID بعد الإدخال!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context, "⚠️ لم يتم إدخال أي بيانات!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        } catch (e: SQLException) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context, " ❌ خطأ في إدخال البيانات: ${e.message}", Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, " ❌ خطأ غير متوقع: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }.start()


    fun checkInEmployee(context: Context, callback: (Boolean, String) -> Unit) {
        Thread {
            try {
                val prefsHelper = SharedPrefsHelper(context)
                SERVER_ADDRESS = prefsHelper.getServerAddress()
                SERVER_PORT = prefsHelper.getServerPort()
                EMP_ID = prefsHelper.getEmpID()

                val connection = connect(context, SERVER_ADDRESS, SERVER_PORT)
                if (connection == null) {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, "❌ فشل الاتصال بقاعدة البيانات!")
                    }
                    return@Thread
                }

                // التحقق مما إذا كان الموظف قد سجل حضوره اليوم بالفعل
                val checkQuery = """
                SELECT CheckInTime FROM Daily 
                WHERE EmpID = ? AND RecordDate = CONVERT(VARCHAR, GETDATE(), 23);
            """
                val checkStatement = connection.prepareStatement(checkQuery)
                checkStatement.setInt(1, EMP_ID)
                val resultSet = checkStatement.executeQuery()

                if (resultSet.next()) {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, "❌ لقد قمت بتسجيل الحضور بالفعل اليوم!")
                    }
                    return@Thread
                }

                // ✅ **تهيئة القيم الافتراضية في كوتلين**
                val checkInTime = getCurrentTime() // دالة لجلب الوقت الحالي
                val absenceStatus = 0


                // ✅ **إدخال القيم المحسوبة مسبقًا إلى قاعدة البيانات**
                val insertQuery = """
                INSERT INTO Daily (EmpID ,RecordDate, CheckInTime, AbsenceStatus) 
                VALUES (?,CONVERT(VARCHAR, GETDATE(), 23), CONVERT(VARCHAR, GETDATE(), 108),?)
            """
                val insertStatement = connection.prepareStatement(insertQuery)
                insertStatement.setInt(1, EMP_ID)
                insertStatement.setInt(2, absenceStatus)

                val rowsAffected = insertStatement.executeUpdate()

                Handler(Looper.getMainLooper()).post {
                    if (rowsAffected > 0) {
                        callback(true, "✅ تم تسجيل الحضور بنجاح!")
                    } else {
                        callback(false, "❌ فشل تسجيل الحضور!")
                    }
                }
            } catch (e: SQLException) {
                Handler(Looper.getMainLooper()).post {
                    callback(false, "❌ خطأ أثناء تسجيل الحضور: ${e.message}")
                }
            }
        }.start()
    }

    fun checkOutEmployee(context: Context, callback: (Boolean, String) -> Unit) {
        Thread {
            try {
                val prefsHelper = SharedPrefsHelper(context)
                SERVER_ADDRESS = prefsHelper.getServerAddress()
                SERVER_PORT = prefsHelper.getServerPort()
                EMP_ID = prefsHelper.getEmpID()

                val connection = connect(context, SERVER_ADDRESS, SERVER_PORT)
                if (connection == null) {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, "❌ فشل الاتصال بقاعدة البيانات!")
                    }
                    return@Thread
                }

                // ✅ **التحقق مما إذا كان الموظف قد سجل حضوره اليوم**
                val checkQuery = """
                SELECT CheckInTime FROM Daily 
                WHERE EmpID = ? AND RecordDate = CONVERT(VARCHAR, GETDATE(), 23);
            """
                val checkStatement = connection.prepareStatement(checkQuery)
                checkStatement.setInt(1, EMP_ID)
                val resultSet = checkStatement.executeQuery()

                if (!resultSet.next()) {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, "❌ لا يوجد تسجيل حضور لهذا اليوم، لا يمكنك تسجيل الانصراف!")
                    }
                    return@Thread
                }

                // ✅ تحديث وقت الانصراف في نفس السجل

                val updateQuery = """
                UPDATE Daily 
                SET CheckOutTime = CONVERT(VARCHAR, GETDATE(), 108), WorkHours = DATEDIFF(minute, CheckInTime, CONVERT(VARCHAR, GETDATE(), 108)) / 60.0 
                WHERE EmpID = ? AND RecordDate = CONVERT(VARCHAR, GETDATE(), 23);
            """
                connection.autoCommit = false  // تعطيل AutoCommit

                val updateStatement = connection.prepareStatement(updateQuery)
                updateStatement.setInt(1, EMP_ID)


                val rowsAffected = updateStatement.executeUpdate()
                connection.commit()  // تأكيد العملية

                Handler(Looper.getMainLooper()).post {
                    if (rowsAffected > 0) {
                        callback(true, "✅ تم تسجيل الانصراف بنجاح!")
                    } else {
                        callback(false, "❌ فشل تسجيل الانصراف!")
                    }
                }
            } catch (e: SQLException) {
                Handler(Looper.getMainLooper()).post {
                    callback(false, "❌ خطأ أثناء تسجيل الانصراف: ${e.message}")
                }
            }
        }.start()
    }


    fun getAttendanceData(context: Context, callback: (EmpData?) -> Unit) {
        Thread {
            try {
                val prefsHelper = SharedPrefsHelper(context)
                SERVER_ADDRESS = prefsHelper.getServerAddress()
                SERVER_PORT = prefsHelper.getServerPort()
                EMP_ID = prefsHelper.getEmpID()
                // إنشاء إتصال جديد بالسيرفر
                val connection = connect(context, SERVER_ADDRESS, SERVER_PORT)
                if (connection == null) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "❌ فشل الاتصال بقاعدة البيانات!", Toast.LENGTH_LONG)
                            .show()
                    }
                    return@Thread
                }
                val query = """
                         SELECT 
                            d.EmpID,
                            CONVERT(VARCHAR, d.RecordDate, 23) AS RecordDate,
                            d.CheckInTime,
                            d.CheckOutTime,
                            e.WorkHours AS RequiredHours,
                            CASE WHEN d.CheckOutTime IS NOT NULL THEN RIGHT('0' + CAST((DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) / 60) AS VARCHAR), 2) + ':' + RIGHT('0' + CAST((DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) % 60) AS VARCHAR), 2) ELSE '00:00' END AS WorkHours,
                            CASE WHEN DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) > (e.WorkHours * 60) THEN RIGHT('0' + CAST(((DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) - (e.WorkHours * 60)) / 60) AS VARCHAR), 2) + ':' + RIGHT('0' + CAST(((DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) - (e.WorkHours * 60)) % 60) AS VARCHAR), 2) ELSE '00:00' END AS Overtime,
                            CASE WHEN DATEDIFF(minute, d.CheckInTime, d.CheckOutTime) < (e.WorkHours * 60) THEN RIGHT('0' + CAST((((e.WorkHours * 60) - DATEDIFF(minute, d.CheckInTime, d.CheckOutTime)) / 60) AS VARCHAR), 2) + ':' + RIGHT('0' + CAST((((e.WorkHours * 60) - DATEDIFF(minute, d.CheckInTime, d.CheckOutTime)) % 60) AS VARCHAR), 2) ELSE '00:00' END AS DelayMinutes,
	                        d.AbsenceStatus
                         FROM Daily d
                         JOIN EmpInfo e ON d.EmpID = e.EmpID
                         WHERE d.EmpID = ? AND d.RecordDate = CONVERT(VARCHAR, GETDATE(), 23);
                        """.trimIndent()

                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, EMP_ID) // استخدام `employeeId` بدلاً من `EMP_ID`
                    statement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            val data = EmpData(
                                recordDate = resultSet.getString("RecordDate"),
                                checkInTime = formatTimeWithAmPm(
                                    resultSet.getString("CheckInTime") ?: "00:00"
                                ),
                                checkOutTime = formatTimeWithAmPm(
                                    resultSet.getString("CheckOutTime") ?: "00:00"
                                ),
                                workHours = resultSet.getString("WorkHours") ?: "00:00",
                                overtimeInMinutes = resultSet.getString("Overtime") ?: "00:00",
                                delayInMinutes = resultSet.getString("DelayMinutes") ?: "00:00",
                                isAbsent = resultSet.getBoolean("AbsenceStatus")
                            )

                            // ✅ **حفظ البيانات في `SharedPreferences` بعد جلبها**
                            prefsHelper.saveData(data)

                            Handler(Looper.getMainLooper()).post {
                                callback(data)
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                callback(null)
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context, "❌ خطأ في جلب البيانات: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    private fun formatTimeWithAmPm(timeString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val outputFormat =
                SimpleDateFormat("hh:mm a", Locale.getDefault()) // تنسيق 12 ساعة مع AM/PM
            val date = inputFormat.parse(timeString)
            if (date != null) {
                outputFormat.format(date)
            } else {
                "00:00"
            }
        } catch (e: Exception) {
            Log.e("formatTimeWithAmPm", "خطأ في تحويل الوقت: ${e.message}")
            "00:00"
        }
    }


    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
