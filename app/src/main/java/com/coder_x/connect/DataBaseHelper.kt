package com.coder_x.connect

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DataBaseHelper {
    private const val DB_NAME = "Connect"
    private const val DRIVER_CLASS = "net.sourceforge.jtds.jdbc.Driver"
    private lateinit var SERVER_ADDRESS: String
    private lateinit var SERVER_PORT: String
    private const val SQL_CONNECTION_TAG = "SQLConnection"

    //🔹 دالة لإنشاء اتصال بقاعدة البيانات
    fun connect(
        context: Context, serverAddress: String, serverPort: String
    ): Connection? {
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
    ) {
        Thread {
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
                            val empID = resultSet.getInt(1)
                            // حفظ `Emp_ID` في SharedPreferences
                            prefsHelper.putEmpID(empID)

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    context, " ✅ تم حفظ البيانات بنجاح!$empID", Toast.LENGTH_SHORT
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
                Log.e(SQL_CONNECTION_TAG, "خطأ SQL", e)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context, " ❌ خطأ في إدخال البيانات: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(SQL_CONNECTION_TAG, "خطأ غير متوقع", e)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, " ❌ خطأ غير متوقع: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }.start()
    }

}