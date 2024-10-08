package com.example.hide_images.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hide_images.display_image
import com.example.hide_images.R
import java.io.File

class Image_Adapter(private val context: Context, private val images: Array<File>?) :
    RecyclerView.Adapter<Image_Adapter.ImageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageView = images?.get(position)

        holder.imageView.setOnClickListener {
            val intent = Intent(context, display_image::class.java)
            val uri = images?.get(position)?.toUri()
            intent.putExtra("image", uri)
            holder.itemView.context.startActivity(intent)
            notifyDataSetChanged()
        }
        Glide.with(holder.imageView.context).load(imageView).into(holder.imageView)
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)

    }
}