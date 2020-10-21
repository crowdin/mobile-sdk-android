package com.crowdin.platform.example.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crowdin.platform.example.R
import com.crowdin.platform.example.utils.dialogAddCategory
import kotlinx.android.synthetic.main.fragment_category.*
import java.util.ArrayList

class CategoryFragment : Fragment(), View.OnClickListener, CategoryAdd, CategoryIsEmpty {

    private lateinit var categoryAdapter: CategoryAdapter
    private var categories: ArrayList<CategoryModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        addCategoryFAB.setOnClickListener(this)

        val dbManageCategory = DBManagerCategory(requireActivity())
        categories = dbManageCategory.getCategoryList()
        categoryAdapter = CategoryAdapter(requireActivity(), categories, this)
        recyclerView.adapter = categoryAdapter
    }

    override fun onClick(view: View?) {
        if (view == addCategoryFAB) {
            dialogAddCategory(requireActivity(), this)
        }
    }

    override fun onCategoryAdded() {
        val dbManageCategory = DBManagerCategory(requireActivity())
        categories = dbManageCategory.getCategoryList()

        categoryAdapter.clearAdapter()
        categoryAdapter.setList(categories)
    }

    override fun categoryIsEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }
}
