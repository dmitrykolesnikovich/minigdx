package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.model.UV
import com.github.dwursteisen.minigdx.buffer.DataSource

fun List<com.dwursteisen.minigdx.scene.api.model.Position>.positionsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 3) {
        val y = it % 3
        val x = (it - y) / 3
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            2 -> this[x].z
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

fun List<com.dwursteisen.minigdx.scene.api.model.Color>.colorsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 4) {
        val y = it % 4
        val x = (it - y) / 4
        when (y) {
            0 -> this[x].r
            1 -> this[x].g
            2 -> this[x].b
            3 -> this[x].alpha
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

fun List<UV>.uvDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 2) {
        val y = it % 2
        val x = (it - y) / 2
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

fun List<Float>.weightDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(this.toFloatArray())
}

fun List<Int>.jointDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(this.map { it.toFloat() }.toFloatArray())
}
