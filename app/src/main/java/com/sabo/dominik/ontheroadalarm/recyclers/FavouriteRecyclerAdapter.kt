package com.sabo.dominik.ontheroadalarm.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sabo.dominik.ontheroadalarm.clickinterfaces.FavouriteClickInterface
import com.sabo.dominik.ontheroadalarm.viewholders.FavouriteViewHolder
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace

class FavouriteRecyclerAdapter(private val clickInterface: FavouriteClickInterface) :
    RecyclerView.Adapter<FavouriteViewHolder>() {
    private val dataList: MutableList<FavouritePlace> = ArrayList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.favourite_recycler_view, viewGroup, false)
        return FavouriteViewHolder(view, clickInterface)
    }

    override fun onBindViewHolder(favViewHolder: FavouriteViewHolder, position: Int) {
        favViewHolder.setInfo(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(data: List<FavouritePlace>?) {
        dataList.clear()
        dataList.addAll(data!!)
        notifyDataSetChanged()
    }

    fun addItem(data: FavouritePlace) {
        val position = dataList.size
        dataList.add(position, data)
        notifyItemInserted(position)
    }

    fun changeItem(data: FavouritePlace, position: Int) {
        dataList[position] = data
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

}
