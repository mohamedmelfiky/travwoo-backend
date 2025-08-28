package com.melfiky.flights

interface WithInsert<T> {

    fun insert(entity: T): T
    fun insertAll(entityList: List<T>): List<T>
}