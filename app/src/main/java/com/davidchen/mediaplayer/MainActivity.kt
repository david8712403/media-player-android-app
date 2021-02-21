package com.davidchen.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davidchen.mediaplayer.databinding.ActivityMainBinding
import com.davidchen.mediaplayer.ui.fragment.VideoListFragment
import com.davidchen.mediaplayer.util.MyProgressDialog
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    lateinit var v: ActivityMainBinding
    lateinit var mProgressDialog: MyProgressDialog

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
        mProgressDialog = MyProgressDialog(this)
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0 ) {
            finish()
        }else {
            mProgressDialog.dismiss()
            supportFragmentManager.popBackStack()
        }
    }
}