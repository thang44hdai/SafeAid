import com.example.safeaid.core.response.Category
import com.google.gson.annotations.SerializedName

data class QuizCategoryResponse(
    @SerializedName("categories")
    val categories: List<Category>
)
