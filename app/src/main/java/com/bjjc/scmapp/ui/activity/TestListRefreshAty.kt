package com.bjjc.scmapp.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.adapter.MyBaseAdapter
import com.bjjc.scmapp.ui.adapter.holder.MyBaseHolder
import com.bjjc.scmapp.util.UIUtils
import kotlinx.android.synthetic.main.activity_test_list_refresh.*
import org.jetbrains.anko.find

class TestListRefreshAty : AppCompatActivity() {
    lateinit var adapter:MyBaseAdapter<TestData>
    private var data1= ArrayList<TestData>()
    private var data2= ArrayList<TestData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list_refresh)

        btnIncrease.setOnClickListener {
            data1.clear()
            data1.addAll(data2)
            adapter.updateData(data2)
        }

        for(i in 0..10){
            val testData=TestData("item+$i")
            data1.add(testData)
        }
        for(i in 0..20){
            val testData=TestData("item+$i")
            data2.add(testData)
        }
        adapter = TestAdapter()
        adapter.setData(data1)
        lvListView.adapter=adapter
    }

    data class TestData(var text:String)

    inner class TestAdapter: MyBaseAdapter<TestData>() {
        override fun getHolder(): MyBaseHolder<TestData> {
            return TestHolder()
        }
        inner class TestHolder:MyBaseHolder<TestData>(){
            private val tvText:TextView by lazy { getRootView()!!.find<TextView>(R.id.tvText) }
            override fun initView(): View? {
                return UIUtils.inflate(R.layout.item_test_list_refresh)
            }

            override fun refreshView(data: TestData) {
                tvText.text=data.text
            }

        }
    }}
