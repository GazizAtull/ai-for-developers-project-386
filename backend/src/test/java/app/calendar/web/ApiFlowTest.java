package app.calendar.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Дымовой тест и сквозной сценарий: тип встречи, слоты, запись, конфликт, список владельца. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiFlowTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  @Test
  void appStartsAndAnswers() throws Exception {
    mvc.perform(get("/api/event-types")).andExpect(status().isOk());
  }

  @Test
  void guestBooksSlotAndOwnerSeesIt() throws Exception {
    String created =
        mvc.perform(
                post("/api/event-types")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"title\":\"Интро\",\"description\":\"15 минут\",\"durationMinutes\":15}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String typeId = json.readTree(created).get("id").asText();

    String availability =
        mvc.perform(get("/api/event-types/" + typeId + "/availability"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.timezone").value("Asia/Almaty"))
            .andExpect(jsonPath("$.days", hasSize(14)))
            .andReturn()
            .getResponse()
            .getContentAsString();
    String start = null;
    for (JsonNode day : json.readTree(availability).get("days")) {
      if (day.get("slots").size() > 0) {
        start = day.get("slots").get(0).get("start").asText();
        break;
      }
    }
    String body =
        "{\"eventTypeId\":\""
            + typeId
            + "\",\"start\":\""
            + start
            + "\",\"guestName\":\"Аня\",\"guestEmail\":\"anya@example.com\"}";

    mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.eventTypeTitle").value("Интро"));
    mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("SLOT_TAKEN"));
    mvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].guestName").value("Аня"));
  }

  @Test
  void errorsFollowContract() throws Exception {
    mvc.perform(
            post("/api/event-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"durationMinutes\":30}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    mvc.perform(get("/api/event-types/00000000-0000-0000-0000-000000000000/availability"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("EVENT_TYPE_NOT_FOUND"));
  }
}
