package com.merttoptas.cointracker.utils

class Constants {
    companion object {
        // Url
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
        const val COIN_DATABASE = "CoinDatabase"
        const val KEY_DATA_STORE_SETTINGS = "settings"
        const val KEY_USER_TOKEN = "user_token"

        //Api Endpoint
        const val CHECK_API_STATUS = "ping"
        const val COIN_LIST = "coins/markets"
        const val COIN_DETAIL = "coins/{id}"
        const val COIN_HISTORY = "coins/{id}/market_chart"
        const val VS_CURRENCY = "vs_currency"
        const val PRICE_CHANGE_PERCENTAGE = "price_change_percentage"
    }
}