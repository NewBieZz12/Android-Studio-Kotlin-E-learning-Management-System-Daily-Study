package com.example.swe401swe2304437

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var btnCourse: LinearLayout
    private lateinit var btnProfile: LinearLayout
    private lateinit var tvCourse: TextView
    private lateinit var tvProfile: TextView
    private lateinit var ivCourse: ImageView
    private lateinit var ivProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCourse = findViewById(R.id.btnNavCourse)
        btnProfile = findViewById(R.id.btnNavProfile)
        tvCourse = findViewById(R.id.tvNavCourse)
        tvProfile = findViewById(R.id.tvNavProfile)
        ivCourse = findViewById(R.id.ivNavCourseIcon)
        ivProfile = findViewById(R.id.ivNavProfileIcon)

        if (savedInstanceState == null) {
            switchFragment(HomeFragment())
            updateNavigationHighlight(isCourseActive = true)
        }

        btnCourse.setOnClickListener {
            switchFragment(HomeFragment())
            updateNavigationHighlight(isCourseActive = true)
        }

        btnProfile.setOnClickListener {
            switchFragment(ProfileFragment())
            updateNavigationHighlight(isCourseActive = false)
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateNavigationHighlight(isCourseActive: Boolean) {
        if (isCourseActive) {
            tvCourse.setTextColor(Color.parseColor("#1A237E"))
            ivCourse.setColorFilter(Color.parseColor("#1A237E"))
            tvCourse.setTypeface(null, android.graphics.Typeface.BOLD)

            tvProfile.setTextColor(Color.parseColor("#555555"))
            ivProfile.setColorFilter(Color.parseColor("#555555"))
            tvProfile.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            tvProfile.setTextColor(Color.parseColor("#1A237E"))
            ivProfile.setColorFilter(Color.parseColor("#1A237E"))
            tvProfile.setTypeface(null, android.graphics.Typeface.BOLD)

            tvCourse.setTextColor(Color.parseColor("#555555"))
            ivCourse.setColorFilter(Color.parseColor("#555555"))
            tvCourse.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }
}