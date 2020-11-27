package com.crowdin.platform.example.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.example.R
import com.crowdin.platform.example.utils.dialogDeleteCategory
import com.crowdin.platform.example.utils.dialogUpdateCategory
import kotlinx.android.synthetic.main.row_category.view.*

class CategoryAdapter(
    private val mContext: Context,
    private var mArrayList: ArrayList<CategoryModel>,
    categoryIsEmpty: CategoryIsEmpty
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var emptyStateCallback: CategoryIsEmpty = categoryIsEmpty

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mArrayList[position])
    }

    override fun getItemCount(): Int {
        if (mArrayList.size == 0) {
            emptyStateCallback.categoryIsEmpty(true)
        } else {
            emptyStateCallback.categoryIsEmpty(false)
        }
        return mArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_category, parent, false)
        )
    }

    fun clearAdapter() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    fun setList(mArrayList: ArrayList<CategoryModel>) {
        this.mArrayList = mArrayList
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), CategoryUpdate,
        CategoryDelete {

        private val categoryNameTv: TextView = view.txtCategoryName
        private val editCategoryImg: ImageView = view.imgEditCategory
        private val deleteCategoryImg: ImageView = view.imgDeleteCategory

        fun onBind(categoryModel: CategoryModel) {
            categoryNameTv.text = categoryModel.categoryName
            editCategoryImg.setOnClickListener {
                dialogUpdateCategory(it.context, categoryModel.id, this)
            }
            deleteCategoryImg.setOnClickListener {
                dialogDeleteCategory(it.context, categoryModel.id, this)
            }
        }

        override fun isCategoryUpdated(list: ArrayList<CategoryModel>) {
            clearAdapter()
            setList(list)
        }

        override fun isCategoryDeleted(list: ArrayList<CategoryModel>) {
            clearAdapter()
            setList(list)
        }
    }
}
