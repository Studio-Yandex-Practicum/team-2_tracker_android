package com.nemislimus.tratometr.expenses.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nemislimus.tratometr.common.util.MainCategoryIcons

class DBHelper (val context: Context):
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        // Создание таб. EXPENSES
        db.execSQL(
            "CREATE TABLE EXPENSES ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "DATE INTEGER NOT NULL,"          // Дата в милисекундах
                    + "AMOUNT INTEGER NOT NULL,"        // Сумма в копейках
                    + "CATEGORY TEXT NOT NULL,"         // Категория
                    + "NOTE TEXT);"                     // Примечание
        )

        // Создание таб. CATEGORIES
        db.execSQL(
            "CREATE TABLE CATEGORIES ("
                    + "CATEGORY_NAME TEXT PRIMARY KEY," // Название категории - первичный ключ
                    + "ICON_RES_ID TEXT NOT NULL);"           // id иконки
        )
        // Вставка предустановленных записей в таблицу CATEGORIES
        val insertCategories = arrayOf(
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Кафе', '${map(MainCategoryIcons.MAIN_CAFE.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Транспорт', '${map(MainCategoryIcons.MAIN_TRANSPORT.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Здоровье', '${map(MainCategoryIcons.MAIN_HEALT.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Подарки', '${map(MainCategoryIcons.MAIN_GIFT.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Образование', '${map(MainCategoryIcons.MAIN_EDUCATION.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Дом', '${map(MainCategoryIcons.MAIN_HOME.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Продукты', '${map(MainCategoryIcons.MAIN_GROCERIES.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Спорт', '${map(MainCategoryIcons.MAIN_SPORTS.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Досуг', '${map(MainCategoryIcons.MAIN_HOBBY.resId)}');",
            "INSERT INTO CATEGORIES (CATEGORY_NAME, ICON_RES_ID) VALUES ('Семья', '${map(MainCategoryIcons.MAIN_FAMILY.resId)}');",
        )
        for (query in insertCategories) {
            db.execSQL(query)
        }
    }

    // Обновление/миграция БД
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

    private fun map(iconResId: Int): String {
        return context.resources.getResourceEntryName(iconResId)
    }

    companion object {
        private const val DB_NAME = "DBExpenses"
        private const val DB_VERSION = 1
    }
}