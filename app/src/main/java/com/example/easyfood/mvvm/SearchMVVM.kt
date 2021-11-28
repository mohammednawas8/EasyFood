package com.example.easyfood.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easyfood.data.pojo.MealDetail
import com.example.easyfood.data.pojo.RandomMeal
import com.example.easyfood.data.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMVVM : ViewModel() {
    private var searchedMealLiveData = MutableLiveData<MealDetail>()


    fun searchMealDetail(name:String){
        RetrofitInstance.foodApi.getMealByName(name).enqueue(object : Callback<RandomMeal>{
            override fun onResponse(call: Call<RandomMeal>, response: Response<RandomMeal>) {
                searchedMealLiveData.value = response.body()!!.meals[0]
            }

            override fun onFailure(call: Call<RandomMeal>, t: Throwable) {
                Log.e(TAG,t.message.toString())
            }

        })
    }

    fun observeSearchLiveData():LiveData<MealDetail>{
        return searchedMealLiveData
    }
}