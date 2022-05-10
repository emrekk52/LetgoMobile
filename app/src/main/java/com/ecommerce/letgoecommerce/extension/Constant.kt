package com.ecommerce.letgoecommerce.extension

import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.model.CategoryModel
import com.ecommerce.letgoecommerce.model.ShowCaseModel

class Constant {


    companion object {

        const val PRODUCT_STATE_ACTIVE = 0
        const val PRODUCT_STATE_INACTIVE = 1

        val cList = listOf<CategoryModel>(

            CategoryModel(name = "Araba", image = R.drawable.ic_car, color = R.color.car),
            CategoryModel(
                name = "Elektronik",
                image = R.drawable.ic_phone,
                color = R.color.electronic
            ),
            CategoryModel(
                name = "Ev Eşyaları",
                image = R.drawable.ic_chair,
                color = R.color.home_items
            ),
            CategoryModel(
                name = "Diğer Araçlar",
                image = R.drawable.ic_moped,
                color = R.color.other_tools
            ),
            CategoryModel(
                name = "Giyim ve Aksesuar",
                image = R.drawable.ic_shoe,
                color = R.color.clothes
            ),
            CategoryModel(
                name = "Bebek ve Çocuk",
                image = R.drawable.ic_baby,
                color = R.color.baby
            ),
            CategoryModel(
                name = "Araç Parçaları",
                image = R.drawable.ic_car_wheel,
                color = R.color.car_tools
            ),
            CategoryModel(
                name = "Spor ve Outdoor",
                image = R.drawable.ic_sport,
                color = R.color.spor
            ),
            CategoryModel(
                name = "Bahçe ve Hırdavat",
                image = R.drawable.ic_garden,
                color = R.color.garden
            ),
            CategoryModel(name = "Oyun", image = R.drawable.ic_game, color = R.color.game),
            CategoryModel(
                name = "Film, Kitap ve Müzik",
                image = R.drawable.ic_music,
                color = R.color.movie_book_music
            ),
            CategoryModel(name = "Diğer", image = R.drawable.ic_other, color = R.color.other),


            )

      private  fun getYearList(): List<String> {
            val array = arrayListOf<String>()
            for (i in 1944..2022)
                array.add(i.toString())
            return array
        }

        val carTypeList = listOf("Sedan", "Hatchback", "Station Wagon", "SUV", "Cabrio", "Pick up")
        val carEngineList = listOf("50+", "75+", "120+", "200+", "350+", "500+")
        val carGearList = listOf("Manuel", "Yarı otomatik", "Otomatik")
        val carTractionList = listOf("Önden çekiş", "Arkadan itiş", "Dört çeker")
        val carFuelList = listOf("Dizel", "Benzin", "Benzin & LPG")
        val carYearList = getYearList()
        val carColorList = listOf(
            "Beyaz",
            "Siyah",
            "Kahverengi",
            "Turuncu",
            "Kırmızı",
            "Bej",
            "Gümüş",
            "Mavi",
            "Sarı",
            "Yeşil",
            "Kurşun",
            "Mor"
        )


    }

}