package com.optimus.eds.ui.route.outlet.tasks;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.optimus.eds.R;
import com.optimus.eds.db.entities.Task;
import com.optimus.eds.utils.Util;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksListItemView extends LinearLayout {


    @BindView(R.id.task)
    public TextView task;

    @BindView(R.id.task_date)
    public TextView taskDate;

    @BindView(R.id.task_status)
    public CheckBox taskStatus;

    @BindView(R.id.completed_date)
    public TextView completedDate;



    public TasksListItemView(Context context) {
        super(context);
    }

    public TasksListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TasksListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this,this);
    }

    public void setTask(Task task,TaskSelectListener listener) {
        this.task.setText(task.getTaskName());
        this.taskStatus.setChecked(task.getStatus());
        this.taskDate.setText(task.getTaskDate());
        this.completedDate.setText(task.getTaskDate());
        taskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setStatus(isChecked);
            if(isChecked)
                task.setCompletedDate(Util.formatDate(Util.DATE_FORMAT_4,new Date().getTime()));
            else
                task.setCompletedDate("");
            listener.OnTaskSelect(task);
        });
    }
}
