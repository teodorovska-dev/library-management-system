package com.library.management.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardTrendDto {

    private long value;
    private String trend;
    private Integer change;
}