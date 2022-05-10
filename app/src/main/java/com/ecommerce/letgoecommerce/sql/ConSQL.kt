package com.ecommerce.letgoecommerce.sql

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.StrictMode
import android.util.Log
import com.ecommerce.letgoecommerce.extension.Constant.Companion.cList
import com.ecommerce.letgoecommerce.extension.SpecialShared
import com.ecommerce.letgoecommerce.extension.convertBitmaptoBase64
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.model.*
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ConSQL {

    companion object {

        const val userTable = "userr"
        const val carExtensionTable = "car_extension"
        const val imageTable = "images"
        const val categoryTable = "category"
        const val productTable = "product"
        const val cityTable = "city"

        const val USER_ID = "user_id"
        const val PRODUCT_ID = "id"
        const val CITY_ID = "id"
        const val CATEGORY_ID = "category_id"
        const val DESCRIPTION = "description"
        const val PRICE = "price"
        const val STATE = "state"
        const val CREATED_DATE = "created_date"

    }

    private var con: Connection? = null

    @SuppressLint("NewApi")
    private fun conClass(): Connection? {

        val reqClass = "net.sourceforge.jtds.jdbc.Driver"
        val ip = "192.168.1.169"
        val port = "1433"
        val db = "dbletgo"
        val username = "test"
        val password = "test"
        val connectUrl = "jdbc:jtds:sqlserver://$ip:$port/$db"

        val a = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(a)

        try {
            Class.forName(reqClass)
            //connectUrl = "jdbc:jtds:sqlserver://$ip:$port;databasename=$db;user=$username;password=$password;"
            con = DriverManager.getConnection(connectUrl, username, password)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Log.e("Error is", e.message.toString())
            showToast("Error ${e.message}")
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("Error is", e.message.toString())
            showToast("Failure ${e.message}")

        }

        return con
    }


    fun signUp(user: User): DbResult {

        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {
                val sqlStatement =
                    "insert into ${ConSQL.userTable} (name,surname,email,password,city_id) " +
                            "values ('${user.name}','${user.surname}','${user.email}','${user.password}','${user.cityId}');"
                val smt = connection.createStatement()
                smt.execute(sqlStatement)

                val query = "select * from ${userTable} where email='${user.email}';"
                val set = smt.executeQuery(query)
                var id = 0
                while (set.next())
                    id = set.getInt("id")

                var image = Image()
                image.uid = id
                image.description = "profile"

                uploadPhoto(image)
                connection.close()


                result.message = "Kayıt başarılı bir şekilde oluşturuldu"
                result.state = true
                result.loginId = id
                sp.setUserId(id)
            } catch (e: Exception) {
                Log.e("Error:", e.message.toString())
                when {
                    e.message.toString().trim().lowercase()
                        .contains("the duplicate key value is") -> {
                        result.message = "Böyle bir e-posta zaten kaydedilmiş"
                        result.state = false
                    }
                    else -> {
                        result.message = "${e.message}"
                        result.state = false
                    }
                }

            }

        }

        return result
    }

    fun login(email: String, password: String): DbResult {
        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {

                val user = getUserInfo(email)


                if (user == null) {
                    result.message = "Böyle bir kullanıcı bulunamadı!"
                    result.state = false
                } else if (user.email.equals(email) && user.password.equals(password)) {
                    result.message = "Giriş yapıldı"
                    result.state = true
                    result.loginId = user.id!!
                    sp.setUserId(user.id!!)
                } else {
                    result.message = "Parola ya da e-posta hatalı!"
                    result.state = false
                }


            } catch (e: Exception) {
                Log.e("Error:", e.message.toString())
                result.message = "${e.message}"
                result.state = false
            }

        }

        return result

    }

    private val sp = SpecialShared()

    fun getUserInfo(email: String? = null): User? {

        val connection = conClass()
        var user: User? = null

        if (connection != null) {


            try {


                val query = if (email.isNullOrEmpty())
                    "select u.id, u.name,u.surname,u.email,u.password,u.city_id,i.description,i.photo_url,c.city_name from ${userTable}  as u, ${imageTable}  as i , ${cityTable} as c where i.uid=u.id and u.city_id=c.id  and u.id='${sp.getUserId()}' and i.description='profile';"
                else
                    "select u.id,u.name,u.surname,u.email,u.password,u.city_id,i.description,i.photo_url,c.city_name from ${userTable}  as u, ${imageTable}  as i, ${cityTable} as c where i.uid=u.id and u.city_id=c.id and u.email='$email' and i.description='profile';"


                val smt = connection.createStatement()
                val set = smt.executeQuery(query)


                while (set.next()) {
                    user = User(
                        set.getInt("id"),
                        set.getString("name"),
                        set.getString("surname"),
                        set.getString("email"),
                        set.getString("password"),
                        set.getString("city_name"),
                        set.getInt("city_id"),
                        photo_url = set.getString("photo_url")
                    )
                }

                connection.close()


            } catch (e: Exception) {
                Log.e("Error:", e.message.toString())
            }

        }


        return user
    }

    fun getUserInfo(id: Int): User? {

        val connection = conClass()
        var user: User? = null

        if (connection != null) {


            try {


                val query =
                    "select u.id, u.name,u.surname,u.email, u.password,i.description,i.photo_url, c.city_name from ${userTable}  as u, ${imageTable}  as i,${cityTable}  as c where i.uid=u.id and u.city_id=c.id and u.id='${id}' and i.description='profile';"


                val smt = connection.createStatement()
                val set = smt.executeQuery(query)


                while (set.next()) {
                    user = User(
                        set.getInt("id"),
                        set.getString("name"),
                        set.getString("surname"),
                        set.getString("email"),
                        set.getString("password"),
                        set.getString("city_name"),
                        photo_url = set.getString("photo_url")
                    )


                }

                connection.close()


            } catch (e: Exception) {
                Log.e("Error:", e.message.toString())
            }

        }


        return user
    }

    fun uploadPhoto(image: Image) {

        val connection = conClass()

        if (connection != null) {

            try {

                val query =
                    "update ${imageTable} set photo_url = '${image.photo_url}' where uid='${image.uid}' and description='${image.description}'"

                val smt = connection.prepareStatement(query)
                smt.executeUpdate()

                connection.close()


            } catch (e: Exception) {
                Log.e("Errore:", e.message.toString())
            }

        }

    }


    fun getCategory(): List<CategoryModel> {
        val connection = conClass()
        val list = arrayListOf<CategoryModel>()

        if (connection != null) {

            val query = "select * from ${categoryTable}"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                val categoryModel = CategoryModel(
                    set.getInt("id"),
                    set.getString("name"),
                    cList.first {
                        it.name.trim().lowercase() == set.getString("name").lowercase().trim()
                    }.image,
                    cList.first {
                        it.name.trim().lowercase() == set.getString("name").lowercase().trim()
                    }.color
                )


                list.add(categoryModel)


            }

            connection.close()


        }

        return list

    }

    fun getCategoryId(id: Int): CategoryModel? {
        val connection = conClass()
        var categoryModel: CategoryModel? = null

        if (connection != null) {

            val query = "select * from ${categoryTable} where id='$id'"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                categoryModel = CategoryModel(
                    set.getInt("id"),
                    set.getString("name"),

                    )


            }

            connection.close()


        }

        return categoryModel

    }

    fun getCategoryTopId(topId: Int): List<CategoryModel> {
        val connection = conClass()
        val list = arrayListOf<CategoryModel>()

        if (connection != null) {

            val query = "select * from ${categoryTable} where topid='$topId'"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                val categoryModel = CategoryModel(
                    set.getInt("id"),
                    set.getString("name"),
                    set.getInt("topid")
                )


                list.add(categoryModel)


            }

            connection.close()


        }

        return list

    }


    fun addProduct(context: Context, product: Product) = CoroutineScope(Dispatchers.IO).async {

        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {
                val sqlStatement =
                    "insert into ${ConSQL.productTable} (header,description,price,user_id,category_id) " +
                            "values ('${product.header}','${product.description}','${product.price}','${product.userId}','${product.categoryId}');"
                val smt = connection.createStatement()
                smt.execute(sqlStatement)

                val query =
                    "select * from ${productTable} where user_id='${product.userId}' and category_id='${product.categoryId}' and header='${product.header}' and description='${product.description}' and price='${product.price}'"
                val set = smt.executeQuery(query)


                while (set.next())
                    product.id = set.getInt("id")


                connection.close()

                if (product.categoryId == 13) {
                    val cExtensionResult = addCarExtension(product)
                }

                val iResult = addProductImage(context, product)

                if (iResult.state) {
                    result.message = "Ürün başarılı bir şekilde oluşturuldu"
                    result.state = true
                } else {
                    result.message = iResult.message
                    result.state = false
                }


            } catch (e: Exception) {
                Log.e("Error product:", e.message.toString())

                result.message = "${e.message}"
                result.state = false

            }

        }

        result
    }

    private fun addCarExtension(product: Product): DbResult {
        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {

                val query =
                    "insert into ${ConSQL.carExtensionTable} (product_id,km,car_type,car_model,car_engine,car_gear,car_color,car_fuel,car_traction) values(?,?,?,?,?,?,?,?,?)"

                val smt = connection.prepareStatement(query)
                if (product.carExtension != null) {

                    val engine = product.carExtension?.carEngine!!.substring(0, 2)
                    smt.setInt(1, product.id!!)
                    smt.setInt(2, product.carExtension?.km!!)
                    smt.setInt(4, product.carExtension?.carModel!!)

                    smt.setString(3, product.carExtension?.carType!!)
                    smt.setString(5, engine)
                    smt.setString(6, product.carExtension?.carGear!!)
                    smt.setString(7, product.carExtension?.carColor!!)
                    smt.setString(8, product.carExtension?.carFuel!!)
                    smt.setString(9, product.carExtension?.carTraction!!)

                }
                smt.execute()

                connection.close()


                result.message = "Araç bilgileri başarılı bir şekilde eklendi"
                result.state = true

            } catch (e: java.lang.Exception) {
                Log.e("Error car_extension :", e.message.toString())

                result.message = "${e.message}"
                result.state = false

            }


        }

        return result
    }

    private fun addProductImage(context: Context, product: Product): DbResult {
        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {

                product.pictureList?.forEach {

                    val query =
                        "insert into ${ConSQL.imageTable} (photo_url,uid,product_id) values(?,?,?)"

                    val smt = connection.prepareStatement(query)
                    smt.setString(1, convertBitmaptoBase64(context, it))
                    smt.setInt(2, product.userId)
                    smt.setInt(3, product.id!!)
                    smt.execute()

                }




                connection.close()


                result.message = "Ürün başarılı bir şekilde oluşturuldu"
                result.state = true


            } catch (e: Exception) {
                Log.e("Error product photo_url:", e.message.toString())

                result.message = "${e.message}"
                result.state = false

            }


        }


        return result

    }


    fun getProduct(userId: Int): List<Product> {
        val connection = conClass()
        val list = arrayListOf<Product>()

        if (connection != null) {


            val query =
                "select * from ${productTable} where user_id='$userId'  order by created_date desc"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                val product = Product(
                    set.getInt("id"),
                    set.getInt("category_id"),
                    set.getInt("user_id"),
                    set.getString("header"),
                    set.getFloat("price"),
                    set.getString("description"),
                    bitmapList = getProductImages(
                        set.getInt("id"),
                        userId
                    ),
                    state = set.getInt("state"),
                    createdDate = convertDate(set.getString("created_date")),

                    )

                if (product.categoryId == 13) {
                    product.carExtension = getCarExtension(product)
                }


                list.add(product)


            }


            connection.close()


        }

        return list

    }

    private fun getCarExtension(product: Product, filter: Filter? = null): CarExtension? {
        val connection = conClass()
        var carExtension: CarExtension? = null

        if (connection != null) {

            val engine = filter?.carEngine?.substring(0, filter.carEngine.length - 1)

            var maxKm: String? = null
            var minKm: String? = null
            if (filter?.maxKm != null)
                maxKm = "km<=${filter.maxKm}"
            if (filter?.minKm != null)
                minKm = "km>=${filter.minKm}"


            var maxYear: String? = null
            var minYear: String? = null
            if (filter?.maxKm != null)
                maxYear = "car_model<=${filter.maxYear}"
            if (filter?.minKm != null)
                minYear = "car_model>=${filter.minYear}"


            var query =
                if (filter == null)
                    "select * from ${carExtensionTable} where product_id='${product.id}'"
                else
                    "select * from ${carExtensionTable} where product_id='${product.id}' and car_type='${filter.carType}' and " +
                            "car_engine='$engine' and " +
                            "car_gear='${filter.carGear}' and car_color='${filter.carColor}' and " +
                            "car_fuel='${filter.carFuel}' and car_traction='${filter.carTraction}' "


            if (maxYear != null)
                query += " and $maxYear "

            if (minYear != null)
                query += " and $minYear "

            if (maxKm != null)
                query += " and $maxKm "

            if (minKm != null)
                query += " and $minKm "


            val smt = connection.createStatement()
            val set = smt.executeQuery(query)

            while (set.next()) {
                carExtension = CarExtension(
                    set.getInt("id"),
                    set.getInt("km"),
                    set.getString("car_type"),
                    set.getInt("car_model"),
                    set.getString("car_engine"),
                    set.getString("car_gear"),
                    set.getString("car_color"),
                    set.getString("car_fuel"),
                    set.getString("car_traction")
                )
            }

            connection.close()


        }


        return carExtension
    }


    @SuppressLint("SimpleDateFormat")
    private fun convertDate(time: String?): String {

        if (time == null)
            return ""

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val outputFormat: DateFormat = SimpleDateFormat("HH:mm dd MMM, yyyy", Locale("tr"))

        val date: Date = inputFormat.parse(time) as Date

        return outputFormat.format(date)

    }

    private fun getProductImages(
        productId: Int,
        userId: Int? = null,
        isBreak: Boolean = false
    ): List<Bitmap> {

        val connection = conClass()
        val list = arrayListOf<Bitmap>()

        if (connection != null) {

            val query =
                if (userId == null) "select * from ${imageTable} where product_id='$productId'"
                else
                    "select * from ${imageTable} where uid='$userId' and product_id='$productId'"

            val smt = connection.createStatement()
            val set = smt.executeQuery(query)

            while (set.next()) {
                val photo_url = set.getString("photo_url")
                if (photo_url != null) {
                    val bitmap = convertImagetoBitmap(photo_url)
                    list.add(bitmap!!)
                    if (isBreak) break

                }
            }

            connection.close()

        }

        return list
    }


    fun removeProduct(id: Int): DbResult {


        val connection = conClass()
        val result = DbResult()

        if (connection != null) {

            try {
                val query = "delete from ${productTable} where id='$id'"
                val query2 = "delete from ${imageTable} where product_id='$id'"
                val smt = connection.createStatement()
                smt.execute(query)
                smt.execute(query2)

                result.message = "İlan başarılı bir şekilde silindi"
                result.state = true

                connection.close()
            } catch (e: Exception) {
                Log.e("Error remove product :", e.message.toString())

                result.message = "${e.message}"
                result.state = false

            }

        }


        return result
    }


    fun getCity(): List<City> {

        val connection = conClass()
        val list = arrayListOf<City>()

        if (connection != null) {

            val query =
                "select * from ${cityTable}"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {
                list.add(City(set.getInt("id"), set.getString("city_name")))

            }

        }

        return list

    }

    fun getProductByCity(city_id: String, size: Int = 10, all: Boolean = false): List<Product> {

        val connection = conClass()
        val list = arrayListOf<Product>()


        if (connection != null) {

            try {


                val query = if (!all)
                    "select top $size * from ${userTable} where city_id='$city_id' and id!='${sp.getUserId()}'"
                else
                    "select * from ${userTable} where city_id='$city_id' and id!='${sp.getUserId()}'"

                val smt = connection.createStatement()
                val set = smt.executeQuery(query)

                val idList = arrayListOf<Int>()

                while (set.next()) {
                    idList.add(set.getInt("id"))
                }

                idList.forEach {
                    val query =
                        "select * from ${productTable} where user_id='$it' and state='0'"
                    val smt = connection.createStatement()
                    val set = smt.executeQuery(query)
                    while (set.next()) {

                        val product = Product(
                            set.getInt("id"),
                            set.getInt("category_id"),
                            set.getInt("user_id"),
                            set.getString("header"),
                            set.getFloat("price"),
                            set.getString("description"),
                            bitmapList = getProductImages(
                                set.getInt("id"),
                                set.getInt("user_id"), true
                            ),
                            state = set.getInt("state"),
                            createdDate = convertDate(set.getString("created_date"))
                        )

                        if (product.categoryId == 13) {
                            product.carExtension = getCarExtension(product)
                        }

                        list.add(product)


                    }

                }

            } catch (e: java.lang.Exception) {
            }

        }



        return list
    }

    fun getUser(): User? = ConSQL().getUserInfo()


    suspend fun getProductByRandom(size: Int = 20, userId: Int?) =
        withContext(Dispatchers.Default) {
            val connection = conClass()
            val list = arrayListOf<Product>()

            if (connection != null) {

                val t = userId ?: -1

                val query =
                    "select top $size * from ${productTable} where user_id!='$t' and state='0' order by newid()"
                val smt = connection.createStatement()
                val set = smt.executeQuery(query)


                while (set.next()) {

                    val product = Product(
                        set.getInt("id"),
                        set.getInt("category_id"),
                        set.getInt("user_id"),
                        set.getString("header"),
                        set.getFloat("price"),
                        set.getString("description"),
                        bitmapList = getProductImages(set.getInt("id")),
                        state = set.getInt("state"),
                        createdDate = convertDate(set.getString("created_date"))
                    )


                    if (product.categoryId == 13) {
                        product.carExtension = getCarExtension(product)
                    }

                    list.add(product)


                }


                connection.close()


            }

            list

        }

    fun getProductByCategory(categoryId: Int?): List<Product> {
        val connection = conClass()
        val list = arrayListOf<Product>()

        if (connection != null) {

            val t = sp.getUserId() ?: -1

            val query =
                "select * from ${productTable} where user_id!='$t' and category_id='$categoryId' and state='0' order by created_date desc"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                val product = Product(
                    set.getInt("id"),
                    set.getInt("category_id"),
                    set.getInt("user_id"),
                    set.getString("header"),
                    set.getFloat("price"),
                    set.getString("description"),
                    bitmapList = getProductImages(set.getInt("id")),
                    state = set.getInt("state"),
                    createdDate = convertDate(set.getString("created_date"))
                )

                if (product.categoryId == 13) {
                    product.carExtension = getCarExtension(product)
                }

                list.add(product)


            }


            connection.close()


        }

        return list


    }


    fun getProductBySearch(searchText: String) = CoroutineScope(Dispatchers.IO).async {
        val connection = conClass()
        val list = arrayListOf<Product>()

        if (connection != null) {

            val t = sp.getUserId() ?: -1

            val query =
                "select * from ${productTable} where user_id!='$t' and state='0' and (header like '%$searchText%' COLLATE Latin1_General_CI_AS or description like '%$searchText%' COLLATE Latin1_General_CI_AS) order by created_date desc"
            val smt = connection.createStatement()
            val set = smt.executeQuery(query)


            while (set.next()) {

                val product = Product(
                    set.getInt("id"),
                    set.getInt("category_id"),
                    set.getInt("user_id"),
                    set.getString("header"),
                    set.getFloat("price"),
                    set.getString("description"),
                    bitmapList = getProductImages(set.getInt("id")),
                    state = set.getInt("state"),
                    createdDate = convertDate(set.getString("created_date"))
                )

                if (product.categoryId == 13) {
                    product.carExtension = getCarExtension(product)
                }

                list.add(product)


            }


            connection.close()


        }

        list


    }


    fun getFilterProduct(filter: Filter?) = CoroutineScope(Dispatchers.IO).async {
        val connection = conClass()
        val list = arrayListOf<Product>()

        if (connection != null) {


            if (filter?.cityId != null) {
                val usersQuery =
                    "select * from ${userTable} where city_id='${filter.cityId}' and id!='${sp.getUserId()}'"

                val smt1 = connection.createStatement()
                val set1 = smt1.executeQuery(usersQuery)

                val idList = arrayListOf<Int>()

                while (set1.next()) {
                    idList.add(set1.getInt("id"))
                }

                idList.forEach {

                    var topQuery =
                        "category_id='${filter.categoryId}' and user_id='$it'"

                    if (filter.minPrice != null)
                        topQuery += "and price>=${filter.minPrice}"

                    if (filter.maxPrice != null)
                        topQuery += "and price<=${filter.maxPrice}"

                    val query =
                        "select * from ${productTable} where $topQuery order by created_date desc"
                    val smt = connection.createStatement()
                    val set = smt.executeQuery(query)


                    while (set.next()) {

                        val product = Product(
                            set.getInt("id"),
                            set.getInt("category_id"),
                            set.getInt("user_id"),
                            set.getString("header"),
                            set.getFloat("price"),
                            set.getString("description"),
                            bitmapList = getProductImages(set.getInt("id")),
                            state = set.getInt("state"),
                            createdDate = convertDate(set.getString("created_date"))
                        )

                        if (product.categoryId == 13) {
                            product.carExtension = getCarExtension(product, filter)
                            if (product.carExtension != null)
                                list.add(product)
                        } else
                            list.add(product)


                    }

                }
            }
            else {

                var topQuery =
                    "user_id!='${sp.getUserId()}' and category_id='${filter?.categoryId}' "

                if (filter?.minPrice != null)
                    topQuery += " and price>=${filter.minPrice} "

                if (filter?.maxPrice != null)
                    topQuery += " and price<=${filter.maxPrice} "

                val query =
                    "select * from ${productTable} where $topQuery order by created_date desc"
                val smt = connection.createStatement()
                val set = smt.executeQuery(query)


                while (set.next()) {

                    val product = Product(
                        set.getInt("id"),
                        set.getInt("category_id"),
                        set.getInt("user_id"),
                        set.getString("header"),
                        set.getFloat("price"),
                        set.getString("description"),
                        bitmapList = getProductImages(set.getInt("id")),
                        state = set.getInt("state"),
                        createdDate = convertDate(set.getString("created_date"))
                    )

                    if (product.categoryId == 13) {
                        product.carExtension = getCarExtension(product, filter)
                        if (product.carExtension != null)
                            list.add(product)
                    } else
                        list.add(product)


                }

            }



            connection.close()


        }

        list


    }


}

