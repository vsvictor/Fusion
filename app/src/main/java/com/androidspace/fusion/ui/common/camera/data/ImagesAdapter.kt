package com.androidspace.fusion.ui.common.camera.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.androidspace.fusion.R
import com.androidspace.fusion.data.model.camera.ImageData
import com.androidspace.fusion.data.model.camera.MediaTypeData
import com.androidspace.fusion.databinding.GalleryItemAddBinding
import com.androidspace.fusion.databinding.GalleryItemImageBinding
import com.androidspace.fusion.databinding.GalleryItemVideoBinding
import com.androidspace.fusion.utils.toTime
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


typealias OnSelect = (item: ImageData?) -> Unit
typealias OnView = (item: ImageData?) -> Unit
typealias OnAdd = () -> Unit

class ImagesAdapter(private val images: List<ImageData>, private val scoupe: CoroutineScope, val isAdd: Boolean = false, val clear: Boolean = false): RecyclerView.Adapter<ImagesAdapter.GalleryViewHolder>() {
    private val TAG = ImagesAdapter::class.java.simpleName
    var onSelect: OnSelect? = null
    var onView: OnView? = null
    var onAdd: OnAdd? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when(viewType){
            2 -> {
                val binding = DataBindingUtil.inflate<GalleryItemAddBinding>(inflater, R.layout.gallery_item_add, parent, false)
                return AddViewHolder(binding)
            }
            1 -> {
                val binding = DataBindingUtil.inflate<GalleryItemVideoBinding>(inflater, R.layout.gallery_item_video, parent, false)
                return VideoViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<GalleryItemImageBinding>(inflater, R.layout.gallery_item_image, parent, false)
                return ImageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val pos = if(isAdd) position-1 else position
        val im = if(position > 0) images.get(pos) else null
        holder.bind(im, onSelect, onView, onAdd)

    }

    override fun getItemCount(): Int {
        return if(isAdd) images.size+1 else images.size
    }

    override fun getItemViewType(position: Int): Int {
        val pos = if(isAdd) position-1 else position
        if(position == 0 && isAdd){
            return 2
        }else {
            return if (images.get(pos).type == MediaTypeData.VIDEO) 1 else 0
        }
    }
    abstract class GalleryViewHolder(open val binding: ViewBinding): RecyclerView.ViewHolder(binding.root){
        abstract fun bind(item: ImageData?, sel: OnSelect? = null, cl:OnView? = null, add: OnAdd? = null)
    }
    inner class VideoViewHolder(override val binding: GalleryItemVideoBinding): GalleryViewHolder(binding){
        override fun bind(item: ImageData?, sel: OnSelect?, cl:OnView?, add: OnAdd?) {
            binding.prBar.isVisible = true
            val cr = binding.root.context.contentResolver
            //scoupe.launch {
                val options = BitmapFactory.Options()
                val k = if (item?.type == MediaTypeData.VIDEO) MediaStore.Video.Thumbnails.MICRO_KIND else MediaStore.Images.Thumbnails.MICRO_KIND
                val th = MediaStore.Video.Thumbnails.getThumbnail(cr, item?.id?:0, k, options)
                binding.llSelected.isVisible = item?.selected?:false
            //withContext(Dispatchers.Main) {

                    binding.prBar.isVisible = false
                    binding.ivPhoto.setImageBitmap(th)
                    binding.tvTiming.setText(item?.timing?.toTime())
                //}
            //}
            binding.root.setOnClickListener{
                cl?.invoke(item)
            }
            binding.root.setOnLongClickListener {
                item?.let {
                    it.selected = !it.selected
                    binding.llSelected.isVisible = it.selected
                    //sel?.invoke(it)
                }
                true
            }
        }
    }
    inner class ImageViewHolder(override val binding: GalleryItemImageBinding): GalleryViewHolder(binding){
        override fun bind(item: ImageData?, sel: OnSelect?, cl:OnView?, add: OnAdd?){
            //scoupe.launch {
                item?.let {
                    binding.llSelected.isVisible = it.selected
                    Glide.with(binding.root.context).load(it.uri).centerCrop().listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.prBar.isVisible = false
                            binding.ivPhoto.setImageDrawable(resource)
                            return true
                        }
                    }).into(binding.ivPhoto)
                }
            //}
            binding.root.setOnClickListener{
                cl?.invoke(item)
            }
            binding.root.setOnLongClickListener {
                item?.let {
                    it.selected = !it.selected
                    binding.llSelected.isVisible = it.selected
                    //sel?.invoke(it)
                }
                true
            }
        }
    }
    inner class AddViewHolder(override val binding: GalleryItemAddBinding): GalleryViewHolder(binding){
        override fun bind(item: ImageData?, sel: OnSelect?, cl:OnView?, add: OnAdd?){
            binding.root.setOnClickListener{
                add?.invoke()
            }
        }
    }
}