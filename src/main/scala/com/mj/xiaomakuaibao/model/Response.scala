package com.mj.xiaomakuaibao.model

case class NewsApiResponse(status: String,
                           totalResults: Int,
                           articles: List[Article])

case class Article(source: Source,
                   author: String,
                   title: String,
                   description: String,
                   url: String,
                   urlToImage: String,
                   publishedAt: java.util.Date,
                   content: String)

case class Source(id: String, name: String)

case class TranslationResponse(code: Int, lang: String, text: List[String])
