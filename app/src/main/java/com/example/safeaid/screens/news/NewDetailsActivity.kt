package com.example.safeaid.screens.news

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtraining.databinding.ActivityNewDetailsBinding

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Cấu hình Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 2. Lấy dữ liệu từ Intent
        val title = intent.getStringExtra("news_title") ?: ""
        val htmlContent = intent.getStringExtra("news_html") ?: "<p>Không có nội dung</p>"

        // 3. Hiển thị title
        binding.tvDetailTitle.text = title

        // 4. Bọc HTML với viewport + CSS responsive
        // Trong NewsDetailsActivity, thay wrappedHtml bằng:
        val wrappedHtml = """
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <style>
                  img {
                    max-width: 100% !important;
                    height: auto !important;
                  }
                  body {
                    margin: 0;
                    padding: 16px;
                    font-size: 16px;
                    line-height: 1.5;
                  }
                  h1 {
                    font-size: 24px;
                    margin: 16px 0 8px;
                  }
                  h2 {
                    font-size: 20px;
                    margin: 14px 0 6px;
                  }
                  h3 {
                    font-size: 18px;
                    margin: 12px 0 4px;
                  }
                </style>
              </head>
              <body>
                $htmlContent
              </body>
            </html>
        """.trimIndent()


        // 5. Cấu hình WebView
        with(binding.webViewDetail.settings) {
            javaScriptEnabled    = true
            useWideViewPort      = true
            loadWithOverviewMode = true
        }
        binding.webViewDetail.webViewClient = WebViewClient()

        // 6. Load HTML đã bọc
        binding.webViewDetail.loadDataWithBaseURL(
            /* baseUrl */      null,
            /* data */         wrappedHtml,
            /* mimeType */     "text/html",
            /* encoding */     "utf-8",
            /* historyUrl */   null
        )
    }
}
