package com.hnhy.socketdemokotlin.server

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object SocketServer {
    private val TAG = SocketServer::class.java.simpleName

    private const val SOCKET_PORT = 9527

    private var socket: Socket? = null

    private var socketServer: ServerSocket? = null

    private lateinit var mCallback: ServerCallback

    private lateinit var outputStream: OutputStream

    var result = true

    private var serverThreadPool: ExecutorService? = null

    /**
     * 开启服务
     */
    fun startServer(callback: ServerCallback): Boolean {
        mCallback = callback
        Thread {
            try {
                socketServer = ServerSocket(SOCKET_PORT)
                while (result) {
                    socket = socketServer?.accept()
                    mCallback.otherMsg("${socket?.inetAddress} to connected")
                    ServerThread(socket!!, mCallback).start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                result = false
            }
        }.start()
        return result
    }

    /**
     * 关闭服务
     */
    fun stopServer() {
        socket?.apply {
            shutdownInput()
            shutdownOutput()
            close()
        }
        socketServer?.close()

        //关闭线程池
        serverThreadPool?.shutdownNow()
        serverThreadPool = null
    }

    /**
     * 发送消息到客户端
     */
    fun sendToClient(msg: String) {
        if (serverThreadPool == null){
            serverThreadPool = Executors.newCachedThreadPool()
        }
        serverThreadPool?.execute{
            if (socket == null) {
                mCallback.otherMsg("客户端还未连接")
                return@execute
            }
            if (socket!!.isClosed){
                mCallback.otherMsg("Socket已关闭")
                return@execute
            }
            outputStream = socket!!.getOutputStream()
            try {
                outputStream.write(msg.toByteArray())
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "向客户端发送消息：$msg 失败")
            }
        }
    }

    // 代码从上往下看，首先是初始化一些变量，然后就是startServer()函数，
    // 在这里进行回调接口的初始化然后开一个子线程进行ServerSocket的构建，
    // 构建成功之后会监听连接，得到一个socket，这个socket就是客户端，
    // 这里将连接客户端的地址显示出来。然后再开启一个子线程去处理客户端发送过来的消息。
    // 这个地方服务端和客户端差不多，下面看ServerThread中的代码。Socket通讯，
    // 发送和接收对应的是输入流和输入流，通过socket.getInputStream()得到输入流，
    // 获取字节数据然后转成String，通过接口回调，最后重置变量。关闭服务就没好说的，
    // 代码一目了然。最后就是发送到客户端的sendToClient()函数。接收发送字符串，
    // 开启子线程，获取输出流，写入字节数据然后刷新，最后回调到页面。
    class ServerThread(private val socket: Socket, private val callback: ServerCallback) :
        Thread() {
        override fun run() {
            val inputStream: InputStream?
            try {
                inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                var len: Int
                var receiverStr = ""
                if (inputStream.available() == 0) {
                    Log.e(TAG, "Server_inputStream.available() == 0")
                }
                while (inputStream.read(buffer).also { len = it } != -1) {
                    receiverStr += String(buffer, 0, len, Charsets.UTF_8)
                    if (len < 1024) {
                        socket.inetAddress.hostAddress?.let {
                            callback.receiverClientMsg(it, receiverStr)
                        }
                        receiverStr = ""
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let { Log.e("socket error", it) }
            }
        }
    }
}