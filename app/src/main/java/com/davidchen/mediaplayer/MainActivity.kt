package com.davidchen.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davidchen.mediaplayer.databinding.ActivityMainBinding
import com.davidchen.mediaplayer.ui.fragment.VideoListFragment
import com.davidchen.mediaplayer.util.ProgressDialogUtil
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    lateinit var v: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)

        Timber.plant(Timber.DebugTree())
        val f = VideoListFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.root, f)
            .show(f)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0 ) {
            finish()
            if (ProgressDialogUtil.mAlertDialog?.isShowing == false) {
                ProgressDialogUtil.mAlertDialog = null
            }
        }else {
            supportFragmentManager.popBackStack()
        }
    }
}