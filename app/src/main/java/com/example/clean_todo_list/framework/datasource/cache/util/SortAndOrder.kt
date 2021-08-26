package com.example.clean_todo_list.framework.datasource.cache.util

enum class SortAndOrder {
    CREATED_DATE_DESC,
    CREATED_DATE_ASC,
    NAME_DESC,
    NAME_ACS
}

val APP_DEFAULT_SORT = SortAndOrder.CREATED_DATE_DESC