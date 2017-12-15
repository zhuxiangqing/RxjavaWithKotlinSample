package com.zhuxiangqing.rxjavawithkotlinsample.entity

/**
 * Created by zhuxi on 2017/12/15.
 *
 */
data class NewsEntity(var reason: String? = null,
                      var result: ResultBean? = null,
                      var error_code: Int = 0)