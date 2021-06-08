package com.example.recyclers

data class Field(
    var fieldId: Int,
    var hint: String,
    val fieldType: String,
    var Required: Boolean,
    var isActive: Boolean,
    var icon: String,
    var keyboard: String? = null
)