import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.aution.Config
import retrofit2.http.Path

// Data model representing an item.
data class HistoryAuction(
    val id: Int,
    val price: Int,
    val user_id: Int,
    val item_id: Int,
    val time_auction: String // Assuming time_auction is a string
)

// Data model representing an item.
data class CreateHistoryAuction(
    val price: Int,
    val user_id: Int,
    val item_id: Int,
)

// ApiService defining the endpoints to fetch and post items.
interface HistoryAuctionApiService {
    @GET("api/history_auctions/{id}")
    suspend fun getHistoryAuctionsByItem(@Path("id") id: Int): Response<List<HistoryAuction>>

    @GET("api/history_auctions")
    suspend fun getHistoryAuctions(): Response<List<HistoryAuction>>

    @POST("api/history_auction")
    suspend fun postHistoryAuction(@Body historyAuction: CreateHistoryAuction): Response<HistoryAuction>
}

// Singleton object to initialize Retrofit and create the ApiService.
object RetrofitHistoryClient {
    private const val BASE_URL = Config.BASE_URL   // Replace with your actual base URL

    val historyAuctionInstance: HistoryAuctionApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(HistoryAuctionApiService::class.java)
    }
}

// Fetcher class to interact with the Retrofit service.
class HistoryAuctionFetcher {
    // Fetches history auctions from the API
    suspend fun fetchHistoryAuctions(): List<HistoryAuction> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<HistoryAuction>> =
                    RetrofitHistoryClient.historyAuctionInstance.getHistoryAuctions()
                if (response.isSuccessful) {
                    response.body() ?: listOf()
                } else {
                    Log.e("HistoryAuctionFetcher", "Error: ${response.errorBody()?.string()}")
                    listOf()
                }
            } catch (e: Exception) {
                Log.e("HistoryAuctionFetcher", "Exception: ${e.message}")
                listOf()
            }
        }
    }

    // Fetches history auctions from the API
    suspend fun getHistoryAuctionsByItem(id: Int): List<HistoryAuction> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<HistoryAuction>> =
                    RetrofitHistoryClient.historyAuctionInstance.getHistoryAuctionsByItem(id)
                if (response.isSuccessful) {
                    response.body() ?: listOf()
                } else {
                    Log.e("HistoryAuctionFetcher", "Error: ${response.errorBody()?.string()}")
                    listOf()
                }
            } catch (e: Exception) {
                Log.e("HistoryAuctionFetcher", "Exception: ${e.message}")
                listOf()
            }
        }
    }


    // Posts a new history auction to the API
    suspend fun postHistoryAuction(historyAuction: CreateHistoryAuction): HistoryAuction? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<HistoryAuction> =
                    RetrofitHistoryClient.historyAuctionInstance.postHistoryAuction(historyAuction)
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
}
