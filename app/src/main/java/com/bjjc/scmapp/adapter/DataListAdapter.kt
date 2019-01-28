package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
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
import com.bjjc.scmapp.util.KeyBoardShowListener
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.view.CenterOutSendDetailView
import org.jetbrains.anko.find


/**
 * Created by Allen on 2018/12/13 11:24
 */
class DataListAdapter(val context: Context?, private val centerOutSendDetailView: CenterOutSendDetailView) :
    BaseAdapter(), View.OnTouchListener, View.OnFocusChangeListener {
    private var selectedEditTextPosition: Int = -1
    private val data: MutableList<CenterOutSendDetailBean> by lazy { ArrayList<CenterOutSendDetailBean>() }
    private val dataChanged: MutableList<CenterOutSendDetailBean> by lazy { ArrayList<CenterOutSendDetailBean>() }
    private val noCodeDataMap: MutableMap<String, String> by lazy { HashMap<String, String>() }
    private var position: Int = -1
    private var orderNumber:String=""
    companion object {
        var noCodeCount: String = "0"
    }

    fun updateData(
        data: MutableList<CenterOutSendDetailBean>,
        orderNumber: String
    ) {
        this.orderNumber = orderNumber
        this.data.clear()
        this.data.addAll(data)
        dataChanged.clear()
        dataChanged.addAll(data)
        for (index in 0 until data.size) {
            if (data[index].允许输入箱数 != 0) {
                dataChanged[index].出库输入箱数 = data[index].允许输入箱数
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams", "SetTextI18n", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        this.position = position
        var reuseView = convertView
        val viewHolder: DataListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context)
                .inflate(R.layout.layout_adpitem_center_out_send_data_list, null)
            viewHolder = ViewHolder(reuseView)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as DataListAdapter.ViewHolder
        }

        viewHolder.etNoCodeNum.setOnTouchListener(this)
        viewHolder.etNoCodeNum.onFocusChangeListener = this
        viewHolder.etNoCodeNum.tag = position
        /*   if (selectedEditTextPosition != -1 && position == selectedEditTextPosition) {
               // 保证每个时刻只有一个EditText能获取到焦点
               viewHolder.etNoCodeNum.requestFocus()
           } else {
               viewHolder.etNoCodeNum.clearFocus()
           }*/
        //监听软键盘的状态
        KeyBoardShowListener(context!!).setKeyboardListener(object :
            KeyBoardShowListener.OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    //软键盘已弹出
                    //Toast.makeText(context,"软键盘已弹出",Toast.LENGTH_SHORT).show()
                } else {
                    //软键盘未弹出
                    //Toast.makeText(context,"软键盘未弹出",Toast.LENGTH_SHORT).show()
                    viewHolder.etNoCodeNum.clearFocus()
                    SPUtils.putBean(context, "dataChanged$orderNumber",dataChanged)
                }
            }
        }, context as Activity)
        viewHolder.tvOrderNumber.text = data[position].原始订单号
        viewHolder.tvSpareNumber.text = data[position].备件编号
        viewHolder.tvPlanBoxNum.text = data[position].计划箱数.toString()
        viewHolder.tvScanCodeNum.text = data[position].出库箱数.toString()
        if (dataChanged[position].是否允许扫描 == 0) {//0 means:input;1 means:scan.
            viewHolder.llNoCodeNum.visibility = View.VISIBLE
            if (dataChanged[position].允许输入箱数 == 0) {
                viewHolder.etNoCodeNum.isFocusableInTouchMode = true
                viewHolder.etNoCodeNum.isFocusable = true
                viewHolder.etNoCodeNum.setText((dataChanged[position].允许输入箱数 + dataChanged[position].出库输入箱数).toString())
            } else {
                viewHolder.etNoCodeNum.isFocusable = false
                viewHolder.etNoCodeNum.isFocusableInTouchMode = false
                viewHolder.etNoCodeNum.setText(dataChanged[position].出库输入箱数.toString())
            }
        } else {
            viewHolder.llNoCodeNum.visibility = View.GONE
        }

        var noCodeTotal: Long = 0
        for (value in dataChanged) {
            noCodeTotal += value.出库输入箱数.toLong()
        }
        centerOutSendDetailView.setNoCodeText(noCodeTotal)

        return reuseView
    }

    override fun getItem(position: Int): Any? {
        return dataChanged[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataChanged.size
    }

    private val mTextWatcher = object : MyTextWatcher() {
        /*  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              if (selectedEditTextPosition != -1 && s?.length!! > 0) {
                  dataChanged[selectedEditTextPosition].出库输入箱数 = s.toString().toInt()
              }
          }*/
        override fun afterTextChanged(s: Editable?) {
            if (selectedEditTextPosition != -1 && s?.length!! > 0) {
                dataChanged[selectedEditTextPosition].出库输入箱数 = s.toString().toInt()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val editText = v as EditText
            selectedEditTextPosition = editText.tag as Int
        }
        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        val editText = v as EditText
        if (hasFocus) {
            editText.addTextChangedListener(mTextWatcher)
        } else {
            editText.removeTextChangedListener(mTextWatcher)
        }

    }


    inner class ViewHolder(private val reuseView: View) {
        val tvOrderNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvOrderNumber) }
        val tvSpareNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvSpareNumber) }
        val tvPlanBoxNum: TextView by lazy { reuseView.find<TextView>(R.id.tvPlanBoxNum) }
        val tvScanCodeNum: TextView by lazy { reuseView.find<TextView>(R.id.tvScanCodeNum) }
        val etNoCodeNum: EditText by lazy { reuseView.find<EditText>(R.id.etNoCodeNum) }
        val llNoCodeNum: LinearLayout by lazy { reuseView.find<LinearLayout>(R.id.llNoCodeNum) }

    }
}