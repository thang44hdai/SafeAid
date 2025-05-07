import com.example.safeaid.core.response.Category
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizCategoryResponse(
    @SerializedName("categories")
    val categories: List<Category>
) : Serializable
