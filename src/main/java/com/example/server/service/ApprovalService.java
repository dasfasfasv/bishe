package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.VO.ApprovalStatisticsVO;
import com.example.server.VO.ApprovalVO;
import com.example.server.dto.ApprovalDTO;
import com.example.server.dto.ApprovalPageDTO;
import com.example.server.entity.Approval;

import java.util.List;

/**
 * 审批 Service 接口
 */
public interface ApprovalService extends IService<Approval> {

    /**
     * 查询待审批列表（当前用户）
     *
     * @param userId 用户ID
     * @return 待审批列表
     */
    List<ApprovalVO> listPendingApprovals(Long userId);

    /**
     * 分页查询审批列表
     *
     * @param pageDTO 分页参数
     * @return 审批列表
     */
    List<ApprovalVO> listApprovalsByPage(ApprovalPageDTO pageDTO);

    /**
     * 查询审批详情
     *
     * @param id 审批ID
     * @return 审批详情
     */
    ApprovalVO getApprovalDetail(Long id);

    /**
     * 查询预约的审批历史
     *
     * @param appointmentId 预约ID
     * @return 审批历史列表
     */
    List<ApprovalVO> getApprovalHistory(Long appointmentId);

    /**
     * 批准预约
     *
     * @param appointmentId 预约ID
     * @param remark 批准意见
     * @param userId 审批人ID
     */
    void approveAppointment(Long appointmentId, String remark, Long userId);

    /**
     * 拒绝预约
     *
     * @param appointmentId 预约ID
     * @param remark 拒绝原因
     * @param userId 审批人ID
     */
    void rejectAppointment(Long appointmentId, String remark, Long userId);

    /**
     * 退回预约
     *
     * @param appointmentId 预约ID
     * @param remark 退回原因
     * @param userId 审批人ID
     */
    void returnAppointment(Long appointmentId, String remark, Long userId);

    /**
     * 获取审批统计数据
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    ApprovalStatisticsVO getApprovalStatistics(Long userId);

    /**
     * 创建审批流程
     *
     * @param appointmentId 预约ID
     */
    void createApprovalWorkflow(Long appointmentId);

    /**
     * 批量审批
     *
     * @param appointmentIds 预约ID列表
     * @param status 审批状态
     * @param remark 备注
     * @param userId 审批人ID
     */
    void batchApprove(List<Long> appointmentIds, String status, String remark, Long userId);
}
