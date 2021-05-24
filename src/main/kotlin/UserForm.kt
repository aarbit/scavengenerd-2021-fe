import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.input
import styled.styledButton

external interface UserFormProps: RProps {
    var userName: String
    var onUserNameChange: (String) -> Unit
    var onSaveUserName: () -> Unit
}

@JsExport
class UserForm: RComponent<UserFormProps, RState>() {
    override fun RBuilder.render() {
        input {
            attrs {
                type = InputType.text
                value = props.userName
                onChangeFunction = { event ->
                    props.onUserNameChange((event.target as HTMLInputElement).value)
                }
            }
        }
        styledButton {
            attrs {
                onClickFunction = {
                    props.onSaveUserName()
                }
            }
            +"Set Name"
        }
    }
}

fun RBuilder.userForm(handler: UserFormProps.() -> Unit): ReactElement {
    return child(UserForm::class) {
        this.attrs(handler)
    }
}