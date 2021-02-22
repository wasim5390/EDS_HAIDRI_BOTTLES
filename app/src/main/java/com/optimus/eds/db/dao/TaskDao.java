package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task where outletId=:outletId")
    LiveData<List<Task>> getTaskByOutletId(Long outletId);

    @Update()
     void updateTask(Task task);

    @Insert(onConflict = REPLACE)
    void insertTasks(List<Task> tasks);

    @Query("DELETE FROM Task")
    void deleteAllTask();
}
