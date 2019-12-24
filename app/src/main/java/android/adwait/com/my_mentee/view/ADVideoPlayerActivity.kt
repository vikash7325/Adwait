package android.adwait.com.my_mentee.view

import android.adwait.com.R
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.MediaController
import android.widget.VideoView


class ADVideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advideo_player)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        if (intent != null && intent.hasExtra("videoUrl")) {
            val videoView = findViewById(R.id.videoView) as VideoView
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            val uri =
                Uri.parse(intent.getStringExtra("videoUrl"))
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(uri)
            videoView.requestFocus()

            videoView.start()
        }
    }
}
