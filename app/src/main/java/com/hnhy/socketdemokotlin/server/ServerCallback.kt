package com.hnhy.socketdemokotlin.server

interface ServerCallback {
    //接收客户端的消息
    fun receiverClientMsg(ipAddress: String, msg: String)
    //其他消息
    fun otherMsg(msg: String)
}