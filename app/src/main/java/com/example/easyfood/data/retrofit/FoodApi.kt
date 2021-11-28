package com.example.easyfood.data.retrofit

import com.example.easyfood.data.pojo.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface FoodApi {
    @GET("categories.php")
    fun getCategories(): Call<Category>

    @GET("filter.php?")
    fun getMealsByCategory(@Query("i") category:String):Call<Meals>

    @GET ("random.php")
    fun getRandomMeal():Call<RandomMeal>

    @GET("lookup.php?")
    fun getMealById(@Query("i") id:String):Call<RandomMeal>

    @GET("search.php?")
    fun getMealByName(@Query("s") s:String):Call<RandomMeal>
}