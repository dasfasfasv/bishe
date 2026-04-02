package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.AccessAuthorizationDTO;
import com.example.server.entity.AccessAuthorization;

import java.util.List;

/**
 * 门禁授权Service接口
 */
public interface AccessAuthorizationService extends IService<AccessAuthorization> {
    /**
     * 生成门禁凭证
     */
    Long generateAccessCredential(AccessAuthorizationDTO dto);

    /**
     * 查询授权信息
     */
    AccessAuthorization getAuthorizationById(Long id);

    /**
     * 根据二维码查询授权
     */
    AccessAuthorization getAuthorizationByQRCode(String qrCode);

    /**
     * 撤销授权
     */
    void revokeAuthorization(Long id);

    /**
     * 查询访客的所有有效授权
     */
    List<AccessAuthorization> listValidAuthorizations(Long visitorId);

    /**
     * 验证访问权限
     */
    boolean validateAccess(String qrCode, String areaCode);
}
