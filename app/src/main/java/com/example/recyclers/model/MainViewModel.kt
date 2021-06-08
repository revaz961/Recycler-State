package com.example.recyclers.model

import android.app.Application
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class MainViewModel(var app:Application) : AndroidViewModel(app) {
    private var fields = mutableListOf<MutableList<Field>>()
    val mainLiveData = MutableLiveData<MutableList<MutableList<Field>>>().apply {
        mutableListOf<MutableList<Field>>()
    }

    fun init() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                initLiveData()
            }
        }
    }


    private suspend fun initLiveData() {
        parseJson()
        mainLiveData.postValue(fields)
    }

    fun getFields(): MutableList<Field>{
        var fieldList = mutableListOf<Field>()
        fields.forEach {
            it.forEach { field ->
                fieldList.add(field)
            }
        }
        return fieldList
    }

    private suspend fun parseJson(){
        var jsonStr = getJson("fields.json")
        try {
            var jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                var arrJson = jsonArray.getJSONArray(i)
                fields.add(mutableListOf<Field>())
                for (j in 0 until arrJson.length()) {
                    val jsonObj = arrJson.getJSONObject(j)
                    fields[i].add(
                        Field(
                            jsonObj.getInt("field_id"),
                            jsonObj.getString("hint"),
                            jsonObj.getString("field_type"),
                            jsonObj.getBoolean("required"),
                            jsonObj.getBoolean("is_active"),
                            jsonObj.getString("icon"),
                            if (jsonObj.has("keyboard"))
                                jsonObj.getString("keyboard")
                            else null
                        )
                    )
                }
            }
        } catch (e: JSONException) {
            d("errorMessage", e.message.toString())
        }
    }

    private fun getJson(fileName:String):String{
        var inputStream = app.assets.open(fileName)
        var json = inputStream.bufferedReader().readText()
        inputStream.close()
        return json
    }
}