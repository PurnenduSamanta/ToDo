package com.purnendu.todo

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.purnendu.todo.database.TaskModel


class Adapter(private val list: MutableList<TaskModel>) :
    RecyclerView.Adapter<Adapter.MyViewHolder>() {

    private val colorArray = arrayOf(
        "#ef5777", "#575fcf", "#4bcffa", "#34e7e4", "#0be881",
        "#ffc048", "#d2dae2", "#485460", "#B33771", "#55E6C1"
    )

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskSubTitle: TextView = itemView.findViewById(R.id.taskSubTitle)
        val category: TextView = itemView.findViewById(R.id.category)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutInflater:LayoutInflater = LayoutInflater.from(parent.context)
        val binding : ViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.single_task_layout, parent, false)
        return  MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        list[position].apply outer@{

            holder.apply {
                this.taskTitle.text = this@outer.title
                this.taskSubTitle.text = this@outer.description
                this.category.text = this@outer.category
                setBorderColor(this)
                date.text = TimeFormatter.dateFormat().format(this@outer.time)
                time.text = TimeFormatter.timeFormat().format(this@outer.time)
            }
        }


    }

    private fun setBorderColor(viewHolder: MyViewHolder) {
        val drawable = viewHolder.constraintLayout.background as InsetDrawable
        val shape = drawable.drawable as GradientDrawable
        val randomInteger = (0..9).random()
        shape.setStroke(convertDpToPx(), Color.parseColor(colorArray[randomInteger]))
    }

    private fun convertDpToPx(dp: Int = 3): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<TaskModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getItemCount() = list.size
}