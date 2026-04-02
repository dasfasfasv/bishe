package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.dto.AccessAuthorizationDTO;
import com.example.server.entity.AccessAuthorization;
import com.example.server.exception.BusinessException;
import com.example.server.mapper.AccessAuthorizationMapper;
import com.example.server.service.AccessAuthorizationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 门禁授权Service实现类
 */
@Service
public class AccessAuthorizationServiceImpl extends ServiceImpl<AccessAuthorizationMapper, AccessAuthorization> implements AccessAuthorizationService {

    @Override
    public Long generateAccessCredential(AccessAuthorizationDTO dto) {
        AccessAuthorization auth = new AccessAuthorization();
        auth.setAppointmentId(dto.getAppointmentId());
        auth.setVisitorId(dto.getVisitorId());
        auth.setAccessCard(generateAccessCard());
        auth.setQrCode(generateQRCode());
        auth.setAllowedAreas(dto.getAllowedAreas());
        auth.setEffectiveTime(dto.getEffectiveTime());
        auth.setExpiryTime(dto.getExpiryTime());
        auth.setStatus("1");
        auth.setCreateTime(LocalDateTime.now());
        auth.setUpdateTime(LocalDateTime.now());

        save(auth);
        return auth.getId();
    }

    @Override
    public AccessAuthorization getAuthorizationById(Long id) {
        return getById(id);
    }

    @Override
    public AccessAuthorization getAuthorizationByQRCode(String qrCode) {
        QueryWrapper<AccessAuthorization> wrapper = new QueryWrapper<>();
        wrapper.eq("qr_code", qrCode);
        return getOne(wrapper);
    }

    @Override
    public void revokeAuthorization(Long id) {
        AccessAuthorization auth = getById(id);
        if (auth == null) {
            throw new BusinessException("授权不存在");
        }
        auth.setStatus("0");
        auth.setUpdateTime(LocalDateTime.now());
        updateById(auth);
    }

    @Override
    public List<AccessAuthorization> listValidAuthorizations(Long visitorId) {
        QueryWrapper<AccessAuthorization> wrapper = new QueryWrapper<>();
        wrapper.eq("visitor_id", visitorId)
                .eq("status", "1")
                .ge("expiry_time", LocalDateTime.now());
        return list(wrapper);
    }

    @Override
    public boolean validateAccess(String qrCode, String areaCode) {
        AccessAuthorization auth = getAuthorizationByQRCode(qrCode);
        if (auth == null || "0".equals(auth.getStatus())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auth.getEffectiveTime()) || now.isAfter(auth.getExpiryTime())) {
            return false;
        }

        // 检查区域权限
        if (auth.getAllowedAreas() != null && !auth.getAllowedAreas().contains(areaCode)) {
            return false;
        }

        return true;
    }

    private String generateAccessCard() {
        return "AC" + System.currentTimeMillis();
    }

    private String generateQRCode() {
        return UUID.randomUUID().toString();
    }
}
