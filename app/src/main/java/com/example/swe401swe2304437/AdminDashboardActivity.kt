package com.example.swe401swe2304437

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var categorySpinner: Spinner
    private lateinit var lvConsoleMonitor: ListView
    private lateinit var txtConsoleHeader: TextView

    private lateinit var btnCreateService: Button
    private lateinit var btnSignOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        dbHelper = DatabaseHelper(this)

        categorySpinner = findViewById(R.id.spinnerConsoleCategory)
        lvConsoleMonitor = findViewById(R.id.lvConsoleMonitor)
        txtConsoleHeader = findViewById(R.id.txtConsoleHeader)
        btnCreateService = findViewById(R.id.btnCreateService)
        btnSignOut = findViewById(R.id.btnSignOut)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.admin_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refreshConsoleMonitor(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCreateService.setOnClickListener {
            val intent = Intent(this, AdminAddServiceActivity::class.java)
            startActivity(intent)
        }

        btnSignOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun refreshConsoleMonitor(selectedPosition: Int) {
        val currentCategoryIndex = categorySpinner.selectedItemPosition

        when (selectedPosition) {
            0 -> {
                txtConsoleHeader.text = "Database Monitor: Registered User Accounts"
                val usersList = dbHelper.getAllUsers()

                val adapter = object : SimpleAdapter(
                    this@AdminDashboardActivity,
                    usersList,
                    R.layout.item_admin_console_row,
                    arrayOf("name", "email"),
                    intArrayOf(R.id.txtAdminLine1, R.id.txtAdminLine2)
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val rowView = super.getView(position, convertView, parent)
                        val text1 = rowView.findViewById<TextView>(R.id.txtAdminLine1)
                        val text2 = rowView.findViewById<TextView>(R.id.txtAdminLine2)
                        val btnDelete = rowView.findViewById<ImageButton>(R.id.btnAdminDeleteRow)

                        text1?.setTextColor(Color.BLACK)
                        text2?.setTextColor(Color.parseColor("#333333"))

                        val userItem = usersList[position]
                        val userId = userItem["id"] ?: ""

                        btnDelete?.setOnClickListener {
                            if (userId.isNotEmpty()) {
                                dbHelper.deleteUser(userId)
                                Toast.makeText(this@AdminDashboardActivity, "User Account Deleted", Toast.LENGTH_SHORT).show()
                                refreshConsoleMonitor(currentCategoryIndex)
                            }
                        }
                        return rowView
                    }
                }
                lvConsoleMonitor.adapter = adapter
            }
            1 -> displayFilteredServices("Math")
            2 -> displayFilteredServices("English")
            3 -> displayFilteredServices("General Knowledge")
        }
    }

    private fun displayFilteredServices(subjectKey: String) {
        val currentCategoryIndex = categorySpinner.selectedItemPosition
        txtConsoleHeader.text = "Database Monitor: $subjectKey Services"
        val servicesList = dbHelper.getServicesBySubjectForAdmin(subjectKey)

        val presentationList = servicesList.map { service ->
            mapOf(
                "id" to (service["id"] ?: ""),
                "line1" to "[${service["type"]}] ${service["title"]}",
                "line2" to "Content: ${service["content"]}"
            )
        }

        val adapter = object : SimpleAdapter(
            this@AdminDashboardActivity,
            presentationList,
            R.layout.item_admin_console_row,
            arrayOf("line1", "line2"),
            intArrayOf(R.id.txtAdminLine1, R.id.txtAdminLine2)
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val rowView = super.getView(position, convertView, parent)
                val text1 = rowView.findViewById<TextView>(R.id.txtAdminLine1)
                val text2 = rowView.findViewById<TextView>(R.id.txtAdminLine2)
                val btnDelete = rowView.findViewById<ImageButton>(R.id.btnAdminDeleteRow)

                text1?.setTextColor(Color.BLACK)
                text2?.setTextColor(Color.parseColor("#333333"))

                val serviceItem = presentationList[position]
                val serviceId = serviceItem["id"] ?: ""

                btnDelete?.setOnClickListener {
                    if (serviceId.isNotEmpty()) {
                        dbHelper.deleteService(serviceId)
                        Toast.makeText(this@AdminDashboardActivity, "Service Record Deleted", Toast.LENGTH_SHORT).show()
                        refreshConsoleMonitor(currentCategoryIndex)
                    }
                }
                return rowView
            }
        }
        lvConsoleMonitor.adapter = adapter
    }
}