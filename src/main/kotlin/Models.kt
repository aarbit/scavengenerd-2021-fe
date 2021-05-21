external interface ItemOverview {
    val id: Int
    val name: String
    val tier: String
    val status: String
}

external interface ItemDetail {
    val id: Int
    val name: String
    val tier: String
    val status: String
    val entries: Array<ItemEntryDetail>
}

external interface ItemEntryDetail {
    val id: Int
    var status: String
    val userName: String
    val photo: ByteArray
}