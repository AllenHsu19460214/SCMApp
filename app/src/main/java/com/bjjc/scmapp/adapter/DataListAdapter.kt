package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bjjc.scmapp.model.bean.CenterOutSendDetailMxBean
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity
import com.bjjc.scmapp.util.KeyBoardShowListener
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.view.CenterOutSendDetailView
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onFocusChange



/**
 * Created by Allen on 2018/12/13 11:24
 */
class DataListAdapter(val context: Context?, private val centerOutSendDetailView: CenterOutSendDetailView) :
    BaseAdapter(), View.OnTouchListener {
    //=====================================Field==================================================================
    private var selectedEditTextPosition: Int = -1
    private lateinit var data: ArrayList<CenterOutSendDetailMxBean>
    private var position: Int = -1
    private var orderNumber: String = ""

    //=====================================/Field=================================================================
    fun updateData(
        data: ArrayList<CenterOutSendDetailMxBean>,
        orderNumber: String
    ) {
        this.orderNumber = orderNumber
        this.data = data
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams", "SetTextI18n", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        this.position = position
        var reuseView = convertView
        val holder: DataListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context)
                .inflate(com.bjjc.scmapp.R.layout.layout_adpitem_center_out_send_data_list, null)
            holder = ViewHolder(reuseView)
            reuseView!!.tag = holder
        } else {
            holder = reuseView.tag as DataListAdapter.ViewHolder
        }

        holder.etNoCodeNum.setOnTouchListener(this)
        holder.etNoCodeNum.tag = position
        // Listening the state of the soft keyboard.
        KeyBoardShowListener(context!!).setKeyboardListener(object :
            KeyBoardShowListener.OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    //The soft keyboard has popped up
                    //Toast.makeText(context,"The soft keyboard has popped up",Toast.LENGTH_SHORT).show()
                } else {
                    //The soft keyboard does not pop up
                    //Toast.makeText(context,"The soft keyboard does not pop up",Toast.LENGTH_SHORT).show()
                    holder.etNoCodeNum.clearFocus()
                    SPUtils.putBean(context, "mxData$orderNumber", data)
                }
            }
        }, context as Activity)
        holder.tvOrderNumber.text = data[position].原始订单号
        holder.tvSpareNumber.text = data[position].备件编号
        holder.tvPlanBoxNum.text = data[position].计划箱数.toString()
        holder.tvScanCodeNum.text = data[position].出库箱数.toString()
        if (data[position].是否允许扫描 == 1) {
            //0 means allowed inputting and 1 means allowed scanning QR code ,These cases was at design time.
            // now,1 means allowed inputting and 0 means allowed scanning QR code.
            if (data[position].计划箱数 >= 0) {
                holder.llNoCodeNum.visibility = View.VISIBLE
                holder.etNoCodeNum.setText(data[position].出库输入箱数.toString())
                holder.etNoCodeNum.isFocusableInTouchMode = true
                holder.etNoCodeNum.isFocusable = true
                holder.etNoCodeNum.onFocusChange { v, hasFocus ->
                    val editText = v as EditText
                    if (hasFocus) {
                        editText.addTextChangedListener(mTextWatcher)
                    } else {
                        editText.removeTextChangedListener(mTextWatcher)
                        if (editText.text.toString() != "" && data.size > 0) {
                            if (data[position].出库输入箱数 > data[position].计划箱数) {
                                editText.setBackgroundColor(Color.parseColor("#FF0000"))
                                ToastUtils.showToastL(context, "无码数量不能大于该订单的计划箱数")
                                val charArray = data[position].允许输入箱数.toString().toCharArray()
                                val len = charArray.size
                                editText.setText(charArray, 0, len)
                                editText.setSelection(len)
                                //ToastUtils.showToastS(context,editText.selectionEnd.toString())
                            } else {
                                editText.addTextChangedListener(mTextWatcher)
                                editText.setBackgroundColor(Color.parseColor("#AAAAAA"))
                            }
                        }
                    }
                }
            } else {
                ToastUtils.showToastS(UIUtils.getContext(), "计划箱数不能小于0!")
                holder.etNoCodeNum.isFocusable = false
                holder.etNoCodeNum.isFocusableInTouchMode = false
                holder.etNoCodeNum.setBackgroundColor(Color.parseColor("#00000000"))
            }
        } else {
            if (data[position].允许输入箱数 > 0) {
                holder.llNoCodeNum.visibility = View.VISIBLE
                holder.etNoCodeNum.isFocusable = false
                holder.etNoCodeNum.isFocusableInTouchMode = false
                holder.etNoCodeNum.setBackgroundColor(Color.parseColor("#00000000"))
                holder.etNoCodeNum.setText(data[position].允许输入箱数.toString())
            } else {
                holder.llNoCodeNum.visibility = View.GONE
            }
        }

        var noCodeTotal: Long = 0
        for (value in data) {
            noCodeTotal += value.出库输入箱数.toLong()
        }
        val centerOutSendDetailActivity = centerOutSendDetailView as CenterOutSendDetailActivity
        centerOutSendDetailActivity.centerOutSendDetailPresenter.setNoCodeToTal(noCodeTotal)
        return reuseView
    }

    override fun getItem(position: Int): Any? {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    private val mTextWatcher = object : MyTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (selectedEditTextPosition != -1 && s?.length!! > 0) {
                data[selectedEditTextPosition].出库输入箱数 = s.toString().toInt()
            } else {
                //If the following statement is used,
                // it will cause corresponding EditText is set 0
                // to press backspace key until no number is present and exit the current activity.
               // data[selectedEditTextPosition].出库输入箱数 = 0
            }
        }

        override fun afterTextChanged(s: Editable?) {
            /* if (selectedEditTextPosition != -1 && s?.length!! > 0) {
                 data[selectedEditTextPosition].出库输入箱数 = s.toString().toInt()
             }*/
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

    inner class ViewHolder(private val reuseView: View) {
        val tvOrderNumber: TextView by lazy { reuseView.find<TextView>(com.bjjc.scmapp.R.id.tvOrderNumber) }
        val tvSpareNumber: TextView by lazy { reuseView.find<TextView>(com.bjjc.scmapp.R.id.tvSpareNumber) }
        val tvPlanBoxNum: TextView by lazy { reuseView.find<TextView>(com.bjjc.scmapp.R.id.tvPlanBoxNum) }
        val tvScanCodeNum: TextView by lazy { reuseView.find<TextView>(com.bjjc.scmapp.R.id.tvScanCodeNum) }
        val etNoCodeNum: EditText by lazy { reuseView.find<EditText>(com.bjjc.scmapp.R.id.etNoCodeNum) }
        val llNoCodeNum: LinearLayout by lazy { reuseView.find<LinearLayout>(com.bjjc.scmapp.R.id.llNoCodeNum) }

    }
}