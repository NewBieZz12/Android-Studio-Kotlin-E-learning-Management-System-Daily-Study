package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChapterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var subjectName: String = "English"
    private lateinit var lvChapters: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)

        dbHelper = DatabaseHelper(this)
        lvChapters = findViewById(R.id.lvChapters)

        subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "English"

        val toolbarTitle: TextView? = findViewById(R.id.chapterToolbarTitle)
        val backButton: ImageView? = findViewById(R.id.chapterBackButton)

        toolbarTitle?.text = "$subjectName Chapters"
        backButton?.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadDynamicChapterList()
    }

    private fun loadDynamicChapterList() {
        val rawTitles = dbHelper.getLessonsBySubject(subjectName)

        val chapterItemsList = rawTitles.mapIndexed { index, title ->
            val downloaded = dbHelper.isServiceDownloaded(subjectName, title, "Lesson")
            ChapterItem(title, "Lesson ${index + 1}", downloaded)
        }

        val adapter = ChapterAdapter(this, chapterItemsList) { item ->
            val newStatus = if (item.isDownloaded) 0 else 1
            dbHelper.setDownloadStatus(subjectName, item.title, "Lesson", newStatus)

            loadDynamicChapterList()
        }
        lvChapters.adapter = adapter

        lvChapters.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = lvChapters.adapter.getItem(position) as ChapterItem

            if (clickedItem.isDownloaded) {
                val intent = Intent(this, LessonActivity::class.java).apply {
                    putExtra("EXTRA_SUBJECT_NAME", subjectName)
                    putExtra("EXTRA_LESSON_TITLE", clickedItem.title)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please download this chapter first!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}