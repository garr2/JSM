package com.garr.pavelbobrovko.notsimplechat.presentation.utils

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.garr.pavelbobrovko.notsimplechat.R


@BindingAdapter("src")
//@JvmStatic
fun setImageUrl(view: ImageView, url: String) {
    val glideOptions: RequestOptions = RequestOptions()
            .error(R.drawable.no_image)
            .centerInside()
            .circleCrop()
    Glide.with(view.context).load(url).apply(glideOptions).into(view)
}

@BindingAdapter("visibility")
fun View.visibility(visibility: Boolean) {
    this.visibility = if(visibility) View.VISIBLE else View.GONE
}