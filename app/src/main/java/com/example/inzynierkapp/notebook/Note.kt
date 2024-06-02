package com.example.inzynierkapp.notebook
import java.util.Date

data class Note (val id: Int,
                 var title: String = "",
                 var text: String = "",
                 val creationDate: Date = Date(),
                 var imagePath: String? = null,
                 val timestampLong: Long? = null,
                 val timestampString : String? = null)
