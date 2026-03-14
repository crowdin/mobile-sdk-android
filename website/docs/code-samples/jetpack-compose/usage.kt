import com.crowdin.platform.compose.crowdinString
import com.crowdin.platform.compose.crowdinPluralString

@Composable
fun WelcomeScreen() {
    // Basic usage
    Text(text = crowdinString(R.string.welcome_message))

    // Usage with arguments
    Text(text = crowdinString(R.string.welcome_user, "User"))

    // Plural usage
    Text(text = crowdinPluralString(R.plurals.task_count, 3, 3))
}
