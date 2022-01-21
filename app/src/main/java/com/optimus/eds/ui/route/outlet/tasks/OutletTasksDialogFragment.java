package com.optimus.eds.ui.route.outlet.tasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.optimus.eds.R;
import com.optimus.eds.db.entities.Task;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OutletTasksDialogFragment extends DialogFragment implements TaskSelectListener {
    public static final String OUTLET_ID = "dataKey";
    private TasksAdapter adapter;
    private TasksViewModel tasksViewModel;
    TextView noTask;
    public static OutletTasksDialogFragment newInstance(Long outletId) {
        OutletTasksDialogFragment frag = new OutletTasksDialogFragment();
        Bundle args = new Bundle();
        args.putLong(OUTLET_ID, outletId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Long outletId = getArguments().getLong(OUTLET_ID);
        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.view_tasks_layout, null);
        RecyclerView rvTasks = view.findViewById(R.id.rvTasks);
        Button mBtnClose = view.findViewById(R.id.btnClose);
        noTask= view.findViewById(R.id.tvNoTasks);
        mBtnClose.setOnClickListener((view1)->dismiss());

        setCancelable(false);
           adapter = new TasksAdapter(this);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        rvTasks.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        rvTasks.setAdapter(adapter);
        setObservers(outletId);

        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;

    }

    private void setObservers(Long outletId){
        tasksViewModel.getTasks(outletId).observe(this,tasks -> {
            adapter.populateTasks(tasks);
            noTask.setVisibility(tasks.isEmpty()?View.VISIBLE:View.GONE);
        });

        tasksViewModel.showMsg().observe(this,s -> {
            Toast.makeText(getContext(), s+"", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void OnTaskSelect(Task task) {
        tasksViewModel.updateTask(task);
    }
}
