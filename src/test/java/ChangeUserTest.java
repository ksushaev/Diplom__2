import all_for_user.ChecksForUser;
import all_for_user.CreateUser;
import all_for_user.DataForLoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;


public class ChangeUserTest {

    private ChecksForUser checksForUser = new ChecksForUser();
    CreateUser newUser = CreateUser.getUserWithRandomStringUtils();
    private final DataForLoginUser dataForLoginUser = DataForLoginUser.from(newUser);
    private String accessToken;

    @Before
    public void setUp() {
        checksForUser = new ChecksForUser();
        accessToken = "";
    }

    @Test
    @DisplayName("Изменить пароль, логин и имя пользователя c авторизацией")
    public void updateAllOfRealUser() {
        checksForUser.createUserWithOkStatus(newUser);
        ValidatableResponse response = checksForUser.autorizationOfUser(dataForLoginUser);
        accessToken = response.extract().path("accessToken");
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.ru");
        newUser.setName(RandomStringUtils.randomAlphabetic(10));
        newUser.setPassword(RandomStringUtils.randomAlphabetic(10));
        checksForUser.updateUser(dataForLoginUser, accessToken)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменить данные пользователя без авторизации")
    public void tryToUpdateDataWithoutAuthorization() {
        checksForUser.createUserWithOkStatus(newUser);
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.ru");
        newUser.setName(RandomStringUtils.randomAlphabetic(10));
        newUser.setPassword(RandomStringUtils.randomAlphabetic(10));
        checksForUser.updateUserWithoutAuthorization(newUser)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
    }

    @After
    public void delete() {
        checksForUser.deleteUser(accessToken);
    }
}

