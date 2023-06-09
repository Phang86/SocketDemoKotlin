package com.hnhy.socketdemokotlin.client

import android.renderscript.ScriptGroup.Input
import android.util.Log
import com.hnhy.socketdemokotlin.server.SocketServer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object SocketClient {
    private val TAG = SocketClient::class.java.simpleName

    private var socket: Socket? = null

    private var outputStream: OutputStream? = null

    private lateinit var mCallback: ClientCallback

    private var inputStreamReader: InputStreamReader? = null

    private const val SOCKET_PORT = 9527

    private var clientThreadPool: ExecutorService? = null

    /**
     * 连接服务
     */
    fun connectServer(ipAddress: String, callback: ClientCallback){
        mCallback = callback
        Thread{
            try {
                socket = Socket(ipAddress, SOCKET_PORT)
                Log.e(TAG, "客户端：ipAddress$ipAddress")
                ClientThread(socket!!, mCallback).start()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 关闭连接
     */
    fun closeConnect(){
        inputStreamReader?.close()
        outputStream?.close()
        socket?.close()

        //关闭线程池
        clientThreadPool?.shutdownNow()
        clientThreadPool = null
    }

    /**
     * 发送消息到服务器
     * @param msg 要发送到服务器的字符串
     */
    fun sendToServer(msg: String){
        if (clientThreadPool == null) {
            clientThreadPool = Executors.newSingleThreadExecutor()
        }
        clientThreadPool?.execute{
            if (socket == null){
                mCallback.otherMsg("客户端还未连接")
                return@execute
            }
            if (socket!!.isClosed) {
                Log.e(TAG, "Socket已关闭")
                return@execute
            }
            outputStream = socket?.getOutputStream()
            try {
                outputStream?.write(msg.toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "向服务端发送消息：$msg 失败")
            }
        }
    }

    class ClientThread(private val socket: Socket, private val callback: ClientCallback): Thread(){
        override fun run() {
            val inputStream: InputStream?
            try {
                inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                var len: Int
                var receiverStr = ""
                if (inputStream.available() == 0){
                    Log.e(TAG, "Client：inputStream.available() == 0")
                }
                while (inputStream.read(buffer).also { len = it } != -1) {
                    receiverStr += String(buffer, 0, len, Charsets.UTF_8)
                    if (len < 1024){
                        socket.inetAddress.hostAddress?.let {
                            callback.receiverServerMsg(it, receiverStr)
                        }
                        receiverStr = ""
                    }
                }
            }catch (e: IOException){
                e.printStackTrace()
                e.message?.let { Log.e("socket error", it) }
            }
        }
    }
}