import kotlinx.browser.localStorage
import kotlinx.css.*
import react.*
import react.dom.p
import styled.css
import styled.styledDiv
import styled.styledP

external interface AppState : RState {
    var currentItem: ItemOverview?
    var userName: String
}

@JsExport
class App : RComponent<RProps, AppState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                fontFamily = "sans-serif"
                fontSize = 3.vh
            }
            styledP {
                css {
                    textAlign = TextAlign.center
                }
                +"ScavengeNerd 2021"
           }
            styledDiv {
                css {
                    float = Float.right
                }
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

            state.currentItem?.let { selectedItem ->
                itemDetails {
                    userName = state.userName
                    item = selectedItem
                    onClearSelection = {
                        setState {
                            currentItem = null
                        }
                    }
                }
            } ?: itemList {
                onSelectItem = { item ->
                    setState {
                        currentItem = item
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        userName = localStorage.getItem("userName")?:""
    }
}

//val rootUrl = "http://localhost:5000"
val rootUrl = "http://scavengenerd2021-dev.eba-sswuq2m4.us-east-1.elasticbeanstalk.com"