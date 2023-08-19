package com.example.pdfuploader.models

import android.net.Uri
import java.net.URL

data class OnlineNotes(var name: String = "", var unit: String = "", var category: String = "")

data class Course(var courseName: String? = null, var onLineUnits: List<OnlineNotes>? = null)