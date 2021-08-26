package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class ObserveTaskInCache(
    private val taskCacheDataSource: TaskCacheDataSource
) {
    private val _query = MutableStateFlow<String>("")
    private val _filterAndOrder = MutableStateFlow<SortAndOrder>(SortAndOrder.CREATED_DATE_DESC)
    private val _page = MutableStateFlow<Int>(1)

    fun execute(
        defaultQuery: String,
        defaultSortAndOrder: SortAndOrder,
        defaultPage: Int
    ): Flow<List<Task>> {
        //set default values to flows
        _query.value = defaultQuery
        _filterAndOrder.value = defaultSortAndOrder
        _page.value = defaultPage
        //combine 3 flow to TasksQueryRequirement (every time one of them change the combine return new TasksQueryRequirement)
        return combine(
            _query.debounce(QUERY_DEBOUNCE_TIME),
            _filterAndOrder,
            _page
        ) { query, filterAndOrder, page ->
            return@combine TasksQueryRequirement(query, filterAndOrder, page)
            //convert TasksQueryRequirement last value to Flow<List<Task>> from cache
        }.flatMapLatest { taskQueryReq ->
            return@flatMapLatest taskCacheDataSource.observeTasksInCache(
                taskQueryReq.query,
                taskQueryReq.sortAndOrder,
                taskQueryReq.page
            )
        }

    }

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setFilterAndOrder(sortAndOrder: SortAndOrder) {
        _filterAndOrder.value = sortAndOrder
    }

    fun setPage(page: Int) {
        _page.value = page
    }


    data class TasksQueryRequirement(
        val query: String,
        val sortAndOrder: SortAndOrder,
        val page: Int
    )

    companion object {
        private const val QUERY_DEBOUNCE_TIME: Long = 500
    }
}