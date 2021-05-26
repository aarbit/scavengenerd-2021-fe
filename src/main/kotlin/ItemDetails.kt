import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.files.Blob
import org.w3c.files.get
import org.w3c.xhr.FormData
import react.*
import react.dom.p
import styled.css
import styled.styledButton
import styled.styledLabel
import styled.styledSpan

external interface ItemDetailsProps: RProps {
    var item: ItemOverview
    var onClearSelection: () -> Unit
    var userName: String
}

external interface ItemDetailsState: RState {
    var details: ItemDetail?
    var uploading: Boolean
    var checkedPhotos: MutableList<Int>
}

@JsExport
class ItemDetails: RComponent<ItemDetailsProps, ItemDetailsState>() {
    override fun RBuilder.render() {
        styledLabel {
            css(ScavengenerdStyles.pushButton)
            styledButton {
                css(ScavengenerdStyles.goAwayThing)
                attrs {
                    onClickFunction = {
                        props.onClearSelection()
                    }

                }
            }
            +"<- back"
        }
        state.details?.let { itemDetail ->
            p {
                +itemDetail.name
                +" "
                styledSpan {
                    css(ScavengenerdStyles.smallerText)
                    +itemDetail.status
                }

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
                    onCheckPhoto = { checkbox, entry ->
                        setState {
                            if (checkbox.checked) {
                                checkedPhotos.add(entry.id)
                            }
                            else {
                                checkedPhotos.remove(entry.id)
                            }
                        }
                    }
                    onSubmit = {
                        val mainScope = MainScope()
                        mainScope.launch {
                            val headers = Headers()
                            headers.append("Content-Type", "application/json")
                            val isOK = window.fetch(
                                "${rootUrl}/entries",
                                RequestInit(method = "PATCH", body = state.checkedPhotos, headers = headers)
                            )
                                .await()
                                .ok
                            if(isOK) {
                                for (entryId in state.checkedPhotos) {
                                    state.details?.let { itemDetail ->
                                        val entryIndex = itemDetail.entries.indexOfFirst { it.id == entryId }
                                        val entry = itemDetail.entries.get(entryIndex)
                                        entry.status = "SUBMITTED"
                                        setState {
                                            itemDetail.entries[entryIndex] = entry
                                        }
                                    }

                                }
                            }
                        }
                    }
                    onApprove = {
                        for(entryId in state.checkedPhotos) {
                            val formData = FormData()
                            formData.append("status", "APPROVED")
                            val mainScope = MainScope()
                            mainScope.launch {
                                val isOK = window.fetch(
                                    "${rootUrl}/entry/${entryId}",
                                    RequestInit(method = "PATCH", body = formData)
                                )
                                    .await()
                                    .ok
                                if(isOK) {
                                    state.details?.let { itemDetail ->
                                        val entryIndex = itemDetail.entries.indexOfFirst { it.id == entryId }
                                        val entry = itemDetail.entries.get(entryIndex)
                                        entry.status = "APPROVED"
                                        setState {
                                            itemDetail.entries[entryIndex] = entry
                                        }
                                    }
                                }
                            }
                        }
                    }
                    onDelete = {
                        for(entryId in state.checkedPhotos) {
                            val mainScope = MainScope()
                            mainScope.launch {
                                val isOK = window.fetch(
                                    "${rootUrl}/entry/${entryId}",
                                    RequestInit(method = "DELETE")
                                )
                                    .await()
                                    .ok
                                if(isOK) {
                                    state.details?.let { itemDetail ->
                                        val entryIndex = itemDetail.entries.indexOfFirst { it.id == entryId }
                                        setState {
                                            itemDetail.entries = itemDetail.entries.removeAt(entryIndex)
                                        }
                                    }
                                }
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
            val itemDetail = fetchItemDetail(props.item.id)
            setState {
                details = itemDetail
            }
        }
        setState {
            checkedPhotos = mutableListOf()
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

fun <T: Any> Array<T>.removeAt(index: Int): Array<T> {
    val list = mutableListOf<T>()
    for (i in 0 until this.size) {
        if(i != index) {
            list.add(this[i])
        }
    }
    return list.toTypedArray()
}

fun RBuilder.itemDetails(handler: ItemDetailsProps.() -> Unit): ReactElement {
    return child(ItemDetails::class) {
        this.attrs(handler)
    }
}