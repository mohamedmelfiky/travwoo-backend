package com.melfiky.flights

import org.springframework.data.jdbc.core.JdbcAggregateTemplate

class WithInsertImpl<T : Any>(
    private val aggregateTemplate: JdbcAggregateTemplate
) : WithInsert<T> {

    override fun insert(entity: T): T {
        return aggregateTemplate.insert<T>(entity)
    }

    override fun insertAll(entityList: List<T>): List<T> {
        return aggregateTemplate.insertAll(entityList)
    }

}