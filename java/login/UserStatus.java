package login;
// UserStatus enum
// possible states for userid+password combination

public enum UserStatus {
	DOES_NOT_EXIST, EXISTS_INCORRECT_PASSWORD, EXISTS_CORRECT_PASSWORD;
}
