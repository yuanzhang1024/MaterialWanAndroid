package com.show.wanandroid.ui.project.fragment

import android.os.Bundle
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.show.wanandroid.R
import com.show.wanandroid.const.Id
import com.show.wanandroid.databinding.FragmentProjectArticleBinding
import com.show.wanandroid.entity.CateBean
import com.show.wanandroid.ui.main.vm.MainViewModel
import com.show.wanandroid.ui.project.adapter.ProjectAdapter
import kotlinx.android.synthetic.main.fragment_project_article.*
import showmethe.github.core.base.LazyFragment
import showmethe.github.core.divider.RecycleViewDivider
import showmethe.github.core.http.coroutines.Result
import showmethe.github.core.util.extras.ObList
import showmethe.github.core.util.extras.plus
import showmethe.github.core.util.extras.set
import showmethe.github.core.util.extras.valueSameAs

/**
 *  com.show.wanandroid.ui.project
 *  2020/3/23
 *  23:41
 */
class ProjectArticleFragment : LazyFragment<FragmentProjectArticleBinding, MainViewModel>() {

    companion object {

        fun get(id: Int): ProjectArticleFragment {
            val bundle = Bundle()
            bundle.putInt(Id, id)
            val fragment =
                ProjectArticleFragment()
            fragment.arguments = bundle
            return fragment
        }

    }


    private lateinit var adapter: ProjectAdapter
    private var cid = 0
    private val pagerNumber = MutableLiveData<Int>()
    private val list = ObList<CateBean.DatasBean>()
    private val cate = MutableLiveData<Result<CateBean>>()

    override fun initViewModel(): MainViewModel = createViewModel()

    override fun getViewId(): Int = R.layout.fragment_project_article

    override fun onBundle(bundle: Bundle) {
        cid = bundle.getInt(Id, 0)
    }

    override fun observerUI() {

        pagerNumber.observe(this, Observer {
            it?.apply {
                if (cid != -1) {
                    router.toTarget("getCate", this, cid, cate)
                } else {
                    refresh.isRefreshing = false
                }
            }
        })

        cate.observe(this, Observer {
            it?.apply {
                when (status) {
                    Result.Success -> {
                        if (pagerNumber valueSameAs 0) {
                            list.clear()
                        }
                        response?.apply {
                            list.addAll(this.datas)
                            if (list.size != 0) {
                                smrl.showContent()
                            } else {
                                smrl.showEmpty()
                            }
                            onSize(this.datas.size)
                        }
                    }
                }
            }
        })

    }

    override fun init() {
        smrl.setDefaultLoadingColorRes(R.color.colorAccent)
        refresh.setColorSchemeResources(R.color.colorAccent)


        initAdapter()

        pagerNumber set 0
    }

    override fun initListener() {

        smrl.setOnReloadWhenErrorOrEmpty {
            pagerNumber set 0
        }


        refresh.setOnRefreshListener {

            pagerNumber set 0
        }


        rv.setOnLoadMoreListener {
            pagerNumber plus 1
        }


    }

    private fun initAdapter() {
        adapter = ProjectAdapter(context,list)
        rv.adapter = adapter
        rv.layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
    }


    private fun onSize(size: Int) {
        rv.finishLoading()
        refresh.isRefreshing = false
        if (size == 0) {
            rv.setEnableLoadMore(false)
        } else {
            rv.setEnableLoadMore(true)
        }
    }

}