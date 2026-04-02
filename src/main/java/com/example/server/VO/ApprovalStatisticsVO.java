package com.example.server.VO;

import lombok.Data;

/**
 * 审批统计信息 VO
 */
@Data
public class ApprovalStatisticsVO {
    private Integer pendingCount;  // 待审批数
    private Integer approvedCount;  // 已批准数
    private Integer rejectedCount;  // 已拒绝数
    private Integer totalCount;  // 总数
    private Double approvalRate;  // 批准率
    private Double rejectRate;  // 拒绝率
    private Long avgApprovalTime;  // 平均审批时间（分钟）
}
