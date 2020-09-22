package com.sabo.dominik.ontheroadalarm.viewholders

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.sabo.dominik.ontheroadalarm.clickinterfaces.AlarmClickInterface
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.models.Alarm

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
        if(alarm.activationDistance < 1000){
            tvDistance.text = alarm.activationDistance.toString() + "m"
        }
        else{
            if(alarm.activationDistance%1000 == 0){
                tvDistance.text = (alarm.activationDistance/1000).toString() + "km"
            }
            else{
                tvDistance.text = (alarm.activationDistance/1000).toString() + "." + (alarm.activationDistance%1000/100).toString() + "km"
            }
        }
        swSwitch.isChecked = alarm.isActive
    }

    init {
        rvLayout.setOnClickListener(this)
        swSwitch.setOnClickListener(this)
    }
}