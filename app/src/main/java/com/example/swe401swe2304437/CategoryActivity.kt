package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val backArrow: ImageView = findViewById(R.id.backArrowButton)
        val titleText: TextView = findViewById(R.id.categorySubjectTitle)
        val btnLesson: LinearLayout = findViewById(R.id.btnLesson)
        val btnExercise: LinearLayout = findViewById(R.id.btnExercise)

        val subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "Subject"
        titleText.text = subjectName

        backArrow.setOnClickListener {
            finish()
        }

        btnLesson.setOnClickListener {
            val intent = Intent(this, ChapterActivity::class.java).apply {
                putExtra("EXTRA_SUBJECT_NAME", subjectName)
            }
            startActivity(intent)
        }

        btnExercise.setOnClickListener {
            val intent = Intent(this, ChapterExerciseActivity::class.java).apply {
                putExtra("EXTRA_SUBJECT_NAME", subjectName)
            }
            startActivity(intent)
        }
    }
}