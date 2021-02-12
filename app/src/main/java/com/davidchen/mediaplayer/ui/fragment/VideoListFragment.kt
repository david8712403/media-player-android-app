package com.davidchen.mediaplayer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidchen.mediaplayer.BuildConfig
import com.davidchen.mediaplayer.R
import com.davidchen.mediaplayer.VideoAdapter
import com.davidchen.mediaplayer.data.RawVideoList
import com.davidchen.mediaplayer.databinding.FragmentVideoListBinding
import com.davidchen.mediaplayer.util.AlertDialogUtil
import com.davidchen.mediaplayer.util.ProgressDialogUtil
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [VideoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoListFragment : Fragment() {

    lateinit var v: FragmentVideoListBinding

    var rawVideoList: RawVideoList? = null
    lateinit var adapter: VideoAdapter
    var keyword: String? = "movie"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        sendVideoListRequest()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_list, container, false)
        v = FragmentVideoListBinding.bind(view)

        val linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        v.rvVideo.layoutManager = linearLayoutManager

        v.rvVideo.layoutManager = linearLayoutManager
        v.rvVideo.addItemDecoration(
            DividerItemDecoration(v.rvVideo.context, DividerItemDecoration.VERTICAL)
        )

        v.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(tv: TextView, actionId: Int, event: KeyEvent?): Boolean {
                val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.etSearch.windowToken, 0)
                if (actionId == EditorInfo.IME_ACTION_SEARCH && tv.text.isNotEmpty()) {
                    keyword = tv.text.toString()
                    sendVideoListRequest()
                    return true
                }
                return false
            }

        })

        if (rawVideoList == null) {
            sendVideoListRequest()
        }else {
            adapter = VideoAdapter(rawVideoList!!.result.list)
            adapter.callback = object : VideoAdapter.Callback {
                override fun onClick(videoId: String) {
                    val f = VideoPlayerFragment.newInstance(videoId)
                    parentFragmentManager.beginTransaction()
                        .addToBackStack(videoId)
                        .replace(R.id.root, f)
                        .commit()
                }
            }
            v.rvVideo.adapter = adapter
        }

        return v.root
    }

    private fun sendVideoListRequest() {
        ProgressDialogUtil.showProgressDialog(requireContext(), "loading videos...")
        val guestKey = BuildConfig.italkutalk_guest_key

        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.italkutalk.com")
            .addPathSegment("api")
            .addPathSegment("video")
            .addPathSegment("list")

        val body = JSONObject()
            .put("guestKey", guestKey)
            .put("keyword", keyword)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(httpUrl.build())
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                AlertDialogUtil.showAlertDialog(requireContext(), e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    rawVideoList = Gson().fromJson(it.string(), RawVideoList::class.java)
                    activity!!.runOnUiThread {
                        adapter = VideoAdapter(rawVideoList!!.result.list)
                        adapter.callback = object : VideoAdapter.Callback {
                            override fun onClick(videoId: String) {
                                val f = VideoPlayerFragment.newInstance(videoId)
                                parentFragmentManager.beginTransaction()
                                    .addToBackStack(videoId)
                                    .replace(R.id.root, f)
                                    .commit()
                            }
                        }
                        v.rvVideo.adapter = adapter
                    }
                }
                ProgressDialogUtil.dismiss()
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VideoListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}