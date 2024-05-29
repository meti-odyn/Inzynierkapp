package com.example.inzynierkapp.notebook

data class Note (val id: Int, var title: String = "", var text: String = "",
                 val timestampLong: Long? = null, val timestampString : String? = null)
