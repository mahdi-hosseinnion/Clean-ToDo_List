package com.example.clean_todo_list.framework.datasource.cache.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clean_todo_list.framework.datasource.cache.model.TaskCacheEntity
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder.*

@Dao
interface TaskDao {

    //insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(entity: TaskCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(entities: List<TaskCacheEntity>): LongArray

    //delete queries

    @Query("""DELETE FROM tasks WHERE id = :primaryKey """)
    suspend fun deleteTask(primaryKey: String): Int

    @Query("""DELETE FROM tasks WHERE id IN (:primaryKeys) """)
    suspend fun deleteTasks(primaryKeys: List<String>): Int

    //update queries

    @Query(
        """UPDATE tasks SET 
        title = :newTitle, 
        body = :newBody, 
        isDone = :newIsDone, 
        updated_at = :updated_at 
        WHERE id = :primaryKey 
    """
    )
    suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean,
        updated_at: Long
    ): Int

    @Query(
        """
        UPDATE tasks SET 
        isDone = :isDone, 
        updated_at = :updated_at 
        WHERE id = :primaryKey
    """
    )
    suspend fun updateIsDone(primaryKey: String, isDone: Boolean, updated_at: Long): Int

    //get queries
    @Query("""SELECT * FROM tasks """)
    suspend fun getAllTasks(): List<TaskCacheEntity>

    @Query("""SELECT * FROM tasks WHERE id = :primaryKey """)
    suspend fun getTaskById(primaryKey: String): TaskCacheEntity?

    //search queries

    @Query(
        """
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY updated_at DESC LIMIT (:page * :pageSize)
    """
    )
    suspend fun searchTasksOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<TaskCacheEntity>

    @Query(
        """
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY updated_at ASC LIMIT (:page * :pageSize)
    """
    )
    suspend fun searchTasksOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<TaskCacheEntity>

    @Query(
        """
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY title DESC LIMIT (:page * :pageSize)
    """
    )
    suspend fun searchTasksOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<TaskCacheEntity>

    @Query(
        """
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY title ASC LIMIT (:page * :pageSize)
    """
    )
    suspend fun searchTasksOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<TaskCacheEntity>

    //other queries
    @Query("""SELECT COUNT(*) FROM tasks """)
    suspend fun getNumOfTasks(): Int


    companion object {
        const val TASK_PAGINATION_PAGE_SIZE = 30
    }
}

suspend fun TaskDao.returnOrderedQuery(
    query: String,
    filterAndOrder: FilterAndOrder,
    page: Int,
    pageSize: Int = TaskDao.TASK_PAGINATION_PAGE_SIZE
): List<TaskCacheEntity> = when (filterAndOrder) {

    DATE_DESC -> {
        searchTasksOrderByDateDESC(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
    DATE_ASC -> {
        searchTasksOrderByDateASC(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
    TITLE_DESC -> {
        searchTasksOrderByTitleDESC(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
    TITLE_ACS -> {
        searchTasksOrderByTitleASC(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
}
