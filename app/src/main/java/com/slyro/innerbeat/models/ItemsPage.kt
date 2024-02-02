package com.slyro.innerbeat.models

import com.slyro.innertube.models.YTItem

data class ItemsPage(
    val items: List<YTItem>,
    val continuation: String?,
)
