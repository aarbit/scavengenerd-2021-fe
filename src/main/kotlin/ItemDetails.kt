import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.css.fontSize
import kotlinx.css.rem
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import org.w3c.files.Blob
import org.w3c.files.get
import org.w3c.xhr.FormData
import react.*
import react.dom.p
import styled.css
import styled.styledButton

external interface ItemDetailsProps: RProps {
    var item: ItemOverview
    var onClearSelection: () -> Unit
    var userName: String
}

external interface ItemDetailsState: RState {
    var details: ItemDetail?
    var uploading: Boolean
}

@JsExport
class ItemDetails: RComponent<ItemDetailsProps, ItemDetailsState>() {
    override fun RBuilder.render() {
        styledButton {
            css {
                fontSize = 2.rem
            }
            attrs {
                onClickFunction = {
                    props.onClearSelection()
                }
                +"<- back"
            }
        }
        state.details?.let { itemDetail ->
            p {
                +itemDetail.name
                +" "
                +itemDetail.status
            }
            entryForm {
                uploading = state.uploading
                onSelectImage = { fileInput ->
                    setState {
                        uploading = true
                    }
                    val formData = FormData()
                    formData.append("photo", fileInput.files?.get(0) as Blob, fileInput.value)
                    formData.append("userName", props.userName)
                    val mainScope = MainScope()
                    mainScope.launch {
                        val response = window.fetch("${rootUrl}/item/${props.item.id}", RequestInit(method = "POST", body = formData))
                            .await()
                            .json()
                            .await()
                        setState {
                            uploading = false
                            itemDetail.entries[itemDetail.entries.size] = response as ItemEntryDetail
                        }
                    }
                }
            }
            if(itemDetail.entries.isNotEmpty()) {
                entryDetails {
                    item = itemDetail
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

suspend fun fetchItemDetail(id: Int): ItemDetail {
    val response = window
        .fetch("${rootUrl}/item/$id")
        .await()
        .json()
        .await()
    return response as ItemDetail
}

fun RBuilder.itemDetails(handler: ItemDetailsProps.() -> Unit): ReactElement {
    return child(ItemDetails::class) {
        this.attrs(handler)
    }
}