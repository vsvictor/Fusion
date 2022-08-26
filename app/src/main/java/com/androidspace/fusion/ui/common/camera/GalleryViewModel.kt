package com.androidspace.fusion.ui.common.camera

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.camera.ImageData
import com.androidspace.fusion.data.model.camera.MediaTypeData
import com.androidspace.fusion.domain.UseCaseScaleBitmap
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.common.camera.data.*
import com.androidspace.fusion.utils.rotateBitmap
import com.androidspace.fusion.utils.toByteArray
import com.androidspace.fusion.utils.toFileName
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class GalleryViewModel(override val app: Application, private val useCaseScaleBitmap: UseCaseScaleBitmap) : BaseViewModel<Object>(app){
    private val TAG = GalleryViewModel::class.java.simpleName
    var imageList = ImageDataListObservable(ArrayList<ImageData>())
    private var onFragmentChange: OnFragmentChange? = null
    private var dest: OnBitmapDestinatation? = null
    var onProgress: OnProgress? = null
    var backID = -1
    var isAdd = false
    var isMultipeSelect = false
    var onCamera: OnCamera? = null
    var args: Bundle? = null
    private var onLoaderShow: OnLoaderShow? = null
    var onBack: OnBack? = null
    fun startLoad(){
        imageList.images = loadUris()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(imageList.images.size > 0){
            val i = 0
        }
        if(backID > 0){
            args?.let {
                navController?.navigate(backID, args)
            }?: kotlin.run {
                navController?.navigate(backID)
            }
        }else {
            navController?.popBackStack()
        }
    }
    private fun loadUris():List<ImageData>{
        onProgress?.onProgerssVisible(true)
        val resultImages = ArrayList<ImageData>()
        val resultVideos = ArrayList<ImageData>()
        val collectionImages = MediaStore.Files.getContentUri("external");

        val projectionImages = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        )
        val selectionImages = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="+ MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        val sortOrderImages = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val queryImages = app.applicationContext.contentResolver.query(collectionImages, projectionImages, selectionImages, null, sortOrderImages)
        queryImages?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val date = Date(cursor.getLong(dateColumn))
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                resultImages.add(ImageData(id, contentUri, name, 0, MediaTypeData.PHOTO, date, 0))
            }
        }
        val collectionVideos = MediaStore.Files.getContentUri("external");
        val projectionVideos = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DURATION
        )
        val selectionVideos = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        val sortOrderVideos = "${MediaStore.Video.Media.DATE_TAKEN} DESC"
        val queryVideos = app.applicationContext.contentResolver.query(collectionVideos, projectionVideos, selectionVideos, null, sortOrderVideos)
        queryVideos?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
            val durationColumns = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val date = Date(it.getLong(dateColumn))
                val duration = it.getLong(durationColumns)
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                Log.d(TAG, contentUri.toString())
                //val b = app.contentResolver.loadThumbnail(contentUri, Size(64,64), CancellationSignal())
                resultVideos.add(ImageData(id, contentUri, name, 0, MediaTypeData.VIDEO, date, duration))
            }
        }
        val result = ArrayList<ImageData>()
        result.addAll(resultVideos)
        result.addAll(resultImages)
        val r = result.sortedByDescending { it.created }
        onProgress?.onProgerssVisible(false)
        return r
    }

    fun onAdd(){
        onCamera?.onShowCamera(true)
    }
    fun onImageLoad(image: ImageData?) {
        image?.let {
            if (it.type == MediaTypeData.PHOTO) {
                Picasso.get().load(it.uri)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            bitmap?.let { b ->
                                var newBitmap: Bitmap? = null
                                val file = it.uri.toFileName(app.applicationContext)
                                file?.let {
                                    val exif = ExifInterface(it)
                                    val rotate = exif.rotationDegrees
                                    newBitmap = b.rotateBitmap(rotate)
                                    val i = 0
                                } ?: kotlin.run {
                                    newBitmap = bitmap
                                }

                                dest?.let {
                                    onLoaderShow?.omLoaderShow(false)
                                    newBitmap?.let { bm ->
                                        it.onDestinatation(bm)
                                    }
                                    //onBackPressed()
                                    onFragmentChange?.onFragmentClose()
                                    dest = null
                                } ?: kotlin.run {
                                    val coef = 800.toFloat() / (newBitmap!!.width.toFloat())
                                    useCaseScaleBitmap.execute(viewModelScope,
                                        UseCaseScaleBitmap.Param(newBitmap!!, coef),
                                        success = {
                                            val arr = it.toByteArray()
                                            val bundle = Bundle().apply {
                                                putByteArray("picture", arr)
                                                putInt("back", backID)
                                            }
                                            onLoaderShow?.omLoaderShow(false)
                                            toCropper(bundle)
                                        },
                                        fail = {
                                            error.postValue(it)
                                        }
                                    )
                                }
                            }
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            onLoaderShow?.omLoaderShow(true)
                        }
                    })
            }
            else{
                dest?.let {
                    onLoaderShow?.omLoaderShow(false)

                    val cr = app.contentResolver
                    viewModelScope.launch {
                        val options = BitmapFactory.Options()
                        val th = MediaStore.Video.Thumbnails.getThumbnail(cr, image.id, MediaStore.Video.Thumbnails.MICRO_KIND, options)
                        withContext(Dispatchers.Main) {
                            th?.let { bm ->
                                it.onDestinatation(bm, image.uri)
                            }
                        }
                    }
                    onFragmentChange?.onFragmentClose()
                    dest = null
                }
            }
        }

    }
    fun onMediaLoad(media: ImageData?){
/*        media?.let {
            val fr = if(it.type == MediaTypeData.PHOTO){
                AlphaPhotoFragment()
            }else{
                AlphaVideoFragment()
            }
            fr.arguments = Bundle().apply {
                putString("url", it.uri.toString())
            }
            onFragmentChange?.onFragmentOpen(fr)
        }*/
    }
    @MainThread
    private fun toCropper(bundle: Bundle){
        //navController?.navigate(R.id.cropFragment, bundle)
    }

    fun setOnDestinatationListener(listener: OnBitmapDestinatation?){
        this.dest = listener
    }
    fun setOnFragmentCloseListener(listener: OnFragmentChange){
        this.onFragmentChange = listener
    }
    fun setOnLoaderShowListener(listener: OnLoaderShow){
        this.onLoaderShow = listener
    }
    fun onBack(view: View){
        for(item in imageList.images){
            item.selected = false
        }
        onBack?.onBack()
    }
    fun onAdd(view: View){
        onBack?.onBack(true)
    }
}