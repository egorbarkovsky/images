package com.wldrmnd.thumbnail.api.controllers

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/v1")
class ConfigurationController {

    @GetMapping("/image/{id}/available")
    fun getStockQuantity(@PathVariable id: Int): Mono<Int> {
        return Mono.just(2 * id)
    }

}
