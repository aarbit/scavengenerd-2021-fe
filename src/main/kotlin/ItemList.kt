import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.fileInput
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.span
import styled.*

external interface ItemListProps: RProps {
    var onSelectItem: (ItemOverview) -> Unit
}

external interface ItemListState: RState {
    var items: List<ItemOverview>?
    var filterInput: String?
    var isOnlyNotFound: Boolean
}

@JsExport
class ItemList: RComponent<ItemListProps, ItemListState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                textAlign = TextAlign.center
            }
            styledInput {
                css {
                    fontSize = 3.vh
                    height = 3.vh
                }
                attrs {
                    type = InputType.search
                    onChangeFunction = {
                        setState {
                            filterInput = (it.target as HTMLInputElement).value
                        }
                    }
                }
            }

            styledLabel {
                css {
                    +ScavengenerdStyles.pushButton
                    if (state.isOnlyNotFound) {
                        backgroundColor = Color.maroon
                    } else {
                        backgroundColor = Color.lightGray
                    }
                }
                styledInput {
                    css(ScavengenerdStyles.goAwayThing)
                    attrs {
                        type = InputType.checkBox
                        onChangeFunction = {
                            setState {
                                isOnlyNotFound = (it.target as HTMLInputElement).checked
                            }
                        }
                    }
                }
                styledSpan {
                    +"Not Found"
                }
            }
        }

        state.items?.let { items ->
            // TODO Allow passing in of a sorting/filtering function
            val sortedItems = items
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.tier }))
                .filter { itemOverview ->
                    state.filterInput?.let { itemOverview.name.toLowerCase().contains(it.toLowerCase()) }?:true
                }
                .filter { itemOverview ->
                    if(state.isOnlyNotFound) {
                        itemOverview.status == "NOT_FOUND"
                    } else {
                        true
                    }
                }
            styledDiv {
                css {
                    width = 100.pct
                }
                styledUl {
                    css {
                        display = Display.table
                        paddingLeft = 0.px
                        paddingRight = 0.px
                        listStyleType = ListStyleType.none
                        width = 90.vw
                        margin = "auto"
                    }
                    for (item in sortedItems) {
                        styledLi {
                            css {
                                display = Display.table
                                marginLeft = 0.px
                                marginRight = 0.px
                                marginTop = .4.vh
                                marginBottom = .4.vh
                                paddingTop = .4.vh
                                paddingBottom = .4.vh
                                borderRadius = 1.vh
                                val alpha = when (item.tier) {
                                    "I" -> .5
                                    "II" -> .75
                                    "III" -> 1.0
                                    else -> .25
                                }
                                backgroundColor = when (item.status) {
                                    "NOT_FOUND" -> Color.maroon //Color.teal
                                    "FOUND" -> Color.gold //Color.gold
                                    "SUBMITTED" -> Color.green //Color.steelBlue
                                    "APPROVED" -> Color.lightGray
                                    else -> Color.white
                                }.withAlpha(alpha)
                                textAlign = TextAlign.center
                                width = 100.pct
                                fontSize = 3.vh
                            }
                            attrs {
                                onClickFunction = {
                                    props.onSelectItem(item)
                                }
                            }
                            key = item.id.toString()
                            span {
                                +item.name
                            }
                            +" "
                            styledSpan {
                                css(ScavengenerdStyles.smallerText)
                                +item.status
                            }
                        }
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        val mainScope = MainScope()
        mainScope.launch {
            val fetchedItems = fetchItems()
            setState {
                items = fetchedItems
            }
        }
    }
}

suspend fun fetchItems(): List<ItemOverview> {
    val response = window
        .fetch("${rootUrl}/items")
        .await()
        .json()
        .await()
    return (response as Array<ItemOverview>).toList()
}

fun RBuilder.itemList(handler: ItemListProps.() -> Unit): ReactElement {
    return child(ItemList::class) {
        this.attrs(handler)
    }
}