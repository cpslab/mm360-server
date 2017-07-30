package com.nshiba.entity

data class ProjectListItemData(var projectId: Int = -1,
                               var projectName: String = "",
                               var dataPath: String = "",
                               var pointVideoPathList: List<String> = listOf())
