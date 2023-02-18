package com.nitishsharma.chatapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.BuildConfig
import com.nitishsharma.chatapp.models.chatresponse.parseMessage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object Utility {
    const val SERVER_PATH = "wss://chatappbackendws.azurewebsites.net/"
    const val SERVER_PATH_PIESOCKET_TEST =
        "wss://${BuildConfig.PIESOCKET_CLUSTER_ID}.piesocket.com/v3/${BuildConfig.PIESOCKET_ROOM_ID}?api_key=${BuildConfig.PIESOCKET_API_KEY}&notify_self=1"

    fun chatMessageResponseMapping(args: Array<Any>?): JSONObject {
        val receivedMessageFromServer =
            parseMessage(JSONArray(Gson().toJson(args))[0].toString())

        val mappedData = JSONObject()
        mappedData.put("userName", receivedMessageFromServer.userName)
        mappedData.put("message", receivedMessageFromServer.message)
        mappedData.put("roomId", receivedMessageFromServer.roomId)
        mappedData.put("isSent", false)

        return mappedData
    }

    fun joinRoomJSONMapping(roomId: String, firebaseInstance: FirebaseAuth): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)
        jsonObject.put("userName", firebaseInstance.currentUser?.displayName)

        return jsonObject
    }

    fun createRoomJSONMapping(userId: String, roomId: String, roomName: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("roomId", roomId)
        jsonObject.put("roomName", roomName)

        return jsonObject
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}