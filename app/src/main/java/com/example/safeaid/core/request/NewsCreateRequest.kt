import com.google.gson.annotations.SerializedName

/**
 * Body dùng để tạo mới 1 tin tức
 */
data class NewsCreateRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    /**
     * Đường dẫn ảnh thumbnail, nếu không có thì truyền chuỗi rỗng
     */
    @SerializedName("thumbnail_path")
    val thumbnailPath: String = ""
)
