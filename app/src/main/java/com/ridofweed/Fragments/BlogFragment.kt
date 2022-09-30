package com.ridofweed.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.ridofweed.Adapters.PostAdapter
import com.ridofweed.Models.Post
import com.ridofweed.R

class BlogFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var fbDatabase: DatabaseReference

    override fun onStart() {
        super.onStart()

        initView()
    }

    private fun initView() {
        view?.let {
            recyclerView = it.findViewById(R.id.rv_posts)
//            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)

            val posts = ArrayList<Post>()
            fbDatabase = FirebaseDatabase.getInstance().getReference("posts")
            fbDatabase.addValueEventListener(
                object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postsnap: DataSnapshot in snapshot.children) {
                            posts.add(postsnap.getValue(Post::class.java) as Post)
                        }
                        posts.reverse()
                        activity?.let { act ->
                            if (posts.isNotEmpty()) {
                                val no_posts = view?.findViewById<TextView>(R.id.no_posts)
                                no_posts?.visibility = View.INVISIBLE
                            }
                            val adapter = PostAdapter(act, posts)
                            recyclerView.adapter = adapter
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }
}