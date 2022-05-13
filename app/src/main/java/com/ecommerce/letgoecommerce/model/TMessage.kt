package com.ecommerce.letgoecommerce.model

import java.io.Serializable

class TMessage:Serializable{
     var user:User?=null
     var messageList:List<Message>?=null
     var product: Product?=null
 }

