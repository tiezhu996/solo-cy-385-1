package com.babytracker.constants;

public final class BabyEnums {
    public static final String[] VACCINE_TYPES = {"乙肝疫苗", "卡介苗", "脊灰疫苗", "百白破疫苗", "麻腮风疫苗"};
    public static final String[] MONTH_STAGES = {"6-8个月", "9-12个月", "13-18个月", "19-36个月"};

    /** 疫苗提醒分类：已接种 */
    public static final String VACCINE_STATUS_DONE = "已接种";
    /** 疫苗提醒分类：待接种（未到计划日期） */
    public static final String VACCINE_STATUS_PENDING = "待接种";
    /** 疫苗提醒分类：已逾期（超过计划日期仍未接种） */
    public static final String VACCINE_STATUS_OVERDUE = "已逾期";
    private BabyEnums() {}
}
