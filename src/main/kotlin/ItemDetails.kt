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
import kotlin.js.Date

@JsExport
class ItemDetails: RComponent<ItemDetailsProps, ItemDetailsState>() {
    override fun RBuilder.render() {
        p{
            +"${Date.now()}"
        }

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
                    window.fetch("http://localhost:8081/item/${props.item.id}", RequestInit(method = "POST", body = formData))
                    rerender()
                }
            }
            if(item.entries.isNotEmpty()) {
                entryDetails {
                    itemId = item.id
                    entries = item.entries.toList()
                }
            }
        }

    }

    override fun componentDidMount() {
        setState {
            renderTrigger = ::rerender
        }
        val mainScope = MainScope()
        mainScope.launch {
            val itemDetail = fetchItemDetail(props.item.id)
            setState {
                details = itemDetail
            }
        }
    }

    fun rerender() {
        this.forceUpdate()
    }
}

external interface ItemDetailsProps: RProps {
    var item: ItemOverview
    var onClearSelection: () -> Unit
    var userName: String
}

external interface ItemDetailsState: RState {
    var details: ItemDetail?
    var renderTrigger: () -> Unit
}

fun RBuilder.itemDetails(handler: ItemDetailsProps.() -> Unit): ReactElement {
    return child(ItemDetails::class) {
        this.attrs(handler)
    }
}

suspend fun fetchItemDetail(id: Int): ItemDetail {
    val response = window
        .fetch("http://localhost:8081/item/$id")
        .await()
        .json()
        .await()
    return response as ItemDetail
}