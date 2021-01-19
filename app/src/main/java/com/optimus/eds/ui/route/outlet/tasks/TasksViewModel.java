package com.optimus.eds.ui.route.outlet.tasks;

import android.app.Application;

import com.optimus.eds.db.entities.Task;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TasksViewModel extends AndroidViewModel {
   private OutletDetailRepository repository;
    private LiveData<String> showMsg;
    public TasksViewModel(@NonNull Application application) {
        super(application);
        repository = new OutletDetailRepository(application);
        showMsg = repository.showMsg();
    }

    public LiveData<List<Task>> getTasks(Long outletId){
        return repository.getTasksByOutletId(outletId);
    }

    public void updateTask(Task task){
         repository.updateTask(task);
    }


    public LiveData<String> showMsg() {
        return showMsg;
    }
}
