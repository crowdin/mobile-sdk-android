package com.crowdin.platform.example.category.`interface`

import com.crowdin.platform.example.category.model.CategoryModel

interface CategoryUpdate {

    fun isCategoryUpdated(list: ArrayList<CategoryModel>)
}