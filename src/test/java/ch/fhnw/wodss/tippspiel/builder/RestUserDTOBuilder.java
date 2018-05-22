package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestUserDTO;

public class RestUserDTOBuilder {
    private RestUserDTO user;

    public RestUserDTOBuilder() {
        this.user = new RestUserDTO();
    }

    public RestUserDTOBuilder withName(String name) {
        user.setName(name);
        return this;
    }

    public RestUserDTOBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public RestUserDTOBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public RestUserDTOBuilder withReminders(boolean reminders) {
        user.setReminders(reminders);
        return this;
    }

    public RestUserDTOBuilder withDailyResults(boolean dailyResults) {
        user.setDailyResults(dailyResults);
        return this;
    }

    public RestUserDTO build() {
        return user;
    }
}
