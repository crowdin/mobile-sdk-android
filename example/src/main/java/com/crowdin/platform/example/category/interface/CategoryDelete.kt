package com.crowdin.platform.example.category.`interface`

import com.crowdin.platform.example.category.model.CategoryModel

interface CategoryDelete {

    fun isCategoryDeleted(list: ArrayList<CategoryModel>)
}