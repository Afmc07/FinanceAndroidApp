package br.unifor.financeapp.model

data class UserWithItems(
    val user:User,
    val items:List<Item>
)