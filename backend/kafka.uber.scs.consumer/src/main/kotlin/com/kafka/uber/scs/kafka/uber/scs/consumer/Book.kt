package com.kafka.uber.scs.kafka.uber.scs.consumer

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor


@Data
@AllArgsConstructor
@NoArgsConstructor
class Book {
    val id: Int? = null
    val name: String? = null
}