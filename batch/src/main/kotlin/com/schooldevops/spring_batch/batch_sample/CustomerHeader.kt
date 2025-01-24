package com.schooldevops.spring_batch.batch_sample

import org.springframework.batch.item.file.FlatFileHeaderCallback
import java.io.Writer

class CustomerHeader : FlatFileHeaderCallback {
    override fun writeHeader(writer: Writer) {
        writer.write("ID,AGE")
    }
}