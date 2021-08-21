package com.example.clean_todo_list.framework.presentation.tasklist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
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

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            itemView.findViewById<TextView>(R.id.title_txt).text = item.title
            itemView.findViewById<CheckBox>(R.id.isDone_checkBox).isChecked = item.isDone

            itemView.findViewById<CheckBox>(R.id.isDone_checkBox).setOnCheckedChangeListener { _, newIsDone ->
                //TODO SOME SERIOUS BUG HERE
                if (newIsDone != item.isDone) {
                    interaction?.onChangeIsDoneSelected(item.id, newIsDone)
                }
            }


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Task)
        fun onChangeIsDoneSelected(taskId: String, newIsDone: Boolean)
    }
}
