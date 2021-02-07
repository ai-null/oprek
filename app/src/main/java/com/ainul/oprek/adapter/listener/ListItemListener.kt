package com.ainul.oprek.adapter.listener

import com.ainul.oprek.database.entities.Project

interface  ListItemListener {
    fun onClick(project: Project)
}