import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.aution.Config

// Data model representing a category.
data class Category(val id: Int, val title: String?, val image: String)

// ApiCategoryService defining the endpoints to fetch categories.
interface ApiCategoryService {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    // Define a new endpoint to fetch a category by ID.
    @GET("api/category/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: Int): Response<Category>
}

// Singleton object to initialize Retrofit and create the ApiCategoryService.
object RetrofitCategoryClient {
    private const val BASE_URL = Config.BASE_URL  // Replace with your actual base URL.

    val instance: ApiCategoryService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiCategoryService::class.java)
    }
}

// Class to fetch category data using Retrofit and Coroutines.
class CategoryFetcher {
    // Function to fetch categories. It's a suspend function to be called from a coroutine.
    suspend fun fetchCategories(): List<Category> {
        return withContext(Dispatchers.IO) {  // Perform network IO in the background.
            try {
                // Make the network request.
                val response: Response<List<Category>> = RetrofitCategoryClient.instance.getCategories()
                if (response.isSuccessful) {
                    // Return the list of categories or an empty list if the body is null.
                    response.body() ?: listOf()
                } else {
                    // Log and return an empty list if the response has an error.
                    Log.e("CategoryFetcher", "Error: ${response.errorBody()?.string()}")
                    listOf()
                }
            } catch (e: Exception) {
                // Log and return an empty list if there's an exception.
                Log.e("CategoryFetcher", "Exception: ${e.message}")
                listOf()
            }
        }
    }

    // Function to fetch a category by ID using the new endpoint.
    suspend fun fetchCategoryById(categoryId: Int): Category? {
        return withContext(Dispatchers.IO) {  // Perform network IO in the background.
            try {
                // Make the network request to fetch a category by ID.
                val response: Response<Category> = RetrofitCategoryClient.instance.getCategoryById(categoryId)
                if (response.isSuccessful) {
                    // Return the fetched category or null if the body is null.
                    response.body()
                } else {
                    // Log and return null if the response has an error.
                    Log.e("CategoryFetcher", "Error: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                // Log and return null if there's an exception.
                Log.e("CategoryFetcher", "Exception: ${e.message}")
                null
            }
        }
    }
}
