package com.purnendu.todo.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.purnendu.todo.database.AppDatabase
import com.purnendu.todo.R
import com.purnendu.todo.database.TaskModel
import com.purnendu.todo.TimeFormatter
import com.purnendu.todo.databinding.ActivityTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TaskActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityTaskBinding
    private var myCalendar: Calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private lateinit var database: AppDatabase
    private val labels = arrayListOf("Business", "Personal", "Official", "Health", "Travelling")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task)


        binding.apply {
            editTextSetDate.setOnClickListener(this@TaskActivity)
            editTextSetTime.setOnClickListener(this@TaskActivity)
            saveTaskButton.setOnClickListener(this@TaskActivity)
        }


        database = AppDatabase.getDataBase(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.spinner.adapter = adapter

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateLabel()
        }

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            binding.editTextSetTime.setText(TimeFormatter.timeFormat().format(myCalendar.time))
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
            R.id.addLabels -> {

            }
        }
    }

    private fun dataValidation() {

        binding.apply {
            if (editTextTaskTitle.text!!.isEmpty()) {
                editTextTaskTitle.error = "Required Field"
                return
            }

            if (editTextTaskDescription.text!!.isEmpty()) {
                editTextTaskDescription.error = "Required Field"
                return
            }
            if (editTextSetDate.text!!.isEmpty()) {
                editTextSetDate.error = "Set Date"
                return
            }
            if (editTextSetTime.text!!.isEmpty()) {
                editTextSetTime.error = "Set Time"
                return
            }
        }

        if (timeValidation())
            saveIntoDatabase()
        else
            Toast.makeText(this, "You can not select previous time", Toast.LENGTH_SHORT).show()
    }

    private fun timeValidation(): Boolean {
        val date = myCalendar.time
        val currentDate = Calendar.getInstance().time
        if (date.before(currentDate))
            return false
        return true

    }

    private fun saveIntoDatabase() {

        CoroutineScope(Dispatchers.IO).launch {
            database.taskDao().insertTask(
                TaskModel(
                    binding.editTextTaskTitle.text.toString(),
                    binding.editTextTaskDescription.text.toString(),
                    binding.spinner.selectedItem.toString(),
                    myCalendar.time.time,
                )
            )
            startActivity(Intent(this@TaskActivity, MainActivity::class.java).apply {
                flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
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
        binding.apply {
            editTextSetDate.setText(TimeFormatter.dateFormat().format(myCalendar.time))
            textInputLayout3.visibility = View.VISIBLE
        }
    }
}