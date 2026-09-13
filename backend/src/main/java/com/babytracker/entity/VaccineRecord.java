package com.babytracker.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("vaccine_record")
public class VaccineRecord {
    private Long id;
    private Long babyId;
    private String vaccineName;
    private LocalDate plannedDate;
    private Boolean completed;
    /** 提醒分类（已接种/待接种/已逾期），由服务端实时计算，不持久化 */
    @TableField(exist = false)
    private String status;
}
