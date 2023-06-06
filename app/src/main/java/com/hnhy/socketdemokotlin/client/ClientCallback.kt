package com.hnhy.socketdemokotlin.client

interface ClientCallback {
    //接收客户端的消息
    fun receiverServerMsg(ipAddress: String ,msg: String)
    //其他消息
    fun otherMsg(msg: String)
}