import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import org.w3c.files.Blob
import org.w3c.files.get
import org.w3c.xhr.FormData
import react.*
import react.dom.p
import styled.styledButton

@JsExport
class ItemDetails: RComponent<ItemDetailsProps, ItemDetailsState>() {
    override fun RBuilder.render() {
        styledButton {
            attrs {
                onClickFunction = {
                    props.onClearSelection()
                }
                +"X"
            }
        }
        state.details?.let { item ->
            p {
                +item.name
                +item.tier
            }
            entryForm {
                onSelectImage = {
                    val inputElement = it.target as HTMLInputElement
                    val formData = FormData()
                    formData.append("photo", inputElement.files?.get(0) as Blob, inputElement.value)
                    formData.append("userName", props.userName)
                    val mainScope = MainScope()
                    mainScope.launch {
                        val response = window.fetch("${rootUrl}/item/${props.item.id}", RequestInit(method = "POST", body = formData))
                            .await()
                            .json()
                            .await()
                        setState {
                            item.entries[item.entries.size] = response as ItemEntryDetail
                        }
                    }
                }
            }
            if(item.entries.isNotEmpty()) {
                entryDetails {
                    itemId = item.id
                    entries = item.entries.toMutableList()
                }
            }
        }

    }

    override fun componentDidMount() {
        val mainScope = MainScope()
        mainScope.launch {
            val itemDetail = fetchItemDetail(props.item.id)
            setState {
                details = itemDetail
            }
        }
    }
}

external interface ItemDetailsProps: RProps {
    var item: ItemOverview
    var onClearSelection: () -> Unit
    var userName: String
}

external interface ItemDetailsState: RState {
    var details: ItemDetail?
}

fun RBuilder.itemDetails(handler: ItemDetailsProps.() -> Unit): ReactElement {
    return child(ItemDetails::class) {
        this.attrs(handler)
    }
}

suspend fun fetchItemDetail(id: Int): ItemDetail {
    val response = window
        .fetch("${rootUrl}/item/$id")
        .await()
        .json()
        .await()
    return response as ItemDetail
}