import com.crowdin.platform.compose.crowdinString

@Composable
fun WelcomeScreen() {
    // Basic usage
    Text(text = crowdinString(R.string.welcome_message))

    // Usage with arguments
    Text(text = crowdinString(R.string.welcome_user, "User"))
}
