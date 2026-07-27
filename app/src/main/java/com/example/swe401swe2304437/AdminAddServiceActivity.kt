package com.example.swe401swe2304437

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AdminAddServiceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var selectedVideoUri: Uri? = null
    private lateinit var txtSelectedVideoPath: TextView
    private lateinit var spinnerAdminSubject: Spinner
    private lateinit var spinnerCorrectAnswer: Spinner

    private var isCurrentStepLesson = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_service)

        dbHelper = DatabaseHelper(this)

        val txtWizardStepTitle = findViewById<TextView>(R.id.txtWizardStepTitle)
        spinnerAdminSubject = findViewById(R.id.spinnerAdminSubject)
        val etTitle = findViewById<EditText>(R.id.etAdminTitle)

        val layoutLesson = findViewById<LinearLayout>(R.id.layoutLessonInputs)
        val layoutExercise = findViewById<LinearLayout>(R.id.layoutExerciseInputs)

        val etYoutube = findViewById<EditText>(R.id.etAdminYoutubeLink)
        val btnPickVideo = findViewById<Button>(R.id.btnPickLocalVideo)
        txtSelectedVideoPath = findViewById(R.id.txtSelectedVideoPath)

        val etQuestion = findViewById<EditText>(R.id.etExerciseQuestion)
        val etOptA = findViewById<EditText>(R.id.etOptA)
        val etOptB = findViewById<EditText>(R.id.etOptB)
        val etOptC = findViewById<EditText>(R.id.etOptC)
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer)

        val btnSave = findViewById<Button>(R.id.btnSaveService)

        val subjectOptions = listOf("Math", "English", "General Knowledge")
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectOptions).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerAdminSubject.adapter = subjectAdapter

        val answerOptions = listOf("Option A", "Option B", "Option C")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, answerOptions).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCorrectAnswer.adapter = spinnerAdapter

        val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedVideoUri = result.data?.data
                if (selectedVideoUri != null) {
                    txtSelectedVideoPath.text = "Local Video File Staged and Ready"
                }
            }
        }

        btnPickVideo.setOnClickListener {
            injectRawFileToDownloads(this, R.raw.english_lesson, "test_lesson_upload.mp4")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/mp4"
            }
            videoPickerLauncher.launch(intent)
        }

        btnSave.setOnClickListener {
            val matchedSubject = spinnerAdminSubject.selectedItem.toString()
            val title = etTitle.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title/Chapter designation field required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isCurrentStepLesson) {
                val ytLink = etYoutube.text.toString().trim()

                if (ytLink.isEmpty() || selectedVideoUri == null) {
                    Toast.makeText(this, "Please provide BOTH your YouTube URL link and local video fields", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                selectedVideoUri?.let { nonNullUri ->
                    val savedInternalPath = copyVideoToInternalStorage(nonNullUri)
                    if (savedInternalPath != null) {
                        val finalContentString = "combo|$ytLink|$savedInternalPath"

                        val isSuccess = dbHelper.addService(matchedSubject, title, "Lesson", finalContentString)
                        if (isSuccess) {
                            Toast.makeText(this, "Lesson successfully saved! Now set up the Exercise.", Toast.LENGTH_LONG).show()

                            isCurrentStepLesson = false
                            txtWizardStepTitle.text = "Step 2: Create Exercise Objective Quiz"
                            txtWizardStepTitle.setTextColor(android.graphics.Color.parseColor("#4CAF50"))

                            spinnerAdminSubject.isEnabled = false
                            etTitle.isEnabled = false

                            layoutLesson.visibility = View.GONE
                            layoutExercise.visibility = View.VISIBLE

                            btnSave.text = "Save Exercise & Finish"
                            btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2196F3")))
                        } else {
                            Toast.makeText(this, "Database error saving Lesson resource link assets.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                val qText = etQuestion.text.toString().trim()
                val choiceA = etOptA.text.toString().trim()
                val choiceB = etOptB.text.toString().trim()
                val choiceC = etOptC.text.toString().trim()

                if (qText.isEmpty() || choiceA.isEmpty() || choiceB.isEmpty() || choiceC.isEmpty()) {
                    Toast.makeText(this, "All Question and Option choice inputs must be filled out", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val correctLetterMarker = when (spinnerCorrectAnswer.selectedItemPosition) {
                    0 -> "a"
                    1 -> "b"
                    else -> "c"
                }

                val exerciseContentPayload = "QUIZ: $qText|a. $choiceA|b. $choiceB|c. $choiceC|$correctLetterMarker"

                val isSuccess = dbHelper.addService(matchedSubject, title, "Exercise", exerciseContentPayload)
                if (isSuccess) {
                    Toast.makeText(this, "Exercise registered! Course progression maps altered automatically.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Database write error saving targeted question profiles.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun injectRawFileToDownloads(context: Context, rawResourceId: Int, targetFileName: String) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, targetFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val downloadUri: Uri? = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (downloadUri == null) return

        try {
            val inputStream: InputStream = context.resources.openRawResource(rawResourceId)
            val outputStream: OutputStream? = contentResolver.openOutputStream(downloadUri)
            if (outputStream != null) {
                val streamBuffer = ByteArray(4 * 1024)
                var bytesRead: Int
                while (inputStream.read(streamBuffer).also { bytesRead = it } != -1) {
                    outputStream.write(streamBuffer, 0, bytesRead)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyVideoToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val lessonsFolder = File(filesDir, "lessons").apply { if (!exists()) mkdirs() }
            val targetDestinationFile = File(lessonsFolder, "video_${System.currentTimeMillis()}.mp4")
            val outputStream = FileOutputStream(targetDestinationFile)
            val streamBuffer = ByteArray(4 * 1024)
            var bytesRead: Int
            while (inputStream.read(streamBuffer).also { bytesRead = it } != -1) {
                outputStream.write(streamBuffer, 0, bytesRead)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            targetDestinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace(); null
        }
    }
}