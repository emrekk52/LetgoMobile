package com.ecommerce.letgoecommerce.imageSlider.`interface`

import com.ecommerce.letgoecommerce.imageSlider.model.SlideUIModel




interface ItemClickListener {

    fun onItemClick(model: SlideUIModel, position: Int)
}