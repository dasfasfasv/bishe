package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.VO.ApprovalVO;
import com.example.server.entity.Approval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 审批 Mapper
 */
@Mapper
public interface ApprovalMapper extends BaseMapper<Approval> {

    /**
     * 查询审批详情（包含关联信息）
     */
    @Select({
            "SELECT ",
            "  a.id, a.appointment_id, a.status, a.remark, a.approval_time, ",
            "  a.approver_user_id, a.approver_name, a.approval_level, ",
            "  a.create_time, a.update_time, ",
            "  ap.visit_reason, ap.appointment_time, ap.appointment_end_time, ",
            "  ap.approval_remark, ",
            "  v.id as visitor_id, v.name as visitor_name, v.phone as visitor_phone, ",
            "  v.email as visitor_email, v.id_card as visitor_id_card, ",
            "  v.category as visitor_category, v.credit_level as credit_level, ",
            "  d.id as department_id, d.dept_name as department_name ",
            "FROM approval a ",
            "LEFT JOIN appointment ap ON a.appointment_id = ap.id ",
            "LEFT JOIN visitor v ON ap.visitor_id = v.id ",
            "LEFT JOIN sys_department d ON ap.department_id = d.id ",
            "WHERE a.id = #{id}"
    })
    ApprovalVO selectApprovalVO(Long id);

    /**
     * 查询所有待审批的预约（包含关联信息）
     */
    @Select({
            "SELECT ",
            "  a.id, a.appointment_id, a.status, a.remark, a.approval_time, ",
            "  a.approver_user_id, a.approver_name, a.approval_level, ",
            "  ap.visit_reason, ap.appointment_time, ap.appointment_end_time, ",
            "  v.id as visitor_id, v.name as visitor_name, v.phone as visitor_phone, ",
            "  v.email as visitor_email, v.id_card as visitor_id_card, ",
            "  d.id as department_id, d.dept_name as department_name ",
            "FROM approval a ",
            "LEFT JOIN appointment ap ON a.appointment_id = ap.id ",
            "LEFT JOIN visitor v ON ap.visitor_id = v.id ",
            "LEFT JOIN sys_department d ON ap.department_id = d.id ",
            "WHERE a.status = '1' ",
            "ORDER BY a.create_time DESC"
    })
    List<ApprovalVO> selectAllPendingApprovals();
}
