package com.example.pdfuploader.models

import android.net.Uri
import java.net.URL

data class OnlineNotes(var name: String? = null, var unit: String? = null, var category: String? = null)

data class Course(var courseName: String? = null, var onLineUnits: List<OnlineNotes>? = null)