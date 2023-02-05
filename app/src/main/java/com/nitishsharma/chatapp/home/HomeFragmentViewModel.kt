package com.nitishsharma.chatapp.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    fun createAndJoinRoom(
        socketIOInstance: Socket?,
        firebaseInstance: FirebaseAuth,
        roomName: String
    ): String {
        val roomId = generateUUID()
        val data = mapToJSON2(roomId, roomName)
        socketIOInstance?.emit("create-room", data)
        return joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    fun joinRoom(
        socketIOInstance: Socket?,
        roomId: String,
        firebaseInstance: FirebaseAuth
    ): String {
        val dataToSend = mapToJSON(roomId, firebaseInstance)
        socketIOInstance?.emit("join-room", dataToSend)

        return roomId
    }


    private fun mapToJSON(roomId: String, firebaseInstance: FirebaseAuth): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)
        jsonObject.put("userName", firebaseInstance.currentUser?.displayName)

        return jsonObject
    }

    private fun mapToJSON2(roomId: String, roomName: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)
        jsonObject.put("roomName", roomName)

        return jsonObject
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}