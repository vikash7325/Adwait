package ad.adwait.mcom.my_mentee.view

import ad.adwait.mcom.R
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
