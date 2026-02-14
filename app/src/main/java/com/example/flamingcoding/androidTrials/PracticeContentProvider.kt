package com.example.flamingcoding.androidTrials

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class PracticeContentProvider : ContentProvider() {

    private val table1Dir = 0
    private val table1Item = 1
    private val table2Dir = 2
    private val table2Item = 3

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI("com.example.app.provider", "table1", table1Dir)
        uriMatcher.addURI("com.example.app.provider ", "table1/#", table1Item)
        uriMatcher.addURI("com.example.app.provider ", "table2", table2Dir)
        uriMatcher.addURI("com.example.app.provider ", "table2/#", table2Item)
    }

    // 初始化ContentProvider的时候调用。通常会在这里完成对数据库的创建和升级等操作，返回true表示ContentProvider初始化成功，返回false则表示失败。
    override fun onCreate(): Boolean {
        return false
    }

    // 从ContentProvider中查询数据。
    // uri参数用于确定查询哪张表，
    // projection参数用于确定查询哪些列，
    // selection和selectionArgs参数用于约束查询哪些行，
    // sortOrder参数用于对结果进行排序，
    // 查询的结果存放在Cursor对象中返回
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            table1Dir -> {
                // 查询table1表中的所有数据
            }

            table1Item -> {
                // 查询table1表中的单条数据
            }

            table2Dir -> {
                // 查询table2表中的所有数据
            }

            table2Item -> {
                // 查询table2表中的单条数据
            }
        }
        return null
    }

    // 向ContentProvider中添加一条数据。uri参数用于确定要添加到的表，待添加的数据保存在values参数中。
    // 添加完成后，返回一个用于表示这条新记录的URI
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    // 更新ContentProvider中已有的数据。
    // uri参数用于确定更新哪一张表中的数据，新数据保存在values参数中，
    // selection和selectionArgs参数用于约束更新哪些行，受影响的行数将作为返回值返回
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    // 从ContentProvider中删除数据。
    // uri参数用于确定删除哪一张表中的数据，selection和selectionArgs参数用于约束删除哪些行，
    // 被删除的行数将作为返回值返回。
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    // 根据传入的内容URI返回相应的MIME类型
    // ● 必须以vnd开头。
    // ● 如果内容URI以路径结尾，则后接android.cursor.dir/； 如果内容URI以id结尾，则后接android.cursor.item/。
    // ● 最后接上vnd.<authority>.<path>。
    // content://com.example.app.provider/table1 --> vnd.android.cursor.dir/vnd.com.example.app.provider.table1
    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        table1Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table1"
        table1Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table1"
        table2Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table2"
        table2Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table2"
        else -> null
    }
}

// 还有一点需要注意，ContentProvider一定要在AndroidManifest.xml文件中注册才可以使用
// class DatabaseProvider : ContentProvider() {
//
//    private val bookDir = 0
//    private val bookItem = 1
//    private val categoryDir = 2
//    private val categoryItem = 3
//    private val authority = "com.example.databasetest.provider"
//    private var dbHelper: MyDatabaseHelper? = null
//
//    private val uriMatcher by lazy {
//        val matcher = UriMatcher(UriMatcher.NO_MATCH)
//        matcher.addURI(authority, "book", bookDir)
//        matcher.addURI(authority, "book/#", bookItem)
//        matcher.addURI(authority, "category", categoryDir)
//        matcher.addURI(authority, "category/#", categoryItem)
//        matcher
//    }
//
//    override fun onCreate() = context?.let {
//        dbHelper = MyDatabaseHelper(it, "BookStore.db", 2)
//        true
//    } ?: false
//
//    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
//            selectionArgs: Array<String>?, sortOrder: String?) = dbHelper?.let {
//        // 查询数据
//        val db = it.readableDatabase
//        val cursor = when (uriMatcher.match(uri)) {
//            bookDir -> db.query("Book", projection, selection, selectionArgs,
//                null, null, sortOrder)
//            bookItem -> {
//                val bookId = uri.pathSegments[1]
//                db.query("Book", projection, "id = ?", arrayOf(bookId), null, null,
//                    sortOrder)
//            }
//            categoryDir -> db.query("Category", projection, selection, selectionArgs,
//                    null, null, sortOrder)
//            categoryItem -> {
//                val categoryId = uri.pathSegments[1]
//                db.query("Category", projection, "id = ?", arrayOf(categoryId),
//                    null, null, sortOrder)
//            }
//            else -> null
//        }
//        cursor
//    }
//
//    override fun insert(uri: Uri, values: ContentValues?) = dbHelper?.let {
//        // 添加数据
//        val db = it.writableDatabase
//        val uriReturn = when (uriMatcher.match(uri)) {
//            bookDir, bookItem -> {
//                val newBookId = db.insert("Book", null, values)
//                Uri.parse("content://$authority/book/$newBookId")
//            }
//            categoryDir, categoryItem -> {
//                val newCategoryId = db.insert("Category", null, values)
//                Uri.parse("content://$authority/category/$newCategoryId")
//            }
//            else -> null
//        }
//        uriReturn
//    }
//
//    override fun update(uri: Uri, values: ContentValues?, selection: String?,
//            selectionArgs: Array<String>?) = dbHelper?.let {
//        // 更新数据
//        val db = it.writableDatabase
//        val updatedRows = when (uriMatcher.match(uri)) {
//            bookDir -> db.update("Book", values, selection, selectionArgs)
//            bookItem -> {
//                val bookId = uri.pathSegments[1]
//                db.update("Book", values, "id = ?", arrayOf(bookId))
//            }
//            categoryDir -> db.update("Category", values, selection, selectionArgs)
//            categoryItem -> {
//                val categoryId = uri.pathSegments[1]
//                db.update("Category", values, "id = ?", arrayOf(categoryId))
//            }
//            else -> 0
//        }
//        updatedRows
//    } ?: 0
//
//    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?)
//            = dbHelper?.let {
//        // 删除数据
//        val db = it.writableDatabase
//        val deletedRows = when (uriMatcher.match(uri)) {
//            bookDir -> db.delete("Book", selection, selectionArgs)
//            bookItem -> {
//                val bookId = uri.pathSegments[1]
//                db.delete("Book", "id = ?", arrayOf(bookId))
//            }
//            categoryDir -> db.delete("Category", selection, selectionArgs)
//            categoryItem -> {
//                val categoryId = uri.pathSegments[1]
//                db.delete("Category", "id = ?", arrayOf(categoryId))
//            }
//            else -> 0
//        }
//        deletedRows
//    } ?: 0
//
//    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
//        bookDir -> "vnd.android.cursor.dir/vnd.com.example.databasetest.provider.book"
//        bookItem -> "vnd.android.cursor.item/vnd.com.example.databasetest.provider.book"
//        categoryDir -> "vnd.android.cursor.dir/vnd.com.example.databasetest.
//            provider.category"
//        categoryItem -> "vnd.android.cursor.item/vnd.com.example.databasetest.
//            provider.category"
//        else -> null
//    }
//}