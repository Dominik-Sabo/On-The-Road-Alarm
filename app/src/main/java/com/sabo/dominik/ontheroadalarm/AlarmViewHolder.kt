package com.sabo.dominik.ontheroadalarm

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class AlarmViewHolder(itemView: View, private val clickInterface: AlarmClickInterface) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val rvLayout: RelativeLayout = itemView.findViewById(R.id.rvLayout)
    private val tvName: TextView = itemView.findViewById(R.id.tvName)
    private val swSwitch: SwitchCompat = itemView.findViewById(R.id.swSwitch)
    private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)

    override fun onClick(view: View?) {
        if (view === rvLayout) {
            clickInterface.onAlarmClick(adapterPosition)
        } else clickInterface.onSwitchClick(adapterPosition)
    }

    fun setInfo(alarm: Alarm){
        tvName.text = alarm.name
        tvDistance.text = alarm.activationDistance.toString() + "km"
        swSwitch.isChecked = alarm.isActive
    }

    init {
        rvLayout.setOnClickListener(this)
        swSwitch.setOnClickListener(this)
    }
}