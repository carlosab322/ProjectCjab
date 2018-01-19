package com.applaudotest.projectcjab.realm

class TransactionTime {
    var start: Long = 0
    var end: Long = 0

    init {
        this.start = 0
        this.end = 0
    }

    val duration: Long
        get() = if (this.start > 0 && this.end > 0) {
            this.end - this.start
        } else {
            0
        }
}