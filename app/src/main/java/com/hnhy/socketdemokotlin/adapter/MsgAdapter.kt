package com.hnhy.socketdemokotlin.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hnhy.socketdemokotlin.bean.Message
import com.hnhy.socketdemokotlin.databinding.ItemRvMsgBinding

class MsgAdapter(private val message: ArrayList<Message>) :
    RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRvMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = message[position]
        if (message.isMyself) {
            holder.mView.tvMyselfMsg.text = message.msg
        } else {
            holder.mView.tvOtherMsg.text = message.msg
        }

        holder.mView.layOther.visibility = if (message.isMyself) View.GONE else View.VISIBLE
        holder.mView.layMyself.visibility = if (message.isMyself) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = message.size

    class ViewHolder(itemView: ItemRvMsgBinding) : RecyclerView.ViewHolder(itemView.root) {
        var mView: ItemRvMsgBinding
        init {
            mView = itemView
        }
    }
}