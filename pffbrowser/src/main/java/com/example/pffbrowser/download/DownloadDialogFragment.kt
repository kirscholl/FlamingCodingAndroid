package com.example.pffbrowser.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pffbrowser.databinding.PbDialogDownloadBinding
import com.example.pffbrowser.utils.FileUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 下载弹窗
 * 从底部弹出，显示下载文件信息，支持编辑文件名
 */
class DownloadDialogFragment : BottomSheetDialogFragment() {

    private var _binding: PbDialogDownloadBinding? = null
    private val binding get() = _binding!!

    private var downloadInfo: DownloadDialogInfo? = null
    private var onDownloadConfirmListener: OnDownloadConfirmListener? = null

    private var fileNameWithoutExt: String = ""
    private var fileExtension: String = ""

    companion object {
        private const val ARG_DOWNLOAD_INFO = "download_info"

        fun newInstance(downloadInfo: DownloadDialogInfo): DownloadDialogFragment {
            return DownloadDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DOWNLOAD_INFO, downloadInfo)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadInfo = arguments?.getParcelable(ARG_DOWNLOAD_INFO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PbDialogDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupViews()
        setupListeners()
    }

    private fun setupDialog() {
        // 设置弹窗高度为屏幕的50%
        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.layoutParams?.height =
                (resources.displayMetrics.heightPixels * 0.5).toInt()
        }
    }

    private fun setupViews() {
        val info = downloadInfo ?: return

        // 分离文件名和扩展名
        val fileName = info.fileName
        val lastDotIndex = fileName.lastIndexOf('.')
        if (lastDotIndex > 0) {
            fileNameWithoutExt = fileName.substring(0, lastDotIndex)
            fileExtension = fileName.substring(lastDotIndex)
        } else {
            fileNameWithoutExt = fileName
            fileExtension = ""
        }

        // 设置文件图标
        val iconRes = FileUtil.getFileIconByExtension(fileExtension)
        binding.ivFileIcon.setImageResource(iconRes)

        // 设置文件名（不含扩展名）
        binding.etFileName.setText(fileNameWithoutExt)
        binding.etFileName.setSelection(fileNameWithoutExt.length)

        // 设置扩展名
        binding.tvExtension.text = fileExtension

        // 设置URL
        binding.tvUrl.text = info.url

        // 设置文件大小
        binding.tvFileSize.text = FileUtil.formatFileSize(info.contentLength)
    }

    private fun setupListeners() {
        // 取消按钮
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // 开始下载按钮
        binding.btnDownload.setOnClickListener {
            val customFileName = binding.etFileName.text.toString().trim()
            if (customFileName.isEmpty()) {
                binding.etFileName.error = "文件名不能为空"
                return@setOnClickListener
            }

            val fullFileName = customFileName + fileExtension
            onDownloadConfirmListener?.onDownloadConfirm(fullFileName, downloadInfo?.url ?: "")
            dismiss()
        }
    }

    /**
     * 设置下载确认监听器
     */
    fun setOnDownloadConfirmListener(listener: OnDownloadConfirmListener) {
        this.onDownloadConfirmListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 下载确认回调接口
     */
    interface OnDownloadConfirmListener {
        fun onDownloadConfirm(fileName: String, url: String)
    }
}

