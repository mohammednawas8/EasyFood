package com.example.easyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.adapters.CategoriesRecyclerAdapter
import com.example.easyfood.adapters.MostPopularRecyclerAdapter
import com.example.easyfood.adapters.OnItemClick
import com.example.easyfood.adapters.OnLongItemClick
import com.example.easyfood.data.pojo.*
import com.example.easyfood.databinding.FragmentHomeBinding
import com.example.easyfood.mvvm.DetailsMVVM
import com.example.easyfood.mvvm.MainFragMVVM
import com.example.easyfood.ui.activites.MealActivity
import com.example.easyfood.ui.MealBottomDialog
import com.example.easyfood.ui.activites.MealDetailesActivity




class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var meal: RandomMealResponse
    private lateinit var detailMvvm: DetailsMVVM
    private var randomMealId = ""


    companion object{
        const val MEAL_ID="com.example.easyfood.ui.fragments.idMeal"
        const val MEAL_NAME="com.example.easyfood.ui.fragments.nameMeal"
        const val MEAL_THUMB="com.example.easyfood.ui.fragments.thumbMeal"
        const val CATEGORY_NAME=" com.example.easyfood.ui.fragments.categoryName"
        const val MEAL_STR=" com.example.easyfood.ui.fragments.strMeal"
        const val MEAL_AREA=" com.example.easyfood.ui.fragments.strArea"


    }



    private lateinit var myAdapter: CategoriesRecyclerAdapter
    private lateinit var mostPopularFoodAdapter: MostPopularRecyclerAdapter
    lateinit var binding: FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailMvvm = ViewModelProviders.of(this)[DetailsMVVM::class.java]
        binding = FragmentHomeBinding.inflate(layoutInflater)
        myAdapter = CategoriesRecyclerAdapter()
        mostPopularFoodAdapter = MostPopularRecyclerAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainFragMVVM = ViewModelProviders.of(this)[MainFragMVVM::class.java]
        showLoadingCase()


        prepareCategoryRecyclerView()
        preparePopularMeals()
        onRndomMealClick()
        onRandomLongClick()


        mainFragMVVM.observeMealByCategory().observe(viewLifecycleOwner, object : Observer<MealsResponse> {
            override fun onChanged(t: MealsResponse?) {
                val meals = t!!.meals
                setMealsByCategoryAdapter(meals)
                cancelLoadingCase()
            }


        })

        mainFragMVVM.observeCategories().observe(viewLifecycleOwner, object : Observer<CategoryResponse> {
            override fun onChanged(t: CategoryResponse?) {
                val categories = t!!.categories
                setCategoryAdapter(categories)

            }
        })

        mainFragMVVM.observeRandomMeal().observe(viewLifecycleOwner, object : Observer<RandomMealResponse> {
            override fun onChanged(t: RandomMealResponse?) {
                val mealImage = view.findViewById<ImageView>(R.id.img_random_meal)
                val imageUrl = t!!.meals[0].strMealThumb
                randomMealId = t.meals[0].idMeal
                Glide.with(this@HomeFragment)
                    .load(imageUrl)
                    .into(mealImage)
                meal = t
            }

        })

        mostPopularFoodAdapter.setOnClickListener(object : OnItemClick {
            override fun onItemClick(meal: Meal) {
                val intent = Intent(activity, MealDetailesActivity::class.java)
                intent.putExtra(MEAL_ID, meal.idMeal)
                intent.putExtra(MEAL_STR, meal.strMeal)
                intent.putExtra(MEAL_THUMB, meal.strMealThumb)
                startActivity(intent)
            }

        })

        myAdapter.onItemClicked(object : CategoriesRecyclerAdapter.OnItemCategoryClicked {
            override fun onClickListener(category: Category) {
                val intent = Intent(activity, MealActivity::class.java)
                intent.putExtra(CATEGORY_NAME, category.strCategory)
                startActivity(intent)
            }

        })

        mostPopularFoodAdapter.setOnLongCLickListener(object : OnLongItemClick {
            override fun onItemLongClick(meal: Meal) {
                detailMvvm.getMealByIdBottomSheet(meal.idMeal)
            }

        })

        detailMvvm.observeMealBottomSheet()
            .observe(viewLifecycleOwner, object : Observer<List<MealDetail>> {
                override fun onChanged(t: List<MealDetail>?) {
                    val bottomSheetFragment = MealBottomDialog()
                    val b = Bundle()
                    b.putString(CATEGORY_NAME, t!![0].strCategory)
                    b.putString(MEAL_AREA, t[0].strArea)
                    b.putString(MEAL_NAME, t[0].strMeal)
                    b.putString(MEAL_THUMB, t[0].strMealThumb)
                    b.putString(MEAL_ID, t[0].idMeal)

                    bottomSheetFragment.arguments = b

                    bottomSheetFragment.show(childFragmentManager, "BottomSheetDialog")
                }

            })


        // on search icon click
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }


    private fun onRndomMealClick() {
        binding.randomMeal.setOnClickListener {
            val temp = meal.meals[0]
            val intent = Intent(activity, MealDetailesActivity::class.java)
            intent.putExtra(MEAL_ID, temp.idMeal)
            intent.putExtra(MEAL_STR, temp.strMeal)
            intent.putExtra(MEAL_THUMB, temp.strMealThumb)
            startActivity(intent)
        }

    }

    private fun onRandomLongClick() {

        binding.randomMeal.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                detailMvvm.getMealByIdBottomSheet(randomMealId)
                return true
            }

        })
    }

    private fun showLoadingCase() {
        binding.apply {
            header.visibility = View.INVISIBLE

            tvWouldLikeToEat.visibility = View.INVISIBLE
            randomMeal.visibility = View.INVISIBLE
            tvOverPupItems.visibility = View.INVISIBLE
            recViewMealsPopular.visibility = View.INVISIBLE
            tvCategory.visibility = View.INVISIBLE
            categoryCard.visibility = View.INVISIBLE
            loadingGif.visibility = View.VISIBLE
            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.g_loading))

        }
    }

    private fun cancelLoadingCase() {
        binding.apply {
            header.visibility = View.VISIBLE
            tvWouldLikeToEat.visibility = View.VISIBLE
            randomMeal.visibility = View.VISIBLE
            tvOverPupItems.visibility = View.VISIBLE
            recViewMealsPopular.visibility = View.VISIBLE
            tvCategory.visibility = View.VISIBLE
            categoryCard.visibility = View.VISIBLE
            loadingGif.visibility = View.INVISIBLE
            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        }
    }

    private fun setMealsByCategoryAdapter(meals: List<Meal>) {
        mostPopularFoodAdapter.setMealList(meals)
    }

    private fun setCategoryAdapter(categories: List<Category>) {
        myAdapter.setCategoryList(categories)
    }

    private fun prepareCategoryRecyclerView() {
        binding.recyclerView.apply {
            adapter = myAdapter
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun preparePopularMeals() {
        binding.recViewMealsPopular.apply {
            adapter = mostPopularFoodAdapter
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        }
    }

}