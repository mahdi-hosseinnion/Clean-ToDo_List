package com.example.clean_todo_list.framework.presentation.tasklist

import android.content.res.Resources
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.model.Task

//TODO FIX DIFF UTIL TO DO TASKS IN BACKGROUND
class TaskListAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return TaskListAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_task_list,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskListAdapterViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Task>) {
        differ.submitList(list)
    }

    class TaskListAdapterViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Task) = with(itemView) {

            val title_txt = itemView.findViewById<TextView>(R.id.title_txt)
            val isDone_checkBox = itemView.findViewById<CheckBox>(R.id.isDone_checkBox)

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            title_txt.text = item.title
            isDone_checkBox.isChecked = item.isDone

            if (item.isDone) {
                setTaskToDone(title_txt, resources)
            } else {
                setTaskToOnGoing(title_txt, resources)

            }

            itemView.findViewById<CheckBox>(R.id.isDone_checkBox)
                .setOnCheckedChangeListener { _, newIsDone ->
                    //TODO SOME SERIOUS BUG HERE
                    if (newIsDone != item.isDone) {
                        interaction?.onChangeIsDoneSelected(item.id, newIsDone, item.title)

                    }
                    if (newIsDone) {
                        setTaskToDone(title_txt, resources)
                    } else {
                        setTaskToOnGoing(title_txt, resources)

                    }
                }


        }

        fun setTaskToDone(txt: TextView, resources: Resources) {
            txt.setTextColor(resources.getColor(R.color.task_is_done_color))
            //draw line on text
            txt.paintFlags = txt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        fun setTaskToOnGoing(txt: TextView, resources: Resources) {
            txt.setTextColor(resources.getColor(R.color.task_is_not_done_color))
            //reset
            txt.paintFlags = TextView(itemView.context).paintFlags
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Task)
        fun onChangeIsDoneSelected(taskId: String, newIsDone: Boolean, title: String?)
    }
}
