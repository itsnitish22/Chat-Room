package com.nitishsharma.chatapp.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.MainActivity
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.chats.ChatActivity
import com.nitishsharma.chatapp.databinding.FragmentHomeBinding
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeFragmentArgs: HomeFragmentArgs by navArgs()
    private val firebaseInstance = FirebaseAuth.getInstance()
    private val homeFragmentVM: HomeFragmentViewModel by viewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var roomId: String? = null
    var socketIOInstance: Socket? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        socketIOInstance = (activity as MainActivity).socketIOInstance

        //initializing the views
        initViews()

        //on click profile pic
        binding.profilePic.setOnClickListener {
            logOutAccount()
        }

        binding.createRoomButton.setOnClickListener {
            showJoinRoomBottomSheet("Create room", "Room's nick name", 1)
        }

        binding.joinRoomButton.setOnClickListener {
            showJoinRoomBottomSheet("Join room", "Enter room id", 2)
        }

        return binding.root
    }

    private fun showJoinRoomBottomSheet(buttonText: String, editTextHint: String, eventType: Int) {
        val view = layoutInflater.inflate(R.layout.join_room_bottom_sheet, null)
        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val button = view.findViewById<Button>(R.id.joinRoomButton)
        val enterEditText = view.findViewById<EditText>(R.id.enterRoomEditText)
        val editText = view.findViewById<TextInputLayout>(R.id.enterRoom)

        button.text = buttonText
        editText.hint = editTextHint


        if (eventType == 1) {
            button.setOnClickListener {
                if (enterEditText.text.toString().isNotEmpty()) {
                    bottomSheetDialog.dismiss()
                    startChatActivity(createAndJoinRoom(enterEditText.text.toString()))
                }
            }
        } else {
            button.setOnClickListener {
                if (enterEditText.text.toString().isNotEmpty()) {
                    roomId = enterEditText.text.toString()
                    bottomSheetDialog.dismiss()
                    roomId?.let {
                        startChatActivity(joinChatRoom(it))
                    }
                }
            }

        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun joinChatRoom(roomId: String): String {
        return homeFragmentVM.joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    private fun createAndJoinRoom(roomName: String): String {
        return homeFragmentVM.createAndJoinRoom(socketIOInstance, firebaseInstance, roomName)
    }


    //starting chat activity
    private fun startChatActivity(roomId: String) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("userName", firebaseInstance.currentUser?.displayName.toString())
        intent.putExtra("roomID", roomId)
        Log.i("ChatAct1", "${firebaseInstance.currentUser?.displayName}, $roomId")
        Handler().postDelayed({
            startActivity(intent)
        }, 3000)
    }

    //logging out
    private fun logOutAccount() {
        firebaseInstance.signOut()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
    }

    //initializing the views
    private fun initViews() {
        homeFragmentArgs.firebaseUser?.let {
            loadImageFromUrl(binding.profilePic, it.photoUrl)
        }
    }

    //loading image from url
    private fun loadImageFromUrl(profilePic: CircleImageView, photoUrl: Uri?) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()

        Glide.with(this).load(photoUrl).apply(options).into(profilePic)
    }
}