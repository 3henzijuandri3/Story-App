package com.dicoding.mystoryapp.adapter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stories")
data class CardStory(
    @field:SerializedName("image") var image: String,

    @field:SerializedName("username") var namaUser: String,

    @field:SerializedName("date") var date: String,

    @PrimaryKey @field:SerializedName("id") var id: String
)