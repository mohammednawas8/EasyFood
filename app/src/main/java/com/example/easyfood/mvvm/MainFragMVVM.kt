package com.example.easyfood.mvvm

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.easyfood.data.db.MealsDatabase
import com.example.easyfood.data.db.Repository
import com.example.easyfood.data.pojo.*
import com.example.easyfood.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "MainMVVM"

class MainFragMVVM: ViewModel() {
    private val mutableCategory = MutableLiveData<Category>()
    private val mutableRandomMeal = MutableLiveData<RandomMeal>()
    private val mutableMealsByCategory = MutableLiveData<Meals>()

    fun getAllCategories(context:Context) {
        RetrofitInstance.foodApi.getCategories().enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                mutableCategory.value = response.body()
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.d(TAG, t.message.toString())
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getRandomMeal() {
        RetrofitInstance.foodApi.getRandomMeal().enqueue(object : Callback<RandomMeal> {
            override fun onResponse(call: Call<RandomMeal>, response: Response<RandomMeal>) {
                mutableRandomMeal.value = response.body()
            }

            override fun onFailure(call: Call<RandomMeal>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }

        })
    }

    fun getMealsByCategory(category:String) {

        RetrofitInstance.foodApi.getMealsByCategory(category).enqueue(object : Callback<Meals> {
            override fun onResponse(call: Call<Meals>, response: Response<Meals>) {
                mutableMealsByCategory.value = response.body()
            }

            override fun onFailure(call: Call<Meals>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }

        })
    }

    fun observeMealByCategory(): LiveData<Meals> {
        return mutableMealsByCategory
    }

    fun observeRandomMeal(): LiveData<RandomMeal> {
        return mutableRandomMeal
    }

    fun observeCategories(): LiveData<Category> {
        return mutableCategory
    }

}