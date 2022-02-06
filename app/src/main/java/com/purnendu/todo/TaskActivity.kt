package com.purnendu.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskActivity : AppCompatActivity(), View.OnClickListener {


    private var myCalendar: Calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private lateinit var editTextSetDate: TextInputEditText
    private lateinit var editTextSetTime: TextInputEditText
    private lateinit var editTextTaskTitle: TextInputEditText
    private lateinit var editTextTaskDescription: TextInputEditText
    private lateinit var saveTaskButton: MaterialButton
    private lateinit var textInputLayout3: TextInputLayout
    private lateinit var spinner: Spinner
    private lateinit var addLabels:ImageView
    private lateinit var database: AppDatabase

    var labels = arrayListOf("Business", "Personal", "Official", "Health", "Travelling")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        editTextSetDate = findViewById(R.id.editTextSetDate)
        editTextSetTime = findViewById(R.id.editTextSetTime)
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle)
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        textInputLayout3 = findViewById(R.id.textInputLayout3)
        spinner = findViewById(R.id.spinner)
        addLabels=findViewById(R.id.addLabels)

        editTextSetDate.setOnClickListener(this)
        editTextSetTime.setOnClickListener(this)
        saveTaskButton.setOnClickListener(this)

        database= AppDatabase.getDataBase(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        spinner.adapter = adapter

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateLabel()

        }

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->


            myCalendar.set(Calendar.HOUR, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            updateTimeLabel()
        }

    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.editTextSetDate -> {
                openDatePickerDialog()
            }

            R.id.editTextSetTime -> {
                openTimePickerDialog()
            }

            R.id.saveTaskButton -> {
                dataValidation()
            }
            R.id.addLabels->
            {

            }
        }
    }

    private fun dataValidation() {

        if(editTextTaskTitle.text!!.isEmpty())
        {
            editTextTaskTitle.error = "Required Field"
            return
        }

        if( editTextTaskDescription.text!!.isEmpty())
        {
            editTextTaskDescription.error = "Required Field"
            return
        }
        if(editTextSetDate.text!!.isEmpty())
        {
            editTextSetDate.error = "Set Date"
            return
        }
        if(editTextSetTime.text!!.isEmpty())
        {
            editTextSetTime.error = "Set Time"
            return
        }

        if(timeValidation())
        saveIntoDatabase()
        else
            Toast.makeText(this,"You can not select previous time",Toast.LENGTH_SHORT).show()
    }

    private fun timeValidation():Boolean {
        val date=myCalendar.time
        val currentDate = Calendar.getInstance().time
        if(date.before(currentDate))
            return false
        return true

    }

    private fun saveIntoDatabase() {

       CoroutineScope(Dispatchers.IO).launch {
            database.taskDao().insertTask(TaskModel(editTextTaskTitle.text.toString(),
            editTextTaskDescription.text.toString(),spinner.selectedItem.toString(),myCalendar.time.time,myCalendar.time.time))
            startActivity(Intent(this@TaskActivity,MainActivity::class.java))
        }
    }

    private fun openTimePickerDialog() {

        val dialog = TimePickerDialog(
            this,
            timeSetListener,
            myCalendar.get(Calendar.HOUR),
            myCalendar.get(Calendar.MINUTE),
            false
        )
        dialog.show()

    }


    private fun openDatePickerDialog() {

        val dialog = DatePickerDialog(
            this,
            dateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun updateDateLabel() {
        val myFormat = "EEE, d MMM yyyy"
        val local = Locale("English")
        val sdf = SimpleDateFormat(myFormat, local)
        editTextSetDate.setText(sdf.format(myCalendar.time))
        textInputLayout3.visibility = View.VISIBLE
    }

    private fun updateTimeLabel() {
        val myFormat = "h:mm a"
        val local = Locale("English")
        val sdf = SimpleDateFormat(myFormat, local)
        editTextSetTime.setText(sdf.format(myCalendar.time))
    }
}