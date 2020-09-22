package com.sabo.dominik.ontheroadalarm.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sabo.dominik.ontheroadalarm.clickinterfaces.AlarmClickInterface
import com.sabo.dominik.ontheroadalarm.viewholders.AlarmViewHolder
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.models.Alarm


class AlarmRecyclerAdapter(private val clickInterface: AlarmClickInterface) :
    RecyclerView.Adapter<AlarmViewHolder>() {
    private val dataList: MutableList<Alarm> = ArrayList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlarmViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.alarm_recycler_view, viewGroup, false)
        return AlarmViewHolder(view, clickInterface)
    }

    override fun onBindViewHolder(alarmViewHolder: AlarmViewHolder, position: Int) {
        alarmViewHolder.setInfo(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(data: List<Alarm>?) {
        dataList.clear()
        dataList.addAll(data!!)
        notifyDataSetChanged()
    }

    fun addItem(data: Alarm) {
        val position = dataList.size
        dataList.add(position, data)
        notifyItemInserted(position)
    }

    fun changeItem(data: Alarm, position: Int) {
        dataList[position] = data
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

}
