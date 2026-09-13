package com.babytracker.service;

import com.babytracker.constants.BabyEnums;
import com.babytracker.entity.VaccineRecord;
import com.babytracker.mapper.VaccineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VaccineServiceTest {

    @Autowired
    private VaccineService service;
    @Autowired
    private VaccineMapper mapper;

    private static final Long BABY_ONE = 1L;
    private static final Long BABY_TWO = 2L;

    @BeforeEach
    void setUp() {
        mapper.delete(null);
        insert(BABY_ONE, "乙肝疫苗", LocalDate.now().minusMonths(6), true);
        insert(BABY_ONE, "脊灰疫苗", LocalDate.now().minusDays(10), false);
        insert(BABY_ONE, "麻腮风疫苗", LocalDate.now().plusDays(30), false);
        insert(BABY_TWO, "卡介苗", LocalDate.now().plusDays(5), false);
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
    void scheduleWithoutBabyIdReturnsAllRecords() {
        List<VaccineRecord> all = service.schedule();
        assertEquals(4, all.size());
    }

    @Test
    void scheduleByBabyReturnsOnlyThatBaby() {
        List<VaccineRecord> records = service.scheduleByBaby(BABY_ONE);
        assertEquals(3, records.size());
        assertTrue(records.stream().allMatch(r -> BABY_ONE.equals(r.getBabyId())));
    }

    @Test
    void scheduleByBabyOrdersByPlannedDateAsc() {
        List<VaccineRecord> records = service.scheduleByBaby(BABY_ONE);
        for (int i = 1; i < records.size(); i++) {
            assertFalse(records.get(i).getPlannedDate().isBefore(records.get(i - 1).getPlannedDate()));
        }
    }

    @Test
    void scheduleByBabyReturnsEmptyWhenNoPlan() {
        assertTrue(service.scheduleByBaby(999L).isEmpty());
    }

    @Test
    void statusIsResolvedFromCompletionAndPlannedDate() {
        List<VaccineRecord> records = service.scheduleByBaby(BABY_ONE);
        assertEquals(BabyEnums.VACCINE_STATUS_DONE, statusOf(records, "乙肝疫苗"));
        assertEquals(BabyEnums.VACCINE_STATUS_OVERDUE, statusOf(records, "脊灰疫苗"));
        assertEquals(BabyEnums.VACCINE_STATUS_PENDING, statusOf(records, "麻腮风疫苗"));
    }

    @Test
    void saveStillInsertsRecord() {
        VaccineRecord record = new VaccineRecord();
        record.setBabyId(BABY_TWO);
        record.setVaccineName("百白破疫苗");
        record.setPlannedDate(LocalDate.now().plusDays(60));
        record.setCompleted(false);
        service.save(record);
        assertEquals(2, service.scheduleByBaby(BABY_TWO).size());
    }

    private String statusOf(List<VaccineRecord> records, String vaccineName) {
        return records.stream()
                .filter(r -> vaccineName.equals(r.getVaccineName()))
                .findFirst()
                .orElseThrow()
                .getStatus();
    }
}
