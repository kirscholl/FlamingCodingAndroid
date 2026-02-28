package com.example.flamingcoding.firstLineCodeAndroid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class LayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_layout)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // LinearLayout宽度为wrap_content,因此它将选择子View的最大宽度为其最后的宽度
        // 但是有个子View的宽度为match_parent，意思它将以LinearLayout的宽度为宽度，这就陷入死循环了
        // 因此这时候， LinearLayout 就会先以0为强制宽度测量一下子View，并正常地测量剩下的其他子View，然后再用其他子View里最宽的那个的宽度，二次测量这个match_parent的子 View，最终得出它的尺寸，并把这个宽度作为自己最终的宽度。
        // 这是对单个子View的二次测量，如果有多个子View写了match_parent ，那就需要对它们每一个都进行二次测量。
        // 除此之外，如果在LinearLayout中使用了weight会导致测量3次甚至更多,重复测量在Android中是很常见的
        // 如果我们的布局有两层，其中父View会对每个子View做二次测量，那它的每个子View一共需要被测量 2 次
        // 如果增加到三层，并且每个父View依然都做二次测量，这时候最下面的子View被测量的次数就直接翻倍了，变成 4 次
        // 同理，增加到 4 层的话会再次翻倍，子 View 需要被测量 8 次
    }
}