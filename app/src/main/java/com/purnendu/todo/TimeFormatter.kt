package com.purnendu.todo

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {

         fun dateFormat():SimpleDateFormat {
            val myFormat = "EEE, d MMM yyyy"
            val local = Locale("English")
            return  SimpleDateFormat(myFormat, local)
        }

         fun timeFormat() :SimpleDateFormat{
             val myFormat = "h:mm a"
             val local = Locale("English")
             return SimpleDateFormat(myFormat, local)
        }
}