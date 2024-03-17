package com.example.easyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfood.R
import com.example.easyfood.adapters.FavoriteMealsRecyclerAdapter
import com.example.easyfood.data.pojo.MealDB
import com.example.easyfood.data.pojo.MealDetail
import com.example.easyfood.databinding.FragmentFavoriteMealsBinding
import com.example.easyfood.mvvm.DetailsMVVM
import com.example.easyfood.ui.MealBottomDialog
import com.example.easyfood.ui.activites.MealDetailsActivity
import com.example.easyfood.ui.fragments.HomeFragment.Companion.CATEGORY_NAME
import com.example.easyfood.ui.fragments.HomeFragment.Companion.MEAL_AREA
import com.example.easyfood.ui.fragments.HomeFragment.Companion.MEAL_ID
import com.example.easyfood.ui.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.easyfood.ui.fragments.HomeFragment.Companion.MEAL_STR
import com.example.easyfood.ui.fragments.HomeFragment.Companion.MEAL_THUMB
import com.google.android.material.snackbar.Snackbar


class FavoriteMeals : Fragment() {
    private lateinit var recView:RecyclerView
    private lateinit var fBinding:FragmentFavoriteMealsBinding
    private lateinit var myAdapter:FavoriteMealsRecyclerAdapter
    private lateinit var detailsMVVM: DetailsMVVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = FavoriteMealsRecyclerAdapter()
        detailsMVVM = ViewModelProvider(this)[DetailsMVVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fBinding = FragmentFavoriteMealsBinding.inflate(inflater,container,false)
        return fBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView(view)
        onFavoriteMealClick()
        onFavoriteLongMealClick()
        observeBottomDialog()

        detailsMVVM.observeSaveMeal().observe(viewLifecycleOwner) { t: List<MealDB>? ->
            if (t.isNullOrEmpty()) {
                myAdapter.setFavoriteMealsList(emptyList())
                fBinding.tvFavEmpty.visibility = View.VISIBLE
            } else {
                myAdapter.setFavoriteMealsList(t)
                fBinding.tvFavEmpty.visibility = View.GONE
            }
        }



        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favoriteMeal = myAdapter.getMelaByPosition(position)
                detailsMVVM.deleteMeal(favoriteMeal)
                showDeleteSnackBar(favoriteMeal)
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recView)

    }

    private fun showDeleteSnackBar(favoriteMeal:MealDB) {
        Snackbar.make(requireView(), "Meal was deleted", Snackbar.LENGTH_LONG)
            .setAction("undo") { detailsMVVM.insertMeal(favoriteMeal) }
            .show()
    }

    private fun observeBottomDialog() {
        detailsMVVM.observeMealBottomSheet().observe(viewLifecycleOwner) { mealDetails: List<MealDetail>? ->
            mealDetails?.getOrNull(0)?.let { firstMealDetail ->
                val bottomDialog = MealBottomDialog()
                val b = Bundle().apply {
                    putString(CATEGORY_NAME, firstMealDetail.strCategory)
                    putString(MEAL_AREA, firstMealDetail.strArea)
                    putString(MEAL_NAME, firstMealDetail.strMeal)
                    putString(MEAL_THUMB, firstMealDetail.strMealThumb)
                    putString(MEAL_ID, firstMealDetail.idMeal)
                }
                bottomDialog.arguments = b
                bottomDialog.show(childFragmentManager, "Favorite bottom dialog")
            }
        }
    }

    private fun prepareRecyclerView(v:View) {
        recView = v.findViewById(R.id.fav_rec_view)
        recView.adapter = myAdapter
        recView.layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
    }

    private fun onFavoriteMealClick(){
        myAdapter.setOnFavoriteMealClickListener(object : FavoriteMealsRecyclerAdapter.OnFavoriteClickListener{
            override fun onFavoriteClick(meal: MealDB) {
                val intent = Intent(context, MealDetailsActivity::class.java)
                intent.putExtra(MEAL_ID,meal.mealId.toString())
                intent.putExtra(MEAL_STR,meal.mealName)
                intent.putExtra(MEAL_THUMB,meal.mealThumb)
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