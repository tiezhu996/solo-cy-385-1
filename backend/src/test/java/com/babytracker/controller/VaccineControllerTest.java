package com.babytracker.controller;

import com.babytracker.entity.VaccineRecord;
import com.babytracker.mapper.VaccineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class VaccineControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private VaccineMapper mapper;

    @BeforeEach
    void setUp() {
        mapper.delete(null);
        insert(1L, "乙肝疫苗", LocalDate.now().minusMonths(6), true);
        insert(1L, "脊灰疫苗", LocalDate.now().minusDays(10), false);
        insert(1L, "麻腮风疫苗", LocalDate.now().plusDays(30), false);
        insert(2L, "卡介苗", LocalDate.now().plusDays(5), false);
    }

    private void insert(Long babyId, String name, LocalDate plannedDate, boolean completed) {
        VaccineRecord record = new VaccineRecord();
        record.setBabyId(babyId);
        record.setVaccineName(name);
        record.setPlannedDate(plannedDate);
        record.setCompleted(completed);
        mapper.insert(record);
    }

    @Test
    void getWithoutBabyIdReturnsAllRecordsWithStatus() throws Exception {
        mvc.perform(get("/api/vaccines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].status", hasItems("已接种", "待接种", "已逾期")))
                .andExpect(jsonPath("$[*].plannedDate", everyItem(notNullValue())));
    }

    @Test
    void getWithBabyIdReturnsOnlyThatBaby() throws Exception {
        mvc.perform(get("/api/vaccines").param("babyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].babyId", everyItem(is(1))));
    }

    @Test
    void getWithUnknownBabyIdReturnsEmptyList() throws Exception {
        mvc.perform(get("/api/vaccines").param("babyId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void postVaccineStillSaves() throws Exception {
        String body = "{\"babyId\":2,\"vaccineName\":\"百白破疫苗\",\"plannedDate\":\""
                + LocalDate.now().plusDays(60) + "\",\"completed\":false}";
        mvc.perform(post("/api/vaccines").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaccineName", is("百白破疫苗")));
        mvc.perform(get("/api/vaccines").param("babyId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
