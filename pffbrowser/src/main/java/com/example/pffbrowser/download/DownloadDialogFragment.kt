package com.example.pffbrowser.download

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.pffbrowser.R
import com.example.pffbrowser.databinding.PbFragmentDownloadDialogBinding
import com.example.pffbrowser.utils.KeyboardUtil.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 下载确认弹窗
 * 使用 BottomSheetDialogFragment 从底部弹出
 */
@AndroidEntryPoint
class DownloadDialogFragment : BottomSheetDialogFragment() {
    
    companion object {
        const val TAG = "DownloadDialogFragment"
    }

    private var _binding: PbFragmentDownloadDialogBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var downloadManager: OkDownloadManager

    /**
     * 下载信息
     */
    private lateinit var downloadInfo: DownloadInfo

    /**
     * 文件名（不含扩展名）
     */
    private var fileNameWithoutExt: String = ""

    /**
     * 文件扩展名（包含点，如 .pdf）
     */
    private var fileExtension: String = ""

    /**
     * 完整文件名
     */
    private val fullFileName: String
        get() = "$fileNameWithoutExt$fileExtension"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取传递的下载信息
        downloadInfo = arguments?.getParcelable(DownloadInfo.ARG_KEY)
            ?: throw IllegalArgumentException("DownloadInfo is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PbFragmentDownloadDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        setupBottomSheetBehavior()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        // 设置屏幕外点击关闭
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    /**
     * 设置 BottomSheet 行为
     * 占据屏幕约 50% 高度
     */
    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val behavior = bottomSheetDialog.behavior

            // 计算屏幕高度的 50% 作为 peek height
            val displayMetrics = resources.displayMetrics
            val halfScreenHeight = (displayMetrics.heightPixels * 0.5).toInt()

            behavior.peekHeight = halfScreenHeight
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = false
            behavior.isHideable = true

//             监听 BottomSheet 状态变化，当拖拽关闭时先清除焦点
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                        binding.etFileName.clearFocus()
//                        hideKeyboard(binding.etFileName)
//                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 向下滑动过程中清除焦点
                    if (slideOffset < 0) {
                        binding.etFileName.clearFocus()
                        hideKeyboard(binding.etFileName)
                    }
                }
            })
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        val originalFileName = downloadInfo.fileName

        // 分离文件名和扩展名
        val lastDotIndex = originalFileName.lastIndexOf('.')
        if (lastDotIndex > 0) {
            fileNameWithoutExt = originalFileName.substring(0, lastDotIndex)
            fileExtension = originalFileName.substring(lastDotIndex)
        } else {
            fileNameWithoutExt = originalFileName
            fileExtension = ""
        }
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        // 1. 设置文件图标
        binding.ivFileIcon.setImageDrawable(getFileIcon(downloadInfo.mimeType))

        // 2. 设置文件名编辑框（只显示文件名，不含扩展名）
        binding.etFileName.setText(fileNameWithoutExt)
        binding.etFileName.setSelection(fileNameWithoutExt.length)

        // 3. 设置扩展名显示
        binding.tvFileExtension.text = fileExtension
        binding.tvFileExtension.visibility =
            if (fileExtension.isNotEmpty()) View.VISIBLE else View.GONE

        // 4. 设置文件大小
        if (downloadInfo.contentLength > 0) {
            binding.tvFileSize.text = "大小: ${formatFileSize(downloadInfo.contentLength)}"
            binding.tvFileSize.visibility = View.VISIBLE
        } else {
            binding.tvFileSize.visibility = View.GONE
        }

        // 5. 设置下载链接（单行省略）
        binding.tvDownloadUrl.text = downloadInfo.url
        binding.tvDownloadUrl.isSelected = true // 开启跑马灯效果

        // 6. 文件名编辑监听
        binding.etFileName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                fileNameWithoutExt = s?.toString() ?: ""
            }
        })

        // 7. 按钮点击事件
        binding.btnCancel.setOnClickListener {
            binding.etFileName.clearFocus()
            hideKeyboard(binding.etFileName)
            dismiss()
        }

        binding.btnDownload.setOnClickListener {
            startDownload()
        }
    }

    /**
     * 开始下载
     */
    private fun startDownload() {
        val finalFileName = fullFileName
        // 如果文件名为空，使用默认名称
        val validFileName = if (finalFileName.isBlank() || finalFileName == fileExtension) {
            "download_${System.currentTimeMillis()}$fileExtension"
        } else {
            finalFileName
        }
        // 开始下载（启动前台服务，传递所有参数）
        downloadManager.startDownload(
            url = downloadInfo.url,
            fileName = validFileName,
            mimeType = downloadInfo.mimeType,
            contentLength = downloadInfo.contentLength
        )
        binding.etFileName.clearFocus()
        hideKeyboard(binding.etFileName)
        dismiss()
    }

    /**
     * 根据 MIME 类型获取文件图标
     */
    private fun getFileIcon(mimeType: String?): Drawable? {
        val iconRes = when {
            mimeType == null -> R.drawable.pb_default_file_img
            mimeType.startsWith("image/") -> R.drawable.pb_default_file_img
            mimeType.startsWith("video/") -> R.drawable.pb_default_file_img
            mimeType.startsWith("audio/") -> R.drawable.pb_default_file_img
            mimeType.contains("pdf") -> R.drawable.pb_default_file_img
            mimeType.contains("word") || mimeType.contains("document") -> R.drawable.pb_default_file_img
            mimeType.contains("excel") || mimeType.contains("sheet") -> R.drawable.pb_default_file_img
            mimeType.contains("powerpoint") || mimeType.contains("presentation") -> R.drawable.pb_default_file_img
            mimeType.contains("zip") || mimeType.contains("compressed") -> R.drawable.pb_default_file_img
            mimeType.contains("apk") -> R.drawable.pb_default_file_img
            else -> R.drawable.pb_default_file_img
        }
        return ContextCompat.getDrawable(requireContext(), iconRes)
    }

    /**
     * 格式化文件大小
     */
    private fun formatFileSize(size: Long): String {
        return Formatter.formatFileSize(requireContext(), size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
