package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.adapter.CardStory
import com.dicoding.mystoryapp.api_settings.responses.ListStoryItem

object DataDummy {

    fun generateDummyStory(): List<CardStory> {
        val items : MutableList<CardStory> = arrayListOf()

        for (i in 0..100) {
            val story = CardStory(
                "image $i",
                "nama $i",
                "tanggal $i",
                i.toString()
            )
            items.add(story)
        }

        return items
    }

    fun generateDummy(storiesResponse : List<ListStoryItem>): List<CardStory> {

        val items : MutableList<CardStory> = arrayListOf()

        for(storyData in storiesResponse){
            val cardStory = CardStory(
                storyData.photoUrl.toString(),
                storyData.name.toString(),
                storyData.createdAt.toString(),
                storyData.id.toString()
            )

            items.add(cardStory)
        }

        return items
    }

}