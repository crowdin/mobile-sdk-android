package com.crowdin.platform.example.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.example.R
import com.crowdin.platform.example.utils.dialogAddCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class CategoryFragment : Fragment(), View.OnClickListener, CategoryAdd, CategoryIsEmpty {

    private lateinit var addCategoryFAB: FloatingActionButton
    private lateinit var emptyView: TextView
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
        addCategoryFAB = view.findViewById(R.id.addCategoryFAB)
        emptyView = view.findViewById(R.id.emptyView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
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
