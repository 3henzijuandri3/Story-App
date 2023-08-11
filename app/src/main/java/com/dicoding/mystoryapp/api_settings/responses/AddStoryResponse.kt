package com.dicoding.mystoryapp.api_settings.responses

import com.google.gson.annotations.SerializedName

data class AddStoryResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)