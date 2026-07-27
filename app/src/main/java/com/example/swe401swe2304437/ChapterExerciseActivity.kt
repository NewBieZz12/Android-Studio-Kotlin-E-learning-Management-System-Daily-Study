package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChapterExerciseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var subjectName: String = "English"
    private lateinit var lvExercises: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_exercise)

        dbHelper = DatabaseHelper(this)
        lvExercises = findViewById(R.id.lvExercises)

        subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "English"

        val toolbarTitle: TextView? = findViewById(R.id.exerciseChapterToolbarTitle)
        val backArrow: ImageView? = findViewById(R.id.exerciseChapterBackButton)

        toolbarTitle?.text = "$subjectName Exercises"
        backArrow?.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadDynamicExerciseList()
    }

    private fun loadDynamicExerciseList() {
        val rawTitles = dbHelper.getExercisesBySubject(subjectName)

        val exerciseItemsList = rawTitles.mapIndexed { index, title ->
            val downloaded = dbHelper.isServiceDownloaded(subjectName, title, "Exercise")
            val isCleared = dbHelper.isExerciseCleared(subjectName, title)
            val statusText = if (isCleared) "⭐ Completed" else "✗ Incomplete"

            ExerciseItem(title, "Exercise ${index + 1} | Status: $statusText", downloaded)
        }

        val adapter = ExerciseAdapter(this, exerciseItemsList) { item ->
            val newStatus = if (item.isDownloaded) 0 else 1
            dbHelper.setDownloadStatus(subjectName, item.title, "Exercise", newStatus)

            loadDynamicExerciseList()
        }
        lvExercises.adapter = adapter

        lvExercises.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = exerciseItemsList[position]

            if (clickedItem.isDownloaded) {
                val intent = Intent(this, QuizActivity::class.java).apply {
                    putExtra("EXTRA_SUBJECT_NAME", subjectName)
                    putExtra("EXTRA_EXERCISE_TITLE", clickedItem.title)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please download this exercise first!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}