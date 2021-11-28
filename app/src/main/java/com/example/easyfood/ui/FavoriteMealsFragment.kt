package com.example.easyfood.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfood.R
import com.example.easyfood.adapters.FavoriteMealsRecyclerAdapter
import com.example.easyfood.data.pojo.MealDB
import com.example.easyfood.data.pojo.MealDetail
import com.example.easyfood.databinding.FragmentFavoriteMealsBinding
import com.example.easyfood.mvvm.DetailsMVVM


class FavoriteMeals : Fragment() {
    lateinit var fBinding:FragmentFavoriteMealsBinding
    private lateinit var myAdapter:FavoriteMealsRecyclerAdapter
    private lateinit var detailsMVVM: DetailsMVVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = FavoriteMealsRecyclerAdapter()
        detailsMVVM = ViewModelProviders.of(this)[DetailsMVVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fBinding = FragmentFavoriteMealsBinding.inflate(inflater,container,false)
        return fBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView(view)
        onFavoriteMealClick()
        onFavoriteLongMealClick()
        observeBottomDialog()

        detailsMVVM.observeSaveMeal().observe(viewLifecycleOwner,object : Observer<List<MealDB>>{
            override fun onChanged(t: List<MealDB>?) {
                myAdapter.setFavoriteMealsList(t!!)
                if(t.isEmpty())
                    fBinding.tvFavEmpty.visibility = View.VISIBLE

                else
                    fBinding.tvFavEmpty.visibility = View.GONE

            }
        })

    }

    private fun observeBottomDialog() {
        detailsMVVM.observeMealBottomSheet().observe(viewLifecycleOwner,object : Observer<List<MealDetail>>{
            override fun onChanged(t: List<MealDetail>?) {
                val bottomDialog = MealBottomDialog()
                val b = Bundle()
                b.putString(HomeFragment.CATEGORY_NAME,t!![0].strCategory)
                b.putString(HomeFragment.MEAL_AREA,t!![0].strArea)
                b.putString(HomeFragment.MEAL_NAME,t!![0].strMeal)
                b.putString(HomeFragment.MEAL_THUMB,t!![0].strMealThumb)
                b.putString(HomeFragment.MEAL_ID,t!![0].idMeal)
                bottomDialog.arguments = b
                bottomDialog.show(childFragmentManager,"Favorite bottom dialog")
            }

        })
    }

    private fun prepareRecyclerView(v:View) {
        val recView =v.findViewById<RecyclerView>(R.id.fav_rec_view)
        recView.adapter = myAdapter
        recView.layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
    }

    private fun onFavoriteMealClick(){
        myAdapter.setOnFavoriteMealClickListener(object : FavoriteMealsRecyclerAdapter.OnFavoriteClickListener{
            override fun onFavoriteClick(meal: MealDB) {
                val intent = Intent(context,MealDetailesActivity::class.java)
                intent.putExtra(HomeFragment.MEAL_ID,meal.mealId.toString())
                intent.putExtra(HomeFragment.MEAL_STR,meal.mealName)
                intent.putExtra(HomeFragment.MEAL_THUMB,meal.mealThumb)
                startActivity(intent)
            }

        })
    }

    private fun onFavoriteLongMealClick() {
        myAdapter.setOnFavoriteLongClickListener(object : FavoriteMealsRecyclerAdapter.OnFavoriteLongClickListener{
            override fun onFavoriteLongCLick(meal: MealDB) {
                detailsMVVM.getMealByIdBottomSheet(meal.mealId.toString())
            }

        })
    }

}