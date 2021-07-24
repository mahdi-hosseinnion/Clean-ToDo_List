package com.example.clean_todo_list.framework.datasource.database

const val TASK_ORDER_ASC: String = ""
const val TASK_ORDER_DESC: String = "-"
const val TASK_FILTER_TITLE = "title"
const val TASK_FILTER_DATE_CREATED = "created_at"

const val ORDER_BY_ASC_DATE_UPDATED = TASK_ORDER_ASC + TASK_FILTER_DATE_CREATED
const val ORDER_BY_DESC_DATE_UPDATED = TASK_ORDER_DESC + TASK_FILTER_DATE_CREATED
const val ORDER_BY_ASC_TITLE = TASK_ORDER_ASC + TASK_FILTER_TITLE
const val ORDER_BY_DESC_TITLE = TASK_ORDER_DESC + TASK_FILTER_TITLE

const val TASK_PAGINATION_PAGE_SIZE = 30