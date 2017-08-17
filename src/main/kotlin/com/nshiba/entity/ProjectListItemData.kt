package com.nshiba.entity

data class ProjectListItemData(var projectId: Int = -1,
                               var projectName: String = "",
                               var dataPath: String = "",
                               var pointVideoPathList: List<PointVideoPathListItem> = listOf())

data class PointVideoPathListItem(var id: Int, var path: String)
