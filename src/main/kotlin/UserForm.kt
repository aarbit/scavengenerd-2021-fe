import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.input
import styled.styledButton

@JsExport
class UserForm: RComponent<UserFormProps, RState>() {
    override fun RBuilder.render() {
        input {
            attrs {
                type = InputType.text
                value = props.userName
                onChangeFunction = {
                    setState {
                        props.onUserNameChange((it.target as HTMLInputElement).value)
                    }
                }
            }
        }
        styledButton {
            attrs {
                onClickFunction = {
                    setState {
                        props.onSaveUserName()
                    }
                }
            }
            +"Set Name"
        }
    }
}

external interface UserFormProps: RProps {
    var userName: String
    var onUserNameChange: (String) -> Unit
    var onSaveUserName: () -> Unit
}

fun RBuilder.userForm(handler: UserFormProps.() -> Unit): ReactElement {
    return child(UserForm::class) {
        this.attrs(handler)
    }
}