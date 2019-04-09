package com.sirionrazzer.diary.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.sirionrazzer.diary.Diary
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.models.UserStorage
import com.sirionrazzer.diary.trackitem.TemplateItemCreatorActivity
import com.sirionrazzer.diary.trackitem.TemplateItemViewerActivity
import com.sirionrazzer.diary.util.DateUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userStorage: UserStorage

    lateinit var mainViewModel: MainViewModel
    lateinit var adapter: TemplatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Diary.app.appComponent.inject(this)

        mainViewModel = createViewModel()

        toolbar.setTitle(R.string.title_my_day)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        if (userStorage.userSettings.firstTime) {
            mainViewModel.createDefaultTrackItems()
            userStorage.updateSettings {
                it.firstTime = false
            }
        }

        adapter = TemplatesAdapter(this, mainViewModel)
        gwTemplates.adapter = adapter

        btnManageActivities.setOnClickListener{
            //startActivity<TemplateItemViewerActivity>()
            val intent = Intent(this, TemplateItemViewerActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }


    override fun onResume() {
        super.onResume()
        val dateUtils = DateUtils()
        tvDate.text = dateUtils.smartDate(dateUtils.dateFromMillis(mainViewModel.date), false)
    }


    private fun createNewTrackItem() {
        startActivity<TemplateItemCreatorActivity>()
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != 1) { // templates are changed
            mainViewModel.initTrackAndTemplateItems()
            adapter.notifyDataSetChanged()
        }

    }


    private fun save() {
        mainViewModel.saveTrackItems()
    }


    fun createViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_save_log, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.mSaveLog) {
            save()
            finish()
        } else {
            onBackPressed()
        }
        return true
    }
}
