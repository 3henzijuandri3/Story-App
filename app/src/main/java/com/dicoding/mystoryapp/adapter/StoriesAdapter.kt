package com.dicoding.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.CardStoryBinding
import com.dicoding.mystoryapp.ui.detail_activity.DetailActivity

class StoriesAdapter : PagingDataAdapter<CardStory, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    class ViewHolder(private val binding: CardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CardStory) {
            binding.userStoryName.text = data.namaUser
            binding.userStoryCreated.text = data.date

            Glide.with(binding.storyImage)
                .load(data.image)
                .into(binding.storyImage)

            binding.cardView.startAnimation(
                AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.scroll_animation
                )
            )

            binding.btnReadMore.setOnClickListener {

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyImage, "image"),
                        Pair(binding.userStoryName, "userName"),
                        Pair(binding.userStoryCreated, "date")
                    )

                val intentToDetail = Intent(itemView.context, DetailActivity::class.java)
                intentToDetail.putExtra(DetailActivity.ID, data.id)
                itemView.context.startActivity(intentToDetail, optionsCompat.toBundle())
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CardStory>() {
            override fun areItemsTheSame(oldItem: CardStory, newItem: CardStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CardStory, newItem: CardStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}