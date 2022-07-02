package com.example.patientquestions

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonParser
    @Inject constructor(
) {
    fun loadJson(context: Context): ResourceBundleJson {
        val jsonFileString = getJsonDataFromAsset(context, "patient.json")
        val resourceBundleType = object : TypeToken<ResourceBundleJson>() {}.type
        return Gson().fromJson(jsonFileString, resourceBundleType)
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}

data class ResourceBundleJson(
    val resourceType: String,
    val entry: List<EntryJson>
    )

data class EntryJson(val resource: ResourceJson)

data class ResourceJson(
    val resourceType: String,
    val id: String,
    val status: String? = null,
    val type: List<TypeJson>? = null,
    val name: List<NameJson>? = null,
    val code: ParentCodeJson? = null,
    val subject: ReferenceJson? = null,
    val actor: ReferenceJson? = null,
    val appointment: ReferenceJson? = null
    )

data class NameJson(
    val family: String,
    val given: List<String>,
    val text: String? = null,
)

data class ParentCodeJson(
    val coding: List<CodeJson>
)

data class CodeJson(
    val system: String,
    val code: String,
    val name: String
)

data class TypeJson(
    val text: String
)

data class ReferenceJson(
    val reference: String
)