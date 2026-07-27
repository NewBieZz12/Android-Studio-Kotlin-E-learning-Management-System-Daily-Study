package com.example.swe401swe2304437

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class LessonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        val backArrow: ImageView = findViewById(R.id.lessonBackButton)
        val titleText: TextView = findViewById(R.id.lessonToolbarTitle)
        val txtSourceLink: TextView = findViewById(R.id.txtOriginalSourceLink)
        val videoPlayer: VideoView = findViewById(R.id.lessonVideoPlayer)

        val subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "English"
        val lessonTitle = intent.getStringExtra("EXTRA_LESSON_TITLE") ?: "Make & Do"

        val dbHelper = DatabaseHelper(this)
        val lessonData = dbHelper.getLessonDetails(subjectName, lessonTitle)

        var targetVideoUrl = "https://www.youtube.com"

        if (lessonData != null) {
            val title = lessonData["title"] ?: "Lesson"
            titleText.text = title

            val contentData = lessonData["content_data"] ?: ""
            if (contentData.contains("|")) {
                val parts = contentData.split("|")
                val streamTypeFlag = parts[0].trim().lowercase()

                when (streamTypeFlag) {
                    "combo" -> {
                        if (parts.size >= 3) {
                            targetVideoUrl = parts[1].trim()
                            val localFilePath = parts[2].trim()

                            txtSourceLink.visibility = View.VISIBLE
                            txtSourceLink.text = "Watch this lesson on YouTube"

                            val videoFile = File(localFilePath)
                            if (videoFile.exists()) {
                                try {
                                    videoPlayer.setVideoPath(videoFile.absolutePath)
                                    setupMediaControls(videoPlayer)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(this, "Error playing local video file", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Local video file missing from app storage.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    "raw" -> {
                        if (parts.size >= 2) {
                            val rawResourceName = parts[1].trim()

                            if (parts.size >= 3) {
                                targetVideoUrl = parts[2].trim()
                            }

                            txtSourceLink.visibility = View.VISIBLE
                            txtSourceLink.text = "Watch this lesson on YouTube"

                            val resourceId = resources.getIdentifier(rawResourceName, "raw", packageName)
                            if (resourceId != 0) {
                                try {
                                    val localVideoPath = "android.resource://$packageName/$resourceId"
                                    videoPlayer.setVideoURI(Uri.parse(localVideoPath))
                                    setupMediaControls(videoPlayer)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                Toast.makeText(this, "Video resource file not found in res/raw", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> {
                        if (parts.size >= 2) {
                            val legacyRawResourceName = parts[0].trim()
                            targetVideoUrl = parts[1].trim()

                            txtSourceLink.visibility = View.VISIBLE
                            txtSourceLink.text = "Watch this lesson on YouTube"

                            val resourceId = resources.getIdentifier(legacyRawResourceName, "raw", packageName)
                            if (resourceId != 0) {
                                videoPlayer.setVideoURI(Uri.parse("android.resource://$packageName/$resourceId"))
                                setupMediaControls(videoPlayer)
                            }
                        }
                    }
                }
            } else {
                txtSourceLink.visibility = View.VISIBLE
                txtSourceLink.text = "Watch this lesson on YouTube"

                val resourceId = resources.getIdentifier(contentData.trim(), "raw", packageName)
                if (resourceId != 0) {
                    videoPlayer.setVideoURI(Uri.parse("android.resource://$packageName/$resourceId"))
                    setupMediaControls(videoPlayer)
                }
            }
        } else {
            titleText.text = "Lesson Not Found"
            txtSourceLink.visibility = View.GONE
        }

        backArrow.setOnClickListener {
            finish()
        }

        txtSourceLink.setOnClickListener {
            try {
                val webUri = Uri.parse(targetVideoUrl)
                val implicitIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(implicitIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid YouTube link or web browser missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMediaControls(videoView: VideoView) {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.start()
    }
}