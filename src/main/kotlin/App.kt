import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import react.*
import react.dom.p

@JsExport
class App : RComponent<RProps, AppState>() {
    override fun RBuilder.render() {
        p {
            +"ScavengeNerd 2021"
            userForm {
                userName = state.userName
                onUserNameChange = { newName ->
                    setState {
                        userName = newName
                    }
                }
                onSaveUserName = {
                    localStorage.setItem("userName", state.userName)
                }
            }
        }
        itemList {
            items = state.items
            onSelectItem = { item ->
                setState {
                    currentItem = item
                }
            }
        }
        state.currentItem?.let { selectedItem ->
            itemDetails {
                userName = state.userName
                item = selectedItem
                onClearSelection = {
                    val mainScope = MainScope()
                    mainScope.launch {
                        val fetchedItems = fetchItems()
                        setState {
                            items = fetchedItems
                        }
                    }
                    setState {
                        currentItem = null
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        items = listOf()

        val mainScope = MainScope()
        mainScope.launch {
            val fetchedItems = fetchItems()
            setState {
                items = fetchedItems
            }
        }
        userName = localStorage.getItem("userName")?:"sss"
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

external interface AppState : RState {
    var items: List<ItemOverview>
    var currentItem: ItemOverview?
    var userName: String
}

val rootUrl = "http://localhost:5000"
//val rootUrl = "http://scavengenerd2021-dev.eba-sswuq2m4.us-east-1.elasticbeanstalk.com"