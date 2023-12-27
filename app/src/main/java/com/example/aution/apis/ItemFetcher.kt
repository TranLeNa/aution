import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.aution.Config
import retrofit2.http.Path

// Data model representing an item.
data class Item(
    val id: Int,
    val category_id: Int?,
    val title: String?,
    val image: String?,
    val min_price: Int?,
    val time_start: Int?,
    val time_end: Int?
)

// ApiService defining the endpoint to fetch items.
interface ApiService {
    @GET("api/items")
    suspend fun getItems(): Response<List<Item>>

    @GET("api/item/{id}")
    suspend fun getItemById(@Path("id") id: Int): Response<Item>

    @GET("api/items/{categoryId}")
    suspend fun getItemsByCategory(@Path("categoryId") categoryId: Int): Response<List<Item>>
}

// Singleton object to initialize Retrofit and create the ApiService.
object RetrofitClient {
    private const val BASE_URL = Config.BASE_URL   // Replace with your actual base URL.

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Class to fetch item data using Retrofit and Coroutines.
class ItemFetcher {
    suspend fun fetchItems(): List<Item> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<Item>> = RetrofitClient.instance.getItems()
                if (response.isSuccessful) {
                    response.body() ?: listOf()
                } else {
                    Log.e("ItemFetcher", "Error: ${response.errorBody()?.string()}")
                    listOf()
                }
            } catch (e: Exception) {
                Log.e("ItemFetcher", "Exception: ${e.message}")
                listOf()
            }
        }
    }

    suspend fun fetchItemById(id: Int): Item? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<Item> = RetrofitClient.instance.getItemById(id)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("ItemFetcher", "Error: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ItemFetcher", "Exception: ${e.message}")
                null
            }
        }
    }

    suspend fun fetchItemsByCategory(categoryId: Int): List<Item> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<Item>> = RetrofitClient.instance.getItemsByCategory(categoryId)
                if (response.isSuccessful) {
                    response.body() ?: listOf()
                } else {
                    Log.e("ItemFetcher", "Error: ${response.errorBody()?.string()}")
                    listOf()
                }
            } catch (e: Exception) {
                Log.e("ItemFetcher", "Exception: ${e.message}")
                listOf()
            }
        }
    }
}
