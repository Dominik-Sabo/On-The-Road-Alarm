package com.sabo.dominik.ontheroadalarm.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sabo.dominik.ontheroadalarm.*
import com.sabo.dominik.ontheroadalarm.clickinterfaces.FavouriteClickInterface
import com.sabo.dominik.ontheroadalarm.databinding.ActivityFavouritesBinding
import com.sabo.dominik.ontheroadalarm.recyclers.FavouriteRecyclerAdapter
import com.sabo.dominik.ontheroadalarm.repository.FavouriteRepository

class FavouritesActivity : AppCompatActivity(), FavouriteClickInterface {

    private val NEW_REQUEST_CODE = 1
    private val EDIT_REQUEST_CODE = 2
    private val RESULT_DELETE = -2

    private lateinit var binding: ActivityFavouritesBinding

    private val repository: FavouriteRepository = FavouriteRepository.instance
    private lateinit var favAdapter: FavouriteRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourites)

        if(repository.favourites.isEmpty()) repository.loadData(application)

        favAdapter = FavouriteRecyclerAdapter(this)

        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rvRecyclerView.adapter = favAdapter

        favAdapter.addData(repository.favourites)

        if(intent.hasExtra("get")){
            binding.btnAdd.visibility = View.GONE
        }
        else {
            binding.btnAdd.setOnClickListener() {
                val intent = Intent(this@FavouritesActivity, EditFavouriteActivity::class.java)
                startActivityForResult(intent, NEW_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    NEW_REQUEST_CODE -> {
                        favAdapter.addItem(repository.favourites.last())
                    }
                    EDIT_REQUEST_CODE -> {
                        val position: Int = data!!.getIntExtra("position", 0)
                        favAdapter.changeItem(repository.favourites[position], position)
                    }
                }
            }
            RESULT_DELETE -> {
                val position = data!!.getIntExtra("position", 0)
                favAdapter.removeItem(position)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFavouritePlaceClick(position: Int) {
        if(intent.hasExtra("get")){
            val data: Intent = Intent()
            data.putExtra("name", repository.favourites[position].name)
            data.putExtra("latitude", repository.favourites[position].latitude)
            data.putExtra("longitude", repository.favourites[position].longitude)
            setResult(RESULT_OK, data)
            finish()
        }
        else{
            val intent = Intent(this@FavouritesActivity, EditFavouriteActivity::class.java)
            intent.putExtra("position", position)
            startActivityForResult(intent, EDIT_REQUEST_CODE)
        }
    }
}