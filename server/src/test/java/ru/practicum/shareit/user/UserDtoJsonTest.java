package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoTest() throws IOException {
        // сериализация
        UserDto userDto = UserDto.builder().id(1L).name("user1").email("email1@mail.ru").build();
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email1@mail.ru");

        // десериализация
        String content = "{\"id\":\"1\",\"name\":\"user1\",\"email\":\"email1@mail.ru\"}";
        assertThat(json.parse(content)).isEqualTo(userDto);
    }
}
