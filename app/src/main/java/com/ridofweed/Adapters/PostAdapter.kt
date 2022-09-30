package com.ridofweed.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ridofweed.Models.Post
import com.ridofweed.R

class PostAdapter(
    private val context: Context,
    private val posts: List<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        holder.bindView(posts[position], context)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var username = itemView.findViewById<TextView>(R.id.postUsername)
        private var imageView = itemView.findViewById<ImageView>(R.id.postImageView)
        private var title = itemView.findViewById<TextView>(R.id.postTitle)
        private var description = itemView.findViewById<TextView>(R.id.postDescription)

        fun bindView(post: Post, context: Context) {
            username.text = post.username
            title.text = post.title
            description.text = post.description
            Glide
                .with(context)
                .load(post.picture)
                .into(imageView)
        }
    }
}