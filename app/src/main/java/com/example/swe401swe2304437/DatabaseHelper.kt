package com.example.swe401swe2304437

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DailyStudyApp.db"
        private const val DATABASE_VERSION = 8

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_NAME = "username"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_ROLE = "role"

        const val TABLE_SERVICES = "services"
        const val COLUMN_SERVICE_ID = "service_id"
        const val COLUMN_SERVICE_SUBJECT = "subject_name"
        const val COLUMN_SERVICE_TITLE = "title"
        const val COLUMN_SERVICE_TYPE = "type"
        const val COLUMN_SERVICE_CONTENT = "content"

        const val COLUMN_SERVICE_COMPLETED = "is_completed"
        const val COLUMN_SERVICE_DOWNLOADED = "is_downloaded"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_USER_ROLE + " TEXT)")
        db.execSQL(createUsersTable)

        val createServicesTable = ("CREATE TABLE " + TABLE_SERVICES + " ("
                + COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SERVICE_SUBJECT + " TEXT, "
                + COLUMN_SERVICE_TITLE + " TEXT, "
                + COLUMN_SERVICE_TYPE + " TEXT, "
                + COLUMN_SERVICE_CONTENT + " TEXT, "
                + COLUMN_SERVICE_COMPLETED + " INTEGER DEFAULT 0, "
                + COLUMN_SERVICE_DOWNLOADED + " INTEGER DEFAULT 0)")
        db.execSQL(createServicesTable)

        val adminValues = ContentValues().apply {
            put(COLUMN_USER_NAME, "admin")
            put(COLUMN_USER_EMAIL, "admin@gmail.com")
            put(COLUMN_USER_PASSWORD, "admin123")
            put(COLUMN_USER_ROLE, "Admin")
        }
        db.insert(TABLE_USERS, null, adminValues)

        insertDefaultServices(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICES")
        onCreate(db)
    }

    private fun insertDefaultServices(db: SQLiteDatabase) {
        val englishLesson = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "English")
            put(COLUMN_SERVICE_TITLE, "Make & Do")
            put(COLUMN_SERVICE_TYPE, "Lesson")
            put(COLUMN_SERVICE_CONTENT, "raw|english_lesson|https://www.youtube.com/watch?v=N9B59PHIFbA")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, englishLesson)

        val englishExercise = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "English")
            put(COLUMN_SERVICE_TITLE, "Exercise 1: Make & Do")
            put(COLUMN_SERVICE_TYPE, "Exercise")
            put(COLUMN_SERVICE_CONTENT, "QUIZ: Which is INCORRECT?|a. This video makes me want to study.|b. I’m making some study with this video.|c. I’m making a video to study with.|b")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, englishExercise)

        val mathLesson = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "Math")
            put(COLUMN_SERVICE_TITLE, "Addition Solution")
            put(COLUMN_SERVICE_TYPE, "Lesson")
            put(COLUMN_SERVICE_CONTENT, "raw|math_lesson|https://www.youtube.com/watch?v=KgZIXq04ee8")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, mathLesson)

        val mathExercise = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "Math")
            put(COLUMN_SERVICE_TITLE, "Exercise 1: Addition")
            put(COLUMN_SERVICE_TYPE, "Exercise")
            put(COLUMN_SERVICE_CONTENT, "QUIZ: 59 + 25 = ?|a. 84|b. 89|c. 74|a")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, mathExercise)

        val gkLesson = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "General Knowledge")
            put(COLUMN_SERVICE_TITLE, "Geography")
            put(COLUMN_SERVICE_TYPE, "Lesson")
            put(COLUMN_SERVICE_CONTENT, "raw|geo_lesson|https://www.youtube.com/watch?v=c0XEcBxlW0o")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, gkLesson)

        val gkExercise = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, "General Knowledge")
            put(COLUMN_SERVICE_TITLE, "Exercise 1: Geography")
            put(COLUMN_SERVICE_TYPE, "Exercise")
            put(COLUMN_SERVICE_CONTENT, "QUIZ: What is geography?|a. The study of places and relationships between people.|b. The study of ancient dinosaur fossils.|c. The calculation of celestial distances.|a")
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        db.insert(TABLE_SERVICES, null, gkExercise)
    }

    fun getExerciseDetails(subjectName: String, exerciseTitle: String): String? {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_CONTENT FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Exercise' LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName, exerciseTitle))

        var exerciseContent: String? = null
        if (cursor.moveToFirst()) {
            exerciseContent = cursor.getString(0)
        }
        cursor.close()
        return exerciseContent
    }

    fun isServiceDownloaded(subjectName: String, title: String, type: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_DOWNLOADED FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName, title, type))
        var downloaded = false
        if (cursor.moveToFirst()) {
            downloaded = cursor.getInt(0) == 1
        }
        cursor.close()
        return downloaded
    }

    fun setDownloadStatus(subjectName: String, title: String, type: String, status: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SERVICE_DOWNLOADED, status)
            if (status == 0) {
                put(COLUMN_SERVICE_COMPLETED, 0)
            }
        }
        return db.update(TABLE_SERVICES, values,
            "$COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = ?",
            arrayOf(subjectName, title, type)) > 0
    }

    fun getExerciseContent(subjectName: String, title: String): String? {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_CONTENT FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = 'Exercise' LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName, title))

        var content: String? = null
        if (cursor.moveToFirst()) {
            content = cursor.getString(0)
        }
        cursor.close()
        return content
    }

    fun getLessonsBySubject(subjectName: String): List<String> {
        val titles = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_TITLE FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Lesson'"
        val cursor = db.rawQuery(query, arrayOf(subjectName))

        if (cursor.moveToFirst()) {
            do {
                titles.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return titles
    }

    fun getExercisesBySubject(subjectName: String): List<String> {
        val titles = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_TITLE FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Exercise'"
        val cursor = db.rawQuery(query, arrayOf(subjectName))

        if (cursor.moveToFirst()) {
            do {
                titles.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return titles
    }

    fun markExerciseAsCleared(subjectName: String, exerciseTitle: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SERVICE_COMPLETED, 1)
        }
        val result = db.update(
            TABLE_SERVICES,
            values,
            "$COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = 'Exercise'",
            arrayOf(subjectName, exerciseTitle)
        )
        return result > 0
    }

    fun isExerciseCleared(subjectName: String, exerciseTitle: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SERVICE_COMPLETED FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = 'Exercise' LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName, exerciseTitle))

        var cleared = false
        if (cursor.moveToFirst()) {
            cleared = cursor.getInt(0) == 1
        }
        cursor.close()
        return cleared
    }

    fun getTotalStarsCount(): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_TYPE = 'Exercise' AND $COLUMN_SERVICE_COMPLETED = 1"
        val cursor = db.rawQuery(query, null)

        var totalStars = 0
        if (cursor.moveToFirst()) {
            totalStars = cursor.getInt(0)
        }
        cursor.close()
        return totalStars
    }

    fun getSubjectProgressPercentage(subjectName: String): Int {
        val db = this.readableDatabase

        val totalQuery = "SELECT COUNT(*) FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Exercise'"
        val totalCursor = db.rawQuery(totalQuery, arrayOf(subjectName))
        var totalExercises = 0
        if (totalCursor.moveToFirst()) {
            totalExercises = totalCursor.getInt(0)
        }
        totalCursor.close()

        if (totalExercises == 0) return 0

        val completedQuery = "SELECT COUNT(*) FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Exercise' AND $COLUMN_SERVICE_COMPLETED = 1"
        val completedCursor = db.rawQuery(completedQuery, arrayOf(subjectName))
        var completedExercises = 0
        if (completedCursor.moveToFirst()) {
            completedExercises = completedCursor.getInt(0)
        }
        completedCursor.close()

        return (completedExercises * 100) / totalExercises
    }

    fun getDistinctSubjectsForHome(): List<Subject> {
        val subjectList = mutableListOf<Subject>()
        val db = this.readableDatabase

        val query = "SELECT DISTINCT $COLUMN_SERVICE_SUBJECT FROM $TABLE_SERVICES"
        val cursor = db.rawQuery(query, null)

        var simulatedId = 1
        if (cursor.moveToFirst()) {
            do {
                val subjectName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_SUBJECT))

                val cleanSubjectDisplay = when {
                    subjectName.equals("Math", ignoreCase = true) -> "Math"
                    subjectName.equals("English", ignoreCase = true) -> "English"
                    subjectName.contains("General", ignoreCase = true) || subjectName.contains("Knowledge", ignoreCase = true) || subjectName.equals("Geo", ignoreCase = true) -> "General Knowledge"
                    else -> subjectName
                }

                val illustrationResource = R.drawable.avatar

                if (subjectList.none { it.title == cleanSubjectDisplay }) {
                    subjectList.add(Subject(simulatedId, cleanSubjectDisplay, illustrationResource))
                    simulatedId++
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjectList
    }

    fun getLessonDetails(subjectName: String, lessonTitle: String): Map<String, String>? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TITLE = ? AND $COLUMN_SERVICE_TYPE = 'Lesson' LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName, lessonTitle))

        var result: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            result = mapOf(
                "title" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TITLE)),
                "content_data" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_CONTENT))
            )
        }
        cursor.close()
        return result
    }

    fun checkUserLogin(nameOrEmail: String, password: CharSequence): String? {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_USER_NAME, $COLUMN_USER_ROLE FROM $TABLE_USERS " +
                "WHERE ($COLUMN_USER_NAME = ? OR $COLUMN_USER_EMAIL = ?) AND $COLUMN_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(nameOrEmail, nameOrEmail, password.toString()))

        var roleResult: String? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val role = cursor.getString(1)
            roleResult = "$name:$role"
        }
        cursor.close()
        return roleResult
    }

    fun registerUser(name: String, email: String, password: CharSequence, role: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, name)
            put(COLUMN_USER_EMAIL, email)
            put(COLUMN_USER_PASSWORD, password.toString())
            put(COLUMN_USER_ROLE, role)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun doesEmailExist(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updatePassword(email: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_PASSWORD, newPassword)
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_USER_EMAIL = ?", arrayOf(email))
        return result > 0
    }

    fun getAllUsers(): List<Map<String, String>> {
        val userList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)).toString(),
                    "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    "role" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    fun deleteUser(userId: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USERS, "$COLUMN_USER_ID = ?", arrayOf(userId))
        return result > 0
    }

    fun updateUserRole(userId: String, newRole: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_USER_ROLE, newRole) }
        val result = db.update(TABLE_USERS, values, "$COLUMN_USER_ID = ?", arrayOf(userId))
        return result > 0
    }

    fun addService(subject: String, title: String, type: String, content: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SERVICE_SUBJECT, subject)
            put(COLUMN_SERVICE_TITLE, title)
            put(COLUMN_SERVICE_TYPE, type)
            put(COLUMN_SERVICE_CONTENT, content)
            put(COLUMN_SERVICE_COMPLETED, 0)
            put(COLUMN_SERVICE_DOWNLOADED, 0)
        }
        val result = db.insert(TABLE_SERVICES, null, values)
        return result != -1L
    }

    fun getServicesBySubjectForAdmin(subjectName: String): List<Map<String, String>> {
        val list = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ?"
        val cursor = db.rawQuery(query, arrayOf(subjectName))

        if (cursor.moveToFirst()) {
            do {
                val map = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_ID)).toString(),
                    "subject" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_SUBJECT)),
                    "title" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TITLE)),
                    "type" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TYPE)),
                    "content" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_CONTENT))
                )
                list.add(map)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getAllServices(): List<Map<String, String>> {
        val list = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SERVICES", null)

        if (cursor.moveToFirst()) {
            do {
                val map = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_ID)).toString(),
                    "subject" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_SUBJECT)),
                    "title" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TITLE)),
                    "type" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TYPE)),
                    "content" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_CONTENT))
                )
                list.add(map)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateService(serviceId: String, title: String, content: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SERVICE_TITLE, title)
            put(COLUMN_SERVICE_CONTENT, content)
        }
        val result = db.update(TABLE_SERVICES, values, "$COLUMN_SERVICE_ID = ?", arrayOf(serviceId))
        return result > 0
    }

    fun deleteService(serviceId: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_SERVICES, "$COLUMN_SERVICE_ID = ?", arrayOf(serviceId))
        return result > 0
    }

    fun isSubjectCleared(subjectName: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SERVICES WHERE $COLUMN_SERVICE_SUBJECT = ? AND $COLUMN_SERVICE_TYPE = 'Exercise' AND $COLUMN_SERVICE_COMPLETED = 1"
        val cursor = db.rawQuery(query, arrayOf(subjectName))
        var completedCount = 0
        if (cursor.moveToFirst()) {
            completedCount = cursor.getInt(0)
        }
        cursor.close()
        return completedCount > 0
    }
}