package com.optimus.eds.ui.route.outlet.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.optimus.eds.R;
import com.optimus.eds.db.entities.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TasksAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Task> tasks;
    private TaskSelectListener listener;

    public TasksAdapter(TaskSelectListener mListener) {
        this.tasks = new ArrayList<>();
        this.listener =mListener;
    }

    public void populateTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public TaskListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_task_list_item, parent, false);
        return new TaskListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Task task = tasks.get(position);
        ((TasksListItemView)holder.itemView).setTask(task,listener);

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskListHolder extends RecyclerView.ViewHolder {
        TaskListHolder(View itemView) {
            super(itemView);
        }

    }

}
