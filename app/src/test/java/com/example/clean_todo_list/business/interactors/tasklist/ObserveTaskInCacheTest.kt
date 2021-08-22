package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * test cases:
 *  1)blankQuery_success_confirmAllTaskInCacheReturned()
 *      a) observe tasks with empty query
 *      b) confirm all tasks returned
 *      d) insert random task
 *      e) confirm flow changed
 *  2)randomQuery_success_confirmCorrectQueryReturned()
 *      a) observe tasks with empty query
 *      b) add random query
 *      c) confirm returned result contains query
 *  3)changeFilter_success_confirmFilterAndOrderChange()
 *      a) observe tasks with empty query
 *      b) change filterAndOrder
 *      c) confirm filter and order changed
 *  4)changePage_success_confirmPageChanged()
 *      a) insert 100 random query
 *      b) observe tasks with empty query(and page 1)
 *      c) confirm 30 tasks returned (default value for pageCount)
 *      d) change page to 2
 *      e) confirm 60 tasks returned
 *  5)changeAllVariable_success_confirmAllChanged()
 *      a) insert 100 random tasks
 *      b) observe tasks with empty query
 *      c) confirm only 30 returned
 *      d) change filter
 *      e) change query
 *      f) change page
 *      g) confirm correct results returned
 *
 */
@ExperimentalCoroutinesApi
class ObserveTaskInCacheTest {

    //system under test
    private lateinit var observeTaskInCache: ObserveTaskInCache

    // dependencies
    private val dependencyContainer = DependencyContainer()

    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource


    @BeforeEach
    fun initSystemUnderTest() {
        observeTaskInCache = ObserveTaskInCache(taskCacheDataSource)
    }

    @Test
    fun blankQuery_success_confirmAllTaskInCacheReturned() = runBlocking {

        val finalFilter = FilterAndOrder.DATE_DESC

        val flowResult: ArrayList<List<Task>> = ArrayList()

        val job = launch {
            observeTaskInCache.execute(
                "", finalFilter, 1
            ).collect {
                flowResult.add(it)
            }
        }
        delay(100)
        job.cancelAndJoin()

        //confirm result returned
        val returnedTasks = flowResult[1]
        val returnedFilterAndOrder = flowResult[0][0].title

        printLogD(
            "blankQuery_success_confirmAllTaskInCacheReturned",
            "returnedTasks: ${returnedTasks.size}"
        )
        printLogD(
            "blankQuery_success_confirmAllTaskInCacheReturned",
            "returnedFilterAndOrder: $returnedFilterAndOrder"
        )
        //the first value that  FakeTaskCacheDataSourceImpl emits is filter and order in title
        assertEquals(
            finalFilter.name,
            returnedFilterAndOrder
        )
        assertTrue {
            returnedTasks.size > 5
        }
    }

    @Test
    fun randomQuery_success_confirmCorrectQueryReturned() = runBlocking {
        val query = "Mountain"
        val finalFilter = FilterAndOrder.DATE_DESC

        val flowResult: ArrayList<List<Task>> = ArrayList()

        val job = launch {
            observeTaskInCache.execute(
                "", finalFilter, 1
            ).collect {
                flowResult.add(it)
            }
        }
        delay(200)
        observeTaskInCache.setQuery(query)
        delay(200)
        job.cancelAndJoin()

        //confirm result returned
        val returnedTasks = flowResult[3]
        val returnedFilterAndOrder = flowResult[0][0].title

        //the first value that  FakeTaskCacheDataSourceImpl emits is filter and order in title
        assertEquals(
            finalFilter.name,
            returnedFilterAndOrder
        )
        assertTrue {
            returnedTasks[0].title.contains(query)
        }

    }

    @Test
    fun changeFilter_success_confirmFilterAndOrderChange() = runBlocking {

        val finalFilter = FilterAndOrder.DATE_DESC

        val flowResult: ArrayList<List<Task>> = ArrayList()

        val job = launch {
            observeTaskInCache.execute(
                "", FilterAndOrder.TITLE_ACS, 1
            ).collect {
                flowResult.add(it)
            }
        }
        delay(200)
        observeTaskInCache.setFilterAndOrder(finalFilter)
        delay(200)
        job.cancelAndJoin()

        //confirm result returned
        val returnedTasks = flowResult[3]
        val firstOrder = flowResult[0][0].title
        val secondOrder = flowResult[2][0].title

        //the first value that  FakeTaskCacheDataSourceImpl emits is filter and order in title
        assertEquals(
            FilterAndOrder.TITLE_ACS.name,
            firstOrder
        )
        assertEquals(
            finalFilter.name,
            secondOrder
        )
        //something actually returned
        assertTrue {
            returnedTasks.size > 5
        }

    }

    @Test
    fun changePage_success_confirmPageChanged() = runBlocking {
        //insert some fake data
        val count = 100
        taskCacheDataSource.insertTasks(TaskFactory.createListOfRandomTasks(count))
        //confirm actually inserted (false positive)
        val countOfTasksInCache = taskCacheDataSource.getNumOfTasks()
        assertTrue { countOfTasksInCache >= count }
        //actual test
        val flowResult: ArrayList<List<Task>> = ArrayList()

        val job = launch {
            observeTaskInCache.execute(
                "", FilterAndOrder.DATE_DESC, 1
            ).collect {
                flowResult.add(it)
            }
        }
        delay(100)
        observeTaskInCache.setPage(2)
        delay(100)
        job.cancelAndJoin()

        //confirm result returned
        val tasksBeforeUpdatePage = flowResult[1]
        val tasksAfterUpdatePage = flowResult[3]

        assertTrue {
            tasksBeforeUpdatePage.size == TaskDao.TASK_PAGINATION_PAGE_SIZE
        }
        assertTrue {
            tasksAfterUpdatePage.size == (TaskDao.TASK_PAGINATION_PAGE_SIZE).times(2)
        }
    }

    @Test
    fun changeAllVariable_success_confirmAllChanged() = runBlocking {
        //insert some fake data
        val count = 1_000
        taskCacheDataSource.insertTasks(TaskFactory.createListOfRandomTasks(count))
        //confirm actually inserted (false positive)
        val countOfTasksInCache = taskCacheDataSource.getNumOfTasks()
        assertTrue { countOfTasksInCache >= count }
        //actual test
        val newFilter = FilterAndOrder.TITLE_ACS
        val newPage = 3
        val newQuery = "a"

        val flowResult: ArrayList<List<Task>> = ArrayList()

        val job = launch {
            observeTaskInCache.execute(
                "", FilterAndOrder.DATE_DESC, 1
            ).collect {
                flowResult.add(it)
            }
        }
        delay(100)
        observeTaskInCache.setPage(newPage)
        observeTaskInCache.setFilterAndOrder(newFilter)
        observeTaskInCache.setQuery(newQuery)
        delay(100)
        job.cancelAndJoin()

        //confirm result returned
        val tasksBeforeUpdate = flowResult[1]
        val returnedFilter = flowResult[2]
        val tasksAfterUpdate = flowResult[3]
        assertEquals(
            newFilter.name,
            returnedFilter[0].title
        )
        assertTrue {
            tasksBeforeUpdate.size == TaskDao.TASK_PAGINATION_PAGE_SIZE
        }
        assertTrue {
            tasksAfterUpdate.size == (TaskDao.TASK_PAGINATION_PAGE_SIZE).times(newPage)
        }
        assertTrue {
            tasksAfterUpdate[0].title.contains(newQuery) ||
                    tasksAfterUpdate[0].body.contains(newQuery)
        }
    }
}