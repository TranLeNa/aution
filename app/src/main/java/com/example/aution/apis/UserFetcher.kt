import android.text.Editable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.aution.Config
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

// Data model representing an item.
data class User(
    val id: Int,
    val name: String?,
    val email: String?,
    val avatar: String?,
)

data class Login(
    val email: String?,
    val password: String?,

    )

data class SignupDetails(
    val name: String, val email: String, val password: String, val avatar: String
)

// ApiService defining the endpoint to fetch items.
interface ApiUserService {
    @GET("api/user/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    @POST("api/user/login")
    suspend fun login(@Body dataLogin: Login): Response<User>

    @POST("api/user")
    suspend fun signup(@Body dataSignup: SignupDetails): Response<User>

}

// Singleton object to initialize Retrofit and create the ApiService.
object RetrofitUserClient {
    private const val BASE_URL = Config.BASE_URL   // Replace with your actual base URL.

    val instance: ApiUserService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiUserService::class.java)
    }
}

// Class to fetch item data using Retrofit and Coroutines.
class UserFetcher {
    suspend fun fetchUserById(id: Int): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<User> = RetrofitUserClient.instance.getUserById(id)
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
                val response: Response<List<Item>> =
                    RetrofitClient.instance.getItemsByCategory(categoryId)
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

    // Posts a new history auction to the API
    suspend fun login(dataLogin: Login): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<User> = RetrofitUserClient.instance.login(dataLogin)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("HistoryAuctionFetcher", "Error: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("HistoryAuctionFetcher", "Exception: ${e.message}")
                null
            }
        }
    }

    suspend fun signup(dataSignup: SignupDetails): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<User> = RetrofitUserClient.instance.signup(dataSignup)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("UserFetcher", "Signup Error: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("UserFetcher", "Signup Exception: ${e.message}")
                null
            }
        }
    }
}
