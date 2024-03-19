package com.rumune.web.domain.post.dto

import com.rumune.web.domain.post.entity.ProductPost
import com.rumune.web.domain.product.entity.Product

class ProductPostDto(
    val title:String,
    val content: String,
    val discount: Double,
    val deliveryFee: Int,
    val isPosted: Boolean,
    val thumbnailURL: String,
    val createdBy: String,
    val images: List<String>,
    val products: List<Product>,
    ) {
    companion object {
        fun from (p:ProductPost):ProductPostDto {
            return ProductPostDto(
                title = p.title,
                content = p.content,
                discount = p.discount,
                deliveryFee = p.deliveryFee,
                isPosted = p.isPosted,
                thumbnailURL = p.thumbnailURL,
                createdBy = p.createdBy.name,
                images = if(p.image.isEmpty()) listOf() else p.image.map{it.file.fileURL},
                products = if(p.products.isEmpty()) listOf() else p.products.map{it.product}
            )
        }
    }
}