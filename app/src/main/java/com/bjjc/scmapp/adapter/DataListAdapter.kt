package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.ui.fragment.DataListFragment
import org.jetbrains.anko.find


/**
 * Created by Allen on 2018/12/13 11:24
 */
class DataListAdapter(private val context: FragmentActivity) : BaseAdapter() {
    private var data: List<CenterOutSendDetailBean>? = null
    private val mData: ArrayList<HashMap<String, String>>? = ArrayList()// 存储的EditText值
    private var editorValue: HashMap<String, String> = HashMap()
    //定义成员变量mTouchItemPosition,用来记录手指触摸的EditText的位置
    private var mTouchItemPosition = -1
    private var iOnUpdateCountTotalListener:IOnUpdateCountTotalListener? =null
    companion object {
        var noCodeCount: String = "0"
        var noCodeList: ArrayList<Long> = ArrayList()
    }
    fun setOnUpdateCountTotalListener(iOnUpdateCountTotalListener:IOnUpdateCountTotalListener){
        this.iOnUpdateCountTotalListener=iOnUpdateCountTotalListener
    }
    fun setData(data: List<CenterOutSendDetailBean>?) {
        data?.let {
            this.data = data
            Log.i("aaaa", data.toString())
        }
        mData?.clear()
        data?.forEach { _ ->
            editorValue = hashMapOf("list_item_inputValue" to "0")
            mData?.add(editorValue)
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: DataListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context)
                .inflate(R.layout.adapter_item_center_distribution_order_output_data_listview, null)
            viewHolder = ViewHolder(reuseView, position)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as DataListAdapter.ViewHolder
            viewHolder.updatePosition(position)
        }
        viewHolder.tvOrderNumber.text=data?.get(position)?.原始订单号
        viewHolder.tvSpareNumber.text = data?.get(position)?.备件编号
        viewHolder.tvPlanBoxCount.text = data?.get(position)?.计划箱数.toString()
        if (DataListFragment.i!=0){
            viewHolder.tvScanCount.text = (data?.get(position)?.出库箱数?.plus(DataListFragment.i)).toString()
        }else{
            viewHolder.tvScanCount.text = data?.get(position)?.出库箱数.toString()
        }
        if (data?.get(position)?.是否允许扫描 != 0){
            viewHolder.llNoCodeCount.visibility=View.GONE
        }else{
            viewHolder.llNoCodeCount.visibility=View.VISIBLE
            //viewHolder.etNoCodeCount.setText(mData?.get(position)?.get("list_item_inputValue"))
            viewHolder.etNoCodeCount.setText(data?.get(position)?.允许输入箱数.toString())
        }
        if (mTouchItemPosition == position) {
            viewHolder.etNoCodeCount.requestFocus()
            viewHolder.etNoCodeCount.setSelection(viewHolder.etNoCodeCount.text.length)
        } else {
            viewHolder.etNoCodeCount.clearFocus()
        }
        return reuseView
    }

    override fun getItem(position: Int): Any? {
        return data?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data?.size ?: 0
    }

    fun getIndexInList(sparePartsNumber:String):Int{
        for ((index,value)in data!!.withIndex()){
            if (sparePartsNumber==value.备件编号){
                return index
            }
        }
        return 0
    }
    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val reuseView: View, position: Int) {
        val tvOrderNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvOrderNumber) }
        val tvSpareNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvSpareNumber) }
        val tvPlanBoxCount: TextView by lazy { reuseView.find<TextView>(R.id.tvPlanBoxTotal) }
        val tvScanCount: TextView by lazy { reuseView.find<TextView>(R.id.tvScanCount) }
        val etNoCodeCount: EditText by lazy { reuseView.find<EditText>(R.id.etNoCodeCount) }
        val llNoCodeCount: LinearLayout by lazy { reuseView.find<LinearLayout>(R.id.llNoCodeCount) }
        private val myTextWatcher: MyTextWatcher by lazy { MyTextWatcher(this) }

        init {
            etNoCodeCount.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    mTouchItemPosition = v.tag as Int
                }
                false
            }
            etNoCodeCount.tag = position
            etNoCodeCount.addTextChangedListener(myTextWatcher)
            updatePosition(position)
        }

        fun updatePosition(position: Int) {
            myTextWatcher.updatePosition(position)
        }

        inner class MyTextWatcher(private val mHolder: ViewHolder) : TextWatcher {
            private var mPos: Int = 0
            fun updatePosition(position: Int) {
                mPos = position
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                // 当EditText数据发生改变的时候存到data变量中
                mData?.get(mPos)?.put("list_item_inputValue", s.toString())
                //ToastUtils.showShortToast(context, mData.toString())
                iOnUpdateCountTotalListener?.onUpdateCountTotal(mData)
            }
        }
    }
    interface IOnUpdateCountTotalListener{
        fun onUpdateCountTotal(mData: ArrayList<HashMap<String, String>>?)
    }
}