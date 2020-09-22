package com.sabo.dominik.ontheroadalarm.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.dominik.ontheroadalarm.clickinterfaces.FavouriteClickInterface
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace

class FavouriteViewHolder(itemView: View, private val clickInterface: FavouriteClickInterface) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val rvLayout: RelativeLayout = itemView.findViewById(R.id.rvFavLayout)
    private val ivImg: ImageView = itemView.findViewById(R.id.ivFavImg)
    private val tvName: TextView = itemView.findViewById(R.id.tvFavName)
    private val tvDescription: TextView = itemView.findViewById(R.id.tvFavDescription)

    fun setInfo(favPlace: FavouritePlace){
        ivImg.setImageBitmap(favPlace.picture)
        tvName.text = favPlace.name
        tvDescription.text = favPlace.description
    }

    init {
        rvLayout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        clickInterface.onFavouritePlaceClick(adapterPosition)
    }
}