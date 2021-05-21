import kotlinx.html.js.onClickFunction
import react.*
import react.dom.div
import react.dom.span

@JsExport
class ItemList: RComponent<ItemListProps, RState>() {
    override fun RBuilder.render() {
        // TODO Allow passing in of a sorting/filtering function
        val sortedItems = props.items
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, {it.name}))
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, {it.tier}))
        for (item in sortedItems) {
            div {
                attrs {
                    onClickFunction = {
                        setState {
                            props.onSelectItem(item)
                        }
                    }
                }
                key = item.id.toString()
                span {
                    +item.name
                }
                +" "
                span {
                    +item.status
                }
                +" "
                span {
                    +item.tier
                }
            }
        }
    }
}

external interface ItemListProps: RProps {
    var items: List<ItemOverview>
    var onSelectItem: (ItemOverview) -> Unit
}

fun RBuilder.itemList(handler: ItemListProps.() -> Unit): ReactElement {
    return child(ItemList::class) {
        this.attrs(handler)
    }
}