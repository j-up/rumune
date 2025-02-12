package com.rumune.web.domain.post.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.rumune.web.domain.product.entity.Product
import jakarta.persistence.*

@Entity
@Table(name ="product_post_product")
class ProductPostProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long = 0,

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="product_post_id")
    val productPost: ProductPost,

    @ManyToOne
    @JoinColumn(name="product_id")
    val product: Product
) {
}