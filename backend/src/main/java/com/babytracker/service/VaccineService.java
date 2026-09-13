package com.babytracker.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.babytracker.constants.BabyEnums;
import com.babytracker.entity.VaccineRecord;
import com.babytracker.mapper.VaccineMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class VaccineService {
    private final VaccineMapper mapper;
    public VaccineService(VaccineMapper mapper) { this.mapper = mapper; }

    public List<VaccineRecord> schedule() {
        return withStatus(mapper.selectList(null));
    }

    /** 按宝宝查询疫苗计划，按计划日期升序；该宝宝没有记录时返回空列表 */
    public List<VaccineRecord> scheduleByBaby(Long babyId) {
        QueryWrapper<VaccineRecord> query = new QueryWrapper<VaccineRecord>()
                .eq("baby_id", babyId)
                .orderByAsc("planned_date");
        return withStatus(mapper.selectList(query));
    }

    public VaccineRecord save(VaccineRecord record) { mapper.insert(record); return record; }

    /** 根据计划日期与接种完成情况实时计算提醒分类 */
    private List<VaccineRecord> withStatus(List<VaccineRecord> records) {
        LocalDate today = LocalDate.now();
        for (VaccineRecord record : records) {
            record.setStatus(resolveStatus(record, today));
        }
        return records;
    }

    private String resolveStatus(VaccineRecord record, LocalDate today) {
        if (Boolean.TRUE.equals(record.getCompleted())) {
            return BabyEnums.VACCINE_STATUS_DONE;
        }
        if (record.getPlannedDate() != null && record.getPlannedDate().isBefore(today)) {
            return BabyEnums.VACCINE_STATUS_OVERDUE;
        }
        return BabyEnums.VACCINE_STATUS_PENDING;
    }
}
